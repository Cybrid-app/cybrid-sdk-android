package app.cybrid.sdkandroid.components.accounts.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cybrid.cybrid_api_bank.client.apis.AccountsApi
import app.cybrid.cybrid_api_bank.client.apis.TradesApi
import app.cybrid.cybrid_api_bank.client.models.AccountBankModel
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.cybrid_api_bank.client.models.SymbolPriceBankModel
import app.cybrid.cybrid_api_bank.client.models.TradeBankModel
import app.cybrid.sdkandroid.AppModule
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.components.accounts.entity.AccountAssetPriceModel
import app.cybrid.sdkandroid.core.AssetPipe
import app.cybrid.sdkandroid.core.AssetPipe.AssetPipeTrade
import app.cybrid.sdkandroid.core.BigDecimal
import app.cybrid.sdkandroid.core.BigDecimalPipe
import app.cybrid.sdkandroid.util.Logger
import app.cybrid.sdkandroid.util.LoggerEvents
import app.cybrid.sdkandroid.util.getResult
import app.cybrid.sdkandroid.util.isSuccessful
import kotlinx.coroutines.launch
import java.math.BigDecimal as JavaBigDecimal

class AccountsViewModel : ViewModel() {

    var currentFiatCurrency = "USD"

    var accountsResponse:List<AccountBankModel> by mutableStateOf(listOf())
    var accounts:List<AccountAssetPriceModel>? by mutableStateOf(null)
    var assets: List<AssetBankModel> = listOf()

    var totalBalance:String by mutableStateOf("")
    var totalFiatBalance:String by mutableStateOf("")

    // -- Trades List
    var trades:List<TradeBankModel> by mutableStateOf(listOf())
    private var currentAccountAssetPriceModel:AccountAssetPriceModel? by mutableStateOf(null)

    fun getAccountsList() {

        val accountService = AppModule.getClient().createService(AccountsApi::class.java)
        Cybrid.instance.let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.let { scope ->
                    scope.launch {
                        val accountsResult = getResult { accountService.listAccounts(customerGuid = Cybrid.instance.customerGuid) }
                        accountsResult.let {
                            accountsResponse = if (isSuccessful(it.code ?: 500)) {
                                it.data?.objects ?: listOf()
                            } else {
                                Logger.log(LoggerEvents.DATA_ERROR, "Accounts Component - Data :: ${it.message}")
                                listOf()
                            }
                        }
                    }
                }
            }
        }
    }

    fun createAccountsFormatted(prices:List<SymbolPriceBankModel>, assets:List<AssetBankModel>) {

        this.accounts = listOf()
        this.assets = assets
        val accountsList = ArrayList<AccountAssetPriceModel>()
        this.accountsResponse.let { balances ->
            balances.forEach { balance ->

                val code = balance.asset ?: "" // BTC
                val symbol = "$code-$currentFiatCurrency" // BTC-USD

                val asset = assets.find { it.code == code } // BTC
                val counterAsset = assets.find { it.code == currentFiatCurrency } // USD
                val price = prices.find { it.symbol ==  symbol } // BTC-USD

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
        this.accounts = accountsList
    }

    fun getCalculatedBalance() {

        var total = BigDecimal(0)
        if (this.accounts != null && this.accounts!!.isNotEmpty()) {
            val pairAsset = this.accounts!![0].pairAsset
            this.accounts!!.forEach { balance ->
                total = total.plus(balance.accountBalanceInFiat).setScale(2)
            }
            total = total
            this.totalBalance = BigDecimalPipe.transform(total, pairAsset) ?: ""
        }
    }

    fun getCalculatedFiatBalance() {

        var total = BigDecimal(0)
        if (this.accounts != null && this.accounts!!.isNotEmpty()) {
            val counterAsset = assets.find { it.code == currentFiatCurrency }
            this.accounts!!.forEach { balance ->
                if (balance.accountType == AccountBankModel.Type.fiat) {
                    total = total.plus(BigDecimal(balance.accountBalance))
                }
            }
            this.totalFiatBalance = if (counterAsset != null) {
                BigDecimalPipe.transform(total, counterAsset) ?: ""
            } else { "" }
        }
    }

    fun getTradesList(balance: AccountAssetPriceModel) {

        this.currentAccountAssetPriceModel = balance
        val tradesService = AppModule.getClient().createService(TradesApi::class.java)
        Cybrid.instance.let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.launch {

                    // -- Getting prices
                    val tradesResult = getResult { tradesService.listTrades(accountGuid = balance.accountGuid) }
                    tradesResult.let {
                        trades = if (isSuccessful(it.code ?: 500)) {
                             it.data?.objects ?: listOf()
                        } else {
                            Logger.log(LoggerEvents.DATA_ERROR, "Accounts Component - Data :: ${it.message}")
                            listOf()
                        }
                    }
                }
            }
        }
    }

    fun getTradeAmount(trade: TradeBankModel, assets:List<AssetBankModel>?) : String {

        val tradeSymbol = trade.symbol
        val assetsParts = tradeSymbol?.split("-")
        val assetString = assetsParts!![0]
        val asset = assets?.find { it.code == assetString }
        val returnValue = if (trade.side == TradeBankModel.Side.sell) {
            AssetPipe.transform(BigDecimal(trade.deliverAmount!!), asset!!, AssetPipeTrade)
        } else {
            AssetPipe.transform(BigDecimal(trade.receiveAmount!!), asset!!, AssetPipeTrade)
        }
        return returnValue.toPlainString()
    }

    fun getTradeFiatAmount(trade: TradeBankModel, assets:List<AssetBankModel>?) : String? {

        val tradeSymbol = trade.symbol
        val assetsParts = tradeSymbol?.split("-")
        val assetString = assetsParts!![1]
        val asset = assets?.find { it.code == assetString }
        val returnValue = if (trade.side == TradeBankModel.Side.sell) {
            BigDecimalPipe.transform(BigDecimal(trade.receiveAmount!!), asset!!)
        } else {
            BigDecimalPipe.transform(BigDecimal(trade.deliverAmount!!), asset!!)
        }
        return returnValue
    }

    fun getCurrentTradeAccount() : AccountAssetPriceModel? {
        return this.currentAccountAssetPriceModel
    }

    fun cleanTrades() {

        this.trades = listOf()
        this.currentAccountAssetPriceModel = null
    }
}