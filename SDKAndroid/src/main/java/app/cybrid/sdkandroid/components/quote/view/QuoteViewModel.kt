package app.cybrid.sdkandroid.components.quote.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cybrid.cybrid_api_bank.client.apis.QuotesApi
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.cybrid_api_bank.client.models.PostQuoteBankModel
import app.cybrid.cybrid_api_bank.client.models.QuoteBankModel
import app.cybrid.sdkandroid.AppModule
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.core.AssetPipe
import app.cybrid.sdkandroid.core.BigDecimal
import app.cybrid.sdkandroid.util.Logger
import app.cybrid.sdkandroid.util.LoggerEvents
import app.cybrid.sdkandroid.util.getResult
import kotlinx.coroutines.launch

class QuoteViewModel: ViewModel() {

    // -- Customer GUID
    private val customerGuid = Cybrid.instance.customerGuid

    // -- Public quoteBankModel
    var quoteBankModel by mutableStateOf(QuoteBankModel())

    // -- Basic postQuoteBankModel object
    private var postQuoteBankModel = PostQuoteBankModel(
        customerGuid = customerGuid,
        symbol = "",
        side = PostQuoteBankModel.Side.buy
    )

    fun getQuoteObject(
        amount: BigDecimal,
        input: AssetBankModel.Type,
        side: PostQuoteBankModel.Side,
        asset: AssetBankModel,
        pairAsset: AssetBankModel
    ): PostQuoteBankModel  {

        // -- Symbol
        val symbol = "${asset.code}-${pairAsset.code}"

        // -- Check side
        when(side) {

            PostQuoteBankModel.Side.buy -> {

                if (input == AssetBankModel.Type.crypto) {
                    postQuoteBankModel = PostQuoteBankModel(
                        customerGuid = customerGuid,
                        symbol = symbol,
                        side = side,
                        receiveAmount = AssetPipe.transform(
                            value = amount,
                            asset = asset,
                            unit = "base"
                        ).toInt()
                    )
                } else {
                    postQuoteBankModel = PostQuoteBankModel(
                        customerGuid = customerGuid,
                        symbol = symbol,
                        side = side,
                        deliverAmount = AssetPipe.transform(
                            value = amount,
                            asset = pairAsset,
                            unit = "base"
                        ).toInt()
                    )
                }
            }

            PostQuoteBankModel.Side.sell -> {

                if (input == AssetBankModel.Type.fiat) {
                    postQuoteBankModel = PostQuoteBankModel(
                        customerGuid = customerGuid,
                        symbol = symbol,
                        side = side,
                        receiveAmount = AssetPipe.transform(
                            value = amount,
                            asset = pairAsset,
                            unit = "base"
                        ).toInt()
                    )
                } else {
                    postQuoteBankModel = PostQuoteBankModel(
                        customerGuid = customerGuid,
                        symbol = symbol,
                        side = side,
                        deliverAmount = AssetPipe.transform(
                            value = amount,
                            asset = asset,
                            unit = "base"
                        ).toInt()
                    )
                }
            }
        }

        // -- Return PostQuoteBankModel
        return this.postQuoteBankModel
    }

    fun getQuote(quoteObject: PostQuoteBankModel) {

        val quoteService = AppModule.getClient().createService(QuotesApi::class.java)
        viewModelScope.launch {

            val quoteResult = getResult { quoteService.createQuote(quoteObject) }
            quoteResult.let {

                quoteBankModel = if (it.code == 200) {
                    it.data!!
                } else {
                    Logger.log(LoggerEvents.DATA_ERROR, "Quote Confirmation Component - Data :: {${it.message}}")
                    QuoteBankModel()
                }
            }
        }
    }

}