package app.cybrid.sdkandroid.components.transfer.view

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cybrid.cybrid_api_bank.client.apis.*
import app.cybrid.cybrid_api_bank.client.infrastructure.ApiClient
import app.cybrid.cybrid_api_bank.client.models.*
import app.cybrid.sdkandroid.AppModule
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.components.TransferView
import app.cybrid.sdkandroid.core.AssetPipe
import app.cybrid.sdkandroid.core.BigDecimal
import app.cybrid.sdkandroid.core.BigDecimalPipe
import app.cybrid.sdkandroid.util.Logger
import app.cybrid.sdkandroid.util.LoggerEvents
import app.cybrid.sdkandroid.util.getResult
import app.cybrid.sdkandroid.util.isSuccessful
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext
import java.math.BigDecimal as JavaBigDecimal

class TransferViewModel: ViewModel() {

    private var dispatcher: CoroutineDispatcher = Dispatchers.Default

    private var assetsService = AppModule.getClient().createService(AssetsApi::class.java)
    private var accountsService = AppModule.getClient().createService(AccountsApi::class.java)
    private var customerService = AppModule.getClient().createService(CustomersApi::class.java)
    private var externalBankAccountsService = AppModule.getClient().createService(ExternalBankAccountsApi::class.java)
    private var quoteService = AppModule.getClient().createService(QuotesApi::class.java)
    private var transferService = AppModule.getClient().createService(TransfersApi::class.java)

    var uiState: MutableState<TransferView.ViewState> = mutableStateOf(TransferView.ViewState.LOADING)
    val modalUiState: MutableState<TransferView.ModalViewState> = mutableStateOf(TransferView.ModalViewState.LOADING)
    val viewDismiss: MutableState<Boolean> = mutableStateOf(false)

    val uiWarning: MutableState<Boolean> = mutableStateOf(false)

    var currentFiatCurrency = "USD"
    var customerGuid = Cybrid.customerGuid
    var assets: List<AssetBankModel>? = null

    var accounts: List<AccountBankModel> by mutableStateOf(listOf())
    var externalBankAccounts: List<ExternalBankAccountBankModel> by mutableStateOf(listOf())

    var fiatBalance: MutableState<String> = mutableStateOf("")
    var currentQuote: QuoteBankModel? by mutableStateOf(null)
    var currentTransfer: TransferBankModel? by mutableStateOf(null)

    fun setDataProvider(dataProvider: ApiClient)  {

        assetsService = dataProvider.createService(AssetsApi::class.java)
        accountsService = dataProvider.createService(AccountsApi::class.java)
        customerService = dataProvider.createService(CustomersApi::class.java)
        externalBankAccountsService = dataProvider.createService(ExternalBankAccountsApi::class.java)
        quoteService = dataProvider.createService(QuotesApi::class.java)
        transferService = dataProvider.createService(TransfersApi::class.java)
    }

    fun setDispatcher(dispatcher: CoroutineDispatcher) {
        this.dispatcher = dispatcher
    }

    suspend fun fetchAssets(): List<AssetBankModel>? {

        var assets: List<AssetBankModel>? = null
        if (!Cybrid.invalidToken) {
            this.viewModelScope.let { scope ->
                val waitFor = scope.async {
                    val assetsResponse = getResult { assetsService.listAssets() }
                    assetsResponse.let {
                        if (isSuccessful(it.code ?: 500)) {
                            Logger.log(LoggerEvents.DATA_REFRESHED, "Fetch - Workflow")
                            assets = it.data?.objects
                            return@async assets
                        }
                    }
                }
                waitFor.await()
            }
        }
        return assets
    }

    suspend fun fetchAccounts() {

        if (!Cybrid.invalidToken) {
            this.viewModelScope.let { scope ->
                val waitFor = scope.async {

                    assets = fetchAssets()
                    val accountsResponse = getResult { accountsService.listAccounts(customerGuid = customerGuid) }
                    accountsResponse.let {
                        if (isSuccessful(it.code ?: 500)) {
                            Logger.log(LoggerEvents.DATA_REFRESHED, "Fetch - Workflow")
                            accounts = it.data?.objects ?: listOf()
                            fetchExternalAccounts()
                        } else {
                            Logger.log(LoggerEvents.NETWORK_ERROR, "Fetch - Workflow")
                        }
                    }
                }
                waitFor.await()
            }
        }
    }

    suspend fun fetchExternalAccounts() {

        if (!Cybrid.invalidToken) {
            this.viewModelScope.let { scope ->
                val waitFor = scope.async {
                    val externalAccountsResponse = getResult {
                        externalBankAccountsService.listExternalBankAccounts(customerGuid = customerGuid)
                    }
                    externalAccountsResponse.let {
                        if (isSuccessful(it.code ?: 500)) {

                            Logger.log(LoggerEvents.DATA_REFRESHED, "Fetch - External Accounts")
                            val accounts = it.data?.objects ?: listOf()
                            checkAccounts(accounts)

                        } else {
                            Logger.log(LoggerEvents.NETWORK_ERROR, "Fetch - External Accounts")
                        }
                    }
                }
                waitFor.await()
            }
        }
    }

    private fun checkAccounts(accounts: List<ExternalBankAccountBankModel>) {

        this.externalBankAccounts = accounts.filter {
                account -> account.state != ExternalBankAccountBankModel.State.deleted &&
                account.state != ExternalBankAccountBankModel.State.deleting
        }

        val accountWithRefreshRequired = this.externalBankAccounts.firstOrNull {
            it.state == ExternalBankAccountBankModel.State.refreshRequired
        }
        if (accountWithRefreshRequired != null) {
            this.uiWarning.value = true
        }

        this.uiState.value = TransferView.ViewState.ACCOUNTS
        calculateFiatBalance()
    }

    fun calculateFiatBalance() {

        val counterAsset = assets?.find { it.code == currentFiatCurrency }
        var total = BigDecimal(0)
        this.accounts.forEach { account ->
            if (account.type == AccountBankModel.Type.fiat &&
                account.state == AccountBankModel.State.created) {
                val balance = BigDecimal(account.platformAvailable ?: JavaBigDecimal(0))
                total = total.plus(balance)
            }
        }
        this.fiatBalance.value = if (counterAsset != null) {
            BigDecimalPipe.transform(total, counterAsset)
        } else { "" }
    }

    suspend fun createQuote(side: PostQuoteBankModel.Side, amount: BigDecimal) {

        if (!Cybrid.invalidToken) {
            this.viewModelScope.let { scope ->
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
                            modalUiState.value = TransferView.ModalViewState.CONFIRM
                        } else {
                            Logger.log(LoggerEvents.NETWORK_ERROR, "Fetch - Workflow")
                        }
                    }
                }
                waitFor.await()
            }
        }
    }

    suspend fun createTransfer(externalBankAccount: ExternalBankAccountBankModel) {

        Cybrid.let { cybrid ->
            if (!cybrid.invalidToken) {
                this.viewModelScope.let { scope ->
                    val waitFor = scope.async {

                        val postTransferPostQuoteBankModel = PostTransferBankModel(
                            quoteGuid = currentQuote?.guid!!,
                            transferType = PostTransferBankModel.TransferType.funding,
                            externalBankAccountGuid = externalBankAccount.guid,
                        )

                        val transferResponse = getResult { transferService.createTransfer(postTransferPostQuoteBankModel) }
                        transferResponse.let {
                            if (isSuccessful(it.code ?: 500)) {

                                Logger.log(LoggerEvents.DATA_REFRESHED, "Fetch - Workflow")
                                currentTransfer = it.data
                                modalUiState.value = TransferView.ModalViewState.DETAILS

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

    fun transformAmountInBaseBigDecimal(amount: String): BigDecimal {

        val counterAsset = assets?.find { it.code == currentFiatCurrency }
        return if (counterAsset != null) {
            AssetPipe.transform(amount, counterAsset, "base")
        } else {
            BigDecimal(0)
        }
    }

    fun transformQuoteAmountInLabelString(quote: QuoteBankModel?): String {

        val counterAsset = assets?.find { it.code == currentFiatCurrency }
        return if (counterAsset != null) {
            val amount = BigDecimal(quote?.deliverAmount ?: JavaBigDecimal(0))
            BigDecimalPipe.transform(amount, counterAsset)
        } else {
            "0"
        }
    }

    fun notifyAccountsHaveToChange() {

        Cybrid.let { cybrid ->
            viewModelScope.launch {
                cybrid.accountsRefreshObservable.emit(true)
            }
        }
        viewDismiss.value = true
    }
}