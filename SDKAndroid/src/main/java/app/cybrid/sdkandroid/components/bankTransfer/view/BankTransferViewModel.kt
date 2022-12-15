package app.cybrid.sdkandroid.components.bankTransfer.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cybrid.cybrid_api_bank.client.apis.*
import app.cybrid.cybrid_api_bank.client.infrastructure.ApiClient
import app.cybrid.cybrid_api_bank.client.models.*
import app.cybrid.sdkandroid.AppModule
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.core.BigDecimal
import app.cybrid.sdkandroid.core.BigDecimalPipe
import app.cybrid.sdkandroid.util.Logger
import app.cybrid.sdkandroid.util.LoggerEvents
import app.cybrid.sdkandroid.util.getResult
import app.cybrid.sdkandroid.util.isSuccessful
import kotlinx.coroutines.async
import java.math.BigDecimal as JavaBigDecimal

class BankTransferViewModel: ViewModel() {

    private var accountsService = AppModule.getClient().createService(AccountsApi::class.java)
    private var customerService = AppModule.getClient().createService(CustomersApi::class.java)
    private var externalBankAccountsService = AppModule.getClient().createService(ExternalBankAccountsApi::class.java)
    private var quoteService = AppModule.getClient().createService(QuotesApi::class.java)
    private var tradeService = AppModule.getClient().createService(TradesApi::class.java)

    var currentFiatCurrency = "USD"
    var customerGuid = Cybrid.instance.customerGuid
    var assets: List<AssetBankModel> = listOf()

    var externalBankAccounts: List<ExternalBankAccountBankModel> by mutableStateOf(listOf())
    var currentQuote: QuoteBankModel? by mutableStateOf(null)
    var currentTrade: TradeBankModel? by mutableStateOf(null)

    fun setDataProvider(dataProvider: ApiClient)  {

        accountsService = dataProvider.createService(AccountsApi::class.java)
        customerService = dataProvider.createService(CustomersApi::class.java)
        externalBankAccountsService = dataProvider.createService(ExternalBankAccountsApi::class.java)
        quoteService = dataProvider.createService(QuotesApi::class.java)
        tradeService = dataProvider.createService(TradesApi::class.java)
    }

    suspend fun getFiatBalance(): String {

        val pairAsset = assets.find { it.code == currentFiatCurrency }
        val accounts = fetchAccounts()
        var total = BigDecimal(0)
        accounts.forEach { account ->
            if (account.type == AccountBankModel.Type.fiat &&
                account.state == AccountBankModel.State.created) {
                val balance = BigDecimal(account.platformBalance ?: JavaBigDecimal(0))
                total = total.plus(balance)
            }
        }
        return BigDecimalPipe.transform(total, pairAsset!!) ?: ""
    }

    suspend fun fetchAccounts(): List<AccountBankModel> {

        var accounts: List<AccountBankModel> = listOf()
        Cybrid.instance.let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.let { scope ->
                    val waitFor = scope.async {

                        val accountsResponse = getResult { accountsService.listAccounts(customerGuid = customerGuid) }
                        accountsResponse.let {
                            if (isSuccessful(it.code ?: 500)) {
                                Logger.log(LoggerEvents.DATA_REFRESHED, "Fetch - Workflow")
                                accounts = it.data?.objects ?: listOf()
                                return@async accounts
                            } else {
                                Logger.log(LoggerEvents.NETWORK_ERROR, "Fetch - Workflow")
                            }
                        }
                    }
                    waitFor.await()
                }
            }
        }
        return accounts
    }

    suspend fun fetchExternalAccounts() {

        Cybrid.instance.let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.let { scope ->
                    val waitFor = scope.async {
                        val externalAccountsResponse = getResult {
                            externalBankAccountsService.listExternalBankAccounts(customerGuid = customerGuid)
                        }
                        externalAccountsResponse.let {
                            if (isSuccessful(it.code ?: 500)) {
                                Logger.log(LoggerEvents.DATA_REFRESHED, "Fetch - Workflow")
                                externalBankAccounts = it.data?.objects ?: listOf()
                            } else {
                                Logger.log(LoggerEvents.NETWORK_ERROR, "Fetch - Workflow")
                            }
                        }
                    }
                    waitFor.await()
                }
            }
        }
    }

    suspend fun createQuote(side: PostQuoteBankModel.Side, amount: BigDecimal) {

        Cybrid.instance.let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.let { scope ->
                    val waitFor = scope.async {

                        val postQuoteBankModel = PostQuoteBankModel(
                            side = side,
                            productType = PostQuoteBankModel.ProductType.funding,
                            customerGuid = customerGuid,
                            asset = currentFiatCurrency,
                            deliverAmount = amount.toJavaBigDecimal()
                        )
                        val quoteResponse = getResult { quoteService.createQuote(postQuoteBankModel) }
                        quoteResponse.let {
                            if (isSuccessful(it.code ?: 500)) {
                                Logger.log(LoggerEvents.DATA_REFRESHED, "Fetch - Workflow")
                                currentQuote = it.data
                            } else {
                                Logger.log(LoggerEvents.NETWORK_ERROR, "Fetch - Workflow")
                            }
                        }
                    }
                    waitFor.await()
                }
            }
        }
    }

    suspend fun createTrade() {

        Cybrid.instance.let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.let { scope ->
                    val waitFor = scope.async {

                        val postTradeBankModel = PostTradeBankModel(quoteGuid = currentQuote?.guid!!)
                        val tradeResponse = getResult { tradeService.createTrade(postTradeBankModel) }
                        tradeResponse.let {
                            if (isSuccessful(it.code ?: 500)) {
                                Logger.log(LoggerEvents.DATA_REFRESHED, "Fetch - Workflow")
                                currentTrade = it.data
                            } else {
                                Logger.log(LoggerEvents.NETWORK_ERROR, "Fetch - Workflow")
                            }
                        }
                    }
                    waitFor.await()
                }
            }
        }
    }
}