package app.cybrid.sdkandroid.components.accounts.view

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cybrid.cybrid_api_bank.client.apis.*
import app.cybrid.cybrid_api_bank.client.infrastructure.ApiClient
import app.cybrid.cybrid_api_bank.client.models.AccountBankModel
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.cybrid_api_bank.client.models.TradeBankModel
import app.cybrid.sdkandroid.AppModule
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.components.AccountsView
import app.cybrid.sdkandroid.components.accounts.entity.AccountAssetPriceModel
import app.cybrid.sdkandroid.components.listprices.view.ListPricesViewModel
import app.cybrid.sdkandroid.core.AssetPipe
import app.cybrid.sdkandroid.core.AssetPipe.AssetPipeTrade
import app.cybrid.sdkandroid.core.BigDecimal
import app.cybrid.sdkandroid.core.BigDecimalPipe
import app.cybrid.sdkandroid.util.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.math.BigDecimal as JavaBigDecimal

class AccountsViewModel : ViewModel() {

    // -- Services
    private var accountsService = AppModule.getClient().createService(AccountsApi::class.java)
    private var tradesService = AppModule.getClient().createService(TradesApi::class.java)

    // -- UI States
    var uiState: MutableState<AccountsView.ViewState> = mutableStateOf(AccountsView.ViewState.LOADING)

    // -- UI Triggers
    var showTradeDetail: MutableState<Boolean> = mutableStateOf(false)

    // -- Associated ViewModels
    var listPricesViewModel: ListPricesViewModel? = null

    // -- Polls
    internal var listPricesPolling: Polling? = null

    // -- Arrays
    private var accounts: List<AccountBankModel> by mutableStateOf(listOf())
    var accountsAssetPrice: List<AccountAssetPriceModel> by mutableStateOf(listOf())

    // -- Trades List
    var trades:List<TradeBankModel> by mutableStateOf(listOf())
    var currentAccountSelected: AccountAssetPriceModel? by mutableStateOf(null)
    var currentTrade: TradeBankModel = TradeBankModel()

    // -- Balances
    var totalBalance:String by mutableStateOf("")
    var totalFiatBalance:String by mutableStateOf("")

    // -- Current currency/customerGUID
    var currentFiatCurrency = "USD"
    var customerGuid = Cybrid.instance.customerGuid

    fun setDataProvider(dataProvider: ApiClient)  {

        accountsService = dataProvider.createService(AccountsApi::class.java)
        tradesService = dataProvider.createService(TradesApi::class.java)
    }

    suspend fun getAccountsList() {

        this.uiState.value = AccountsView.ViewState.LOADING
        Cybrid.instance.let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.let { scope ->
                    val waitFor = scope.async {

                        val accountsResult = getResult {
                            accountsService.listAccounts(
                                customerGuid = customerGuid
                            )
                        }
                        accountsResult.let {
                            if (isSuccessful(it.code ?: 500)) {

                                accounts = it.data?.objects ?: listOf()
                                getPricesList()
                                Logger.log(LoggerEvents.DATA_FETCHED, "Accounts")

                            } else {

                                accounts = listOf()
                                Logger.log(LoggerEvents.DATA_ERROR, "Accounts :: ${it.message}")
                            }
                        }
                    }
                    waitFor.await()
                }
            }
        }
    }

    internal suspend fun getPricesList() {

        listPricesViewModel?.getPricesList()
        if (listPricesViewModel?.prices?.isNotEmpty() == true) {

            createAccountsFormatted()
            uiState.value = AccountsView.ViewState.CONTENT
            listPricesPolling = Polling {
                viewModelScope.launch {

                    listPricesViewModel?.getPricesList()
                    createAccountsFormatted()
                }
            }
        }
    }

    fun createAccountsFormatted() {

        this.accountsAssetPrice = listOf()
        val accountsList = ArrayList<AccountAssetPriceModel>()
        this.accounts.let { balances ->
            balances.forEach { balance ->

                val code = balance.asset ?: "" // BTC
                val symbol = "$code-$currentFiatCurrency" // BTC-USD

                val asset = listPricesViewModel?.assets?.find { it.code == code } // BTC
                val counterAsset = listPricesViewModel?.assets?.find { it.code == currentFiatCurrency } // USD
                val price = listPricesViewModel?.prices?.find { it.symbol ==  symbol } // BTC-USD

                val assetDecimals = BigDecimal(asset?.decimals ?: JavaBigDecimal(0)) // 18

                val balanceValue = BigDecimal(balance.platformBalance ?: JavaBigDecimal(0))
                val balanceValueFormatted = AssetPipe.transform(balanceValue, assetDecimals, "trade")
                val balanceValueFormattedString = balanceValueFormatted.toPlainString()

                val buyPrice = BigDecimal(price?.buyPrice ?: JavaBigDecimal(0))
                val buyPriceFormatted = BigDecimalPipe.transform(buyPrice, counterAsset!!)

                val accountBalanceInFiat = balanceValueFormatted.times(buyPrice).setScale(2)
                val accountBalanceInFiatFormatted = BigDecimalPipe.transform(accountBalanceInFiat, counterAsset)

                val account = AccountAssetPriceModel(
                    accountAssetCode = code,
                    accountBalance = balanceValue.toJavaBigDecimal(),
                    accountBalanceFormatted = balanceValueFormatted,
                    accountBalanceFormattedString = balanceValueFormattedString,
                    accountBalanceInFiat = accountBalanceInFiat,
                    accountBalanceInFiatFormatted = accountBalanceInFiatFormatted ?: "$0.0",
                    accountGuid = balance.guid ?: "",
                    accountType = balance.type ?: AccountBankModel.Type.trading,
                    accountCreated = balance.createdAt ?: java.time.OffsetDateTime.now(),
                    assetName = asset?.name ?: "",
                    assetSymbol = asset?.symbol ?: "",
                    assetType = asset?.type ?: AssetBankModel.Type.fiat,
                    assetDecimals = asset?.decimals ?: JavaBigDecimal(0),
                    pairAsset = counterAsset,
                    buyPrice = buyPrice,
                    buyPriceFormatted = buyPriceFormatted ?: "",
                    sellPrice = price?.sellPrice ?: JavaBigDecimal(0)
                )
                accountsList.add(account)
            }
        }
        this.accountsAssetPrice = accountsList
        this.getCalculatedBalance()
        this.getCalculatedFiatBalance()
    }

    fun getCalculatedBalance() {

        var total = BigDecimal(0)
        if (this.accountsAssetPrice != null && this.accountsAssetPrice!!.isNotEmpty()) {
            val pairAsset = this.accountsAssetPrice!![0].pairAsset
            this.accountsAssetPrice!!.forEach { balance ->
                total = total.plus(balance.accountBalanceInFiat).setScale(2)
            }
            total = total
            this.totalBalance = BigDecimalPipe.transform(total, pairAsset)
        }
    }

    fun getCalculatedFiatBalance() {

        var total = BigDecimal(0)
        if (this.accountsAssetPrice != null && this.accountsAssetPrice!!.isNotEmpty()) {
            val counterAsset = listPricesViewModel?.assets?.find { it.code == currentFiatCurrency }
            this.accountsAssetPrice!!.forEach { balance ->
                if (balance.accountType == AccountBankModel.Type.fiat) {
                    total = total.plus(BigDecimal(balance.accountBalance))
                }
            }
            this.totalFiatBalance = if (counterAsset != null) {
                BigDecimalPipe.transform(total, counterAsset) ?: ""
            } else { "" }
        }
    }

    suspend fun getTradesList(account: AccountAssetPriceModel) {

        this.uiState.value = AccountsView.ViewState.LOADING
        this.currentAccountSelected = account
        Cybrid.instance.let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.launch {

                    // -- Getting prices
                    val tradesResult = getResult { tradesService.listTrades(accountGuid = account.accountGuid) }
                    tradesResult.let {
                        if (isSuccessful(it.code ?: 500)) {

                            trades = it.data?.objects ?: listOf()
                            uiState.value = AccountsView.ViewState.TRADES

                        } else {

                            Logger.log(LoggerEvents.DATA_ERROR, "Accounts Component - Data :: ${it.message}")
                            trades = listOf()
                        }
                    }
                }
            }
        }
    }

    fun getTradeAmount(trade: TradeBankModel) : String {

        val tradeSymbol = trade.symbol
        val assetsParts = tradeSymbol?.split("-")
        val assetString = assetsParts!![0]
        val asset = listPricesViewModel?.assets?.find { it.code == assetString }
        val returnValue = if (trade.side == TradeBankModel.Side.sell) {
            AssetPipe.transform(BigDecimal(trade.deliverAmount!!), asset!!, AssetPipeTrade)
        } else {
            AssetPipe.transform(BigDecimal(trade.receiveAmount!!), asset!!, AssetPipeTrade)
        }
        return returnValue.toPlainString()
    }

    fun getTradeFiatAmount(trade: TradeBankModel) : String {

        val tradeSymbol = trade.symbol
        val assetsParts = tradeSymbol?.split("-")
        val assetString = assetsParts!![1]
        val asset = listPricesViewModel?.assets?.find { it.code == assetString }
        val returnValue = if (trade.side == TradeBankModel.Side.sell) {
            BigDecimalPipe.transform(BigDecimal(trade.receiveAmount!!), asset!!)
        } else {
            BigDecimalPipe.transform(BigDecimal(trade.deliverAmount!!), asset!!)
        }
        return returnValue
    }

    fun showTradeDetail(trade: TradeBankModel) {

        this.currentTrade = trade
        this.showTradeDetail.value = true
    }

    fun dismissTradeDetail() {

        this.currentTrade = TradeBankModel()
        this.showTradeDetail.value = false
    }
}