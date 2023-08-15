package app.cybrid.sdkandroid.components.trade.view

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cybrid.cybrid_api_bank.client.apis.QuotesApi
import app.cybrid.cybrid_api_bank.client.apis.TradesApi
import app.cybrid.cybrid_api_bank.client.infrastructure.ApiClient
import app.cybrid.cybrid_api_bank.client.models.*
import app.cybrid.sdkandroid.AppModule
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.components.TradeView
import app.cybrid.sdkandroid.components.listprices.view.ListPricesViewModel
import app.cybrid.sdkandroid.core.AssetPipe
import app.cybrid.sdkandroid.util.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TradeViewModel: ViewModel() {

    private var quoteService = AppModule.getClient().createService(QuotesApi::class.java)
    private var tradeService = AppModule.getClient().createService(TradesApi::class.java)

    var uiState: MutableState<TradeView.ViewState> = mutableStateOf(TradeView.ViewState.LOADING)
    var uiModalState: MutableState<TradeView.QuoteModalViewState> = mutableStateOf(TradeView.QuoteModalViewState.LOADING)

    var currentFiatCurrency = ""
    var customerGuid = Cybrid.customerGuid

    var listPricesViewModel: ListPricesViewModel? = null
    var listPricesPolling: Polling? = null

    var currentAsset: MutableState<AssetBankModel?> = mutableStateOf(null)
    var currentPairAsset: MutableState<AssetBankModel?> = mutableStateOf(null)
    var postQuoteBankModel: PostQuoteBankModel? = null
    var showModalDialog: MutableState<Boolean> = mutableStateOf(false)

    var quoteBankModel: QuoteBankModel by mutableStateOf(QuoteBankModel())
    var quotePolling: Polling? = null

    var tradeBankModel: TradeBankModel by mutableStateOf(TradeBankModel())

    // -- Warning Modal
    var showKYCWarningModal: MutableState<Boolean> = mutableStateOf(false)

    fun setDataProvider(dataProvider: ApiClient) {

        quoteService = dataProvider.createService(QuotesApi::class.java)
        tradeService = dataProvider.createService(TradesApi::class.java)
    }

    suspend fun getPricesList() {

        listPricesViewModel?.getPricesList()
        if (listPricesViewModel?.prices?.isNotEmpty() == true) {

            uiState.value = TradeView.ViewState.LIST_PRICES
            listPricesPolling = Polling {
                viewModelScope.launch { listPricesViewModel?.getPricesList() }
            }
        }
    }

    fun handlePricesOnClick(asset: AssetBankModel, pairAsset: AssetBankModel) {

        currentAsset.value = asset
        currentPairAsset.value = pairAsset
        uiState.value = TradeView.ViewState.QUOTE_CONTENT
    }

    fun createPostQuote(
        amountState: MutableState<String>,
        typeOfAmountState: MutableState<AssetBankModel.Type>,
        side: PostQuoteBankModel.Side,
        asset: AssetBankModel,
        pairAsset: AssetBankModel,
    ) {

        // -- Symbol
        val symbol = "${asset.code}-${pairAsset.code}"

        // -- Check side
        when(side) {

            PostQuoteBankModel.Side.buy -> {

                if (typeOfAmountState.value == AssetBankModel.Type.crypto) {
                    postQuoteBankModel = PostQuoteBankModel(
                        customerGuid = customerGuid,
                        symbol = symbol,
                        side = side,
                        receiveAmount = AssetPipe.transform(
                            value = amountState.value,
                            asset = asset,
                            unit = AssetPipe.AssetPipeBase
                        ).toJavaBigDecimal()
                    )
                } else {
                    postQuoteBankModel = PostQuoteBankModel(
                        customerGuid = customerGuid,
                        symbol = symbol,
                        side = side,
                        deliverAmount = AssetPipe.transform(
                            value = amountState.value,
                            asset = pairAsset,
                            unit = AssetPipe.AssetPipeBase
                        ).toJavaBigDecimal()
                    )
                }
            }

            PostQuoteBankModel.Side.sell -> {

                if (typeOfAmountState.value == AssetBankModel.Type.fiat) {
                    postQuoteBankModel = PostQuoteBankModel(
                        customerGuid = customerGuid,
                        symbol = symbol,
                        side = side,
                        receiveAmount = AssetPipe.transform(
                            value = amountState.value,
                            asset = pairAsset,
                            unit = AssetPipe.AssetPipeBase
                        ).toJavaBigDecimal()
                    )
                } else {
                    postQuoteBankModel = PostQuoteBankModel(
                        customerGuid = customerGuid,
                        symbol = symbol,
                        side = side,
                        deliverAmount = AssetPipe.transform(
                            value = amountState.value,
                            asset = asset,
                            unit = AssetPipe.AssetPipeBase
                        ).toJavaBigDecimal()
                    )
                }
            }

            else -> {}
        }

        // --
        this.showModalDialog.value = true
        this.uiModalState.value = TradeView.QuoteModalViewState.LOADING
    }

    suspend fun createQuote() {

        if (!Cybrid.invalidToken) {
            this.viewModelScope.let { scope ->
                val waitFor = scope.async {

                    val quoteResult = getResult { quoteService.createQuote(postQuoteBankModel!!) }
                    quoteResult.let {

                        val code = it.code ?: 500
                        if (isSuccessful(code)) {

                            quoteBankModel = it.data!!
                            uiModalState.value = TradeView.QuoteModalViewState.CONTENT
                            if (quotePolling == null && tradeBankModel.guid == null) {
                                quotePolling = Polling { viewModelScope.launch { createQuote() } }
                            }
                        } else if (code == 422) {
                            if (it.message == "unverified_customer") {
                                showKYCWarningModal.value = true
                                modalBeDismissed()
                            }
                        }
                    }
                }
                waitFor.await()
            }
        }
    }

    suspend fun createTrade() {

        quotePolling?.stop()
        quotePolling = null

        uiModalState.value = TradeView.QuoteModalViewState.LOADING_SUBMITTED
        val postTradeBankModel = PostTradeBankModel(
            quoteGuid = quoteBankModel.guid ?: ""
        )

        if (!Cybrid.invalidToken) {
            this.viewModelScope.let { scope ->
                val waitFor = scope.async {

                    val tradeResult = getResult { tradeService.createTrade(postTradeBankModel) }
                    tradeResult.let {
                        if (isSuccessful(it.code ?: 500)) {

                            tradeBankModel = it.data!!
                            uiModalState.value = TradeView.QuoteModalViewState.DONE
                        }
                    }
                }
                waitFor.await()
            }
        }
    }

    fun modalBeDismissed() {

        showModalDialog.value = false
        uiModalState.value = TradeView.QuoteModalViewState.LOADING

        quotePolling?.stop()
        quotePolling = null

        quoteBankModel = QuoteBankModel()
        tradeBankModel = TradeBankModel()
    }
}