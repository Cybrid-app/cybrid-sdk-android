package app.cybrid.sdkandroid.components.accounts.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cybrid.cybrid_api_bank.client.apis.AccountsApi
import app.cybrid.cybrid_api_bank.client.models.AccountBankModel
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.cybrid_api_bank.client.models.SymbolPriceBankModel
import app.cybrid.sdkandroid.AppModule
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.components.accounts.entity.AccountAssetPriceModel
import app.cybrid.sdkandroid.core.AssetPipe
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
    var accounts:List<AccountAssetPriceModel> by mutableStateOf(listOf())

    var totalBalance:String by mutableStateOf("")

    fun getAccounts() {

        val accountService = AppModule.getClient().createService(AccountsApi::class.java)
        Cybrid.instance.let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.launch {

                    // -- Getting prices
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

    fun createAccountsFormatted(
        prices:List<SymbolPriceBankModel>,
        assets:List<AssetBankModel>
    ) {

        this.accounts = listOf()
        val accountsList = ArrayList<AccountAssetPriceModel>()
        this.accountsResponse.let { balances ->
            balances.forEach { balance ->

                val code = balance.asset ?: ""
                val symbol = "$code-$currentFiatCurrency"
                val asset = assets.find { it.code == code }
                val pairAsset = assets.find { it.code == currentFiatCurrency }
                val price = prices.find { it.symbol ==  symbol}
                val assetDecimals = BigDecimal(asset?.decimals ?: JavaBigDecimal(0))

                val balanceValue = BigDecimal(balance.platformBalance ?: JavaBigDecimal(0))
                val balanceValueFormatted = AssetPipe.transform(balanceValue, assetDecimals, "trade")

                val buyPrice = BigDecimal(price?.buyPrice ?: JavaBigDecimal(0))
                val buyPriceFormatted = BigDecimalPipe.transform(buyPrice, pairAsset!!)

                val accountBalanceInFiat = balanceValueFormatted.times(buyPrice)
                val accountBalanceInFiatFormatted = BigDecimalPipe.transform(accountBalanceInFiat, pairAsset)

                val account = AccountAssetPriceModel(
                    accountAssetCode = code,
                    accountBalance = balanceValue.toJavaBigDecimal(),
                    accountBalanceFormatted = balanceValueFormatted,
                    accountBalanceInFiat = accountBalanceInFiat,
                    accountBalanceInFiatFormatted = accountBalanceInFiatFormatted ?: "$0.0",
                    accountGuid = balance.guid ?: "",
                    accountType = balance.type ?: AccountBankModel.Type.trading,
                    accountCreated = balance.createdAt ?: java.time.OffsetDateTime.now(),
                    assetName = asset?.name ?: "",
                    assetSymbol = asset?.symbol ?: "",
                    assetType = asset?.type ?: AssetBankModel.Type.fiat,
                    assetDecimals = asset?.decimals ?: JavaBigDecimal(0),
                    pairAsset = pairAsset,
                    buyPrice = buyPrice,
                    buyPriceFormatted = buyPriceFormatted ?: "",
                    sellPrice = price?.sellPrice ?: JavaBigDecimal(0)
                )
                accountsList.add(account)
            }
        }
        this.accounts = accountsList
    }

    fun getTotalBalance() {

        var total = BigDecimal(0)
        this.accounts.let { balances ->

            val pairAsset = balances[0].pairAsset
            balances.forEach { balance ->
                total = total.plus(balance.accountBalanceInFiat)
            }
            this.totalBalance = BigDecimalPipe.transform(total, pairAsset) ?: ""
        }
    }
}