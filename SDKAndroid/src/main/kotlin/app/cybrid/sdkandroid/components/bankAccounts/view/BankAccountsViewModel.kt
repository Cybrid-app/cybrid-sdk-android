package app.cybrid.sdkandroid.components.bankAccounts.view

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cybrid.cybrid_api_bank.client.apis.BanksApi
import app.cybrid.cybrid_api_bank.client.apis.CustomersApi
import app.cybrid.cybrid_api_bank.client.apis.ExternalBankAccountsApi
import app.cybrid.cybrid_api_bank.client.apis.WorkflowsApi
import app.cybrid.cybrid_api_bank.client.infrastructure.ApiClient
import app.cybrid.cybrid_api_bank.client.models.*
import app.cybrid.sdkandroid.AppModule
import app.cybrid.sdkandroid.BuildConfig
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.components.BankAccountsView
import app.cybrid.sdkandroid.util.*
import com.plaid.link.configuration.LinkTokenConfiguration
import app.cybrid.sdkandroid.components.BankAccountsView.State as BankAccountsViewState
import com.plaid.link.result.LinkAccount
import com.plaid.link.result.LinkResult
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Locale

class BankAccountsViewModel: ViewModel() {

    private val plaidCustomizationName = "default"
    private val androidPackageName = "app.cybrid.demoapp"
    private val defaultAssetCurrency = "USD"

    private var workflowService = AppModule.getClient().createService(WorkflowsApi::class.java)
    private var externalBankAccountsService = AppModule.getClient().createService(ExternalBankAccountsApi::class.java)
    private var customerService = AppModule.getClient().createService(CustomersApi::class.java)
    private var bankService = AppModule.getClient().createService(BanksApi::class.java)

    var customerGuid = Cybrid.customerGuid

    var uiState: MutableState<BankAccountsView.State> = mutableStateOf(BankAccountsView.State.LOADING)
    var buttonAddAccountsState: MutableState<BankAccountsView.AddAccountButtonState> = mutableStateOf(BankAccountsView.AddAccountButtonState.LOADING)
    var accountDetailState: MutableState<BankAccountsView.ModalState> = mutableStateOf(BankAccountsView.ModalState.CONTENT)

    var workflowJob: Polling? = null
    var workflowUpdateJob: Polling? = null
    var externalAccountJob: Polling? = null
    var latestWorkflow: WorkflowWithDetailsBankModel? = null
    var latestWorkflowUpdate: WorkflowWithDetailsBankModel? = null

    var accounts: List<ExternalBankAccountBankModel>? by mutableStateOf(null)

    var showAccountDetailModal: MutableState<Boolean> = mutableStateOf(false)
    var currentAccount: ExternalBankAccountBankModel = ExternalBankAccountBankModel()

    var getPlaidUpdateResult: ManagedActivityResultLauncher<LinkTokenConfiguration, LinkResult>? = null

    fun setDataProvider(dataProvider: ApiClient)  {

        workflowService = dataProvider.createService(WorkflowsApi::class.java)
        externalBankAccountsService = dataProvider.createService(ExternalBankAccountsApi::class.java)
        customerService = dataProvider.createService(CustomersApi::class.java)
        bankService = dataProvider.createService(BanksApi::class.java)
    }

    suspend fun fetchExternalBankAccounts() {

        val customer = Cybrid.customer
        uiState.value = BankAccountsViewState.LOADING
        buttonAddAccountsState.value = BankAccountsView.AddAccountButtonState.LOADING

        if (!Cybrid.invalidToken) {
            this.viewModelScope.let { scope ->
                val waitFor = scope.async {
                    val accountsResult = getResult {
                        externalBankAccountsService.listExternalBankAccounts(
                            customerGuid = customerGuid
                        )
                    }
                    accountsResult.let {
                        if (isSuccessful(it.code ?: 500)) {

                            Logger.log(LoggerEvents.DATA_REFRESHED, "Fetch - External Bank Accounts")

                            val accountsList = it.data?.objects ?: listOf()
                            val accountsFiltered = accountsList.filter { account ->
                                account.state != ExternalBankAccountBankModel.State.deleted &&
                                account.state != ExternalBankAccountBankModel.State.deleting
                            }
                            accounts = accountsFiltered
                            uiState.value = BankAccountsView.State.CONTENT

                            buttonAddAccountsState.value = BankAccountsView.AddAccountButtonState.LOADING
                            createWorkflow()

                        } else {
                            Logger.log(LoggerEvents.NETWORK_ERROR, "Fetch - External Bank Accounts")
                        }
                    }
                }
                waitFor.await()
            }
        }
    }

    suspend fun createWorkflow() {

        if (!Cybrid.invalidToken) {
            this.viewModelScope.let { scope ->
                val waitFor = scope.async {

                    val workflowResult = getResult {
                        workflowService.createWorkflow(
                            postWorkflowBankModel = PostWorkflowBankModel(
                                type = PostWorkflowBankModel.Type.plaid,
                                kind = PostWorkflowBankModel.Kind.create,
                                customerGuid = customerGuid,
                                language = getLanguage(Locale.getDefault().language),
                                linkCustomizationName = plaidCustomizationName,
                                androidPackageName = androidPackageName
                            )
                        )
                    }

                    workflowResult.let {
                        if (isSuccessful(it.code ?: 500)) {
                            Logger.log(LoggerEvents.DATA_FETCHED, "Create - Workflow")
                            workflowJob = Polling { fetchWorkflow(guid = workflowResult.data?.guid!!) }
                        } else {
                            Logger.log(LoggerEvents.NETWORK_ERROR, "Create - Workflow")
                            uiState.value = BankAccountsViewState.ERROR
                        }
                    }
                }
                waitFor.await()
            }
        }
    }

    fun fetchWorkflow(guid: String) {

        if (!Cybrid.invalidToken) {
            this.viewModelScope.let { scope ->
                scope.launch {
                    val workflowResult = getResult {
                        workflowService.getWorkflow(workflowGuid = guid)
                    }
                    workflowResult.let {
                        if (isSuccessful(it.code ?: 500)) {
                            Logger.log(LoggerEvents.DATA_REFRESHED, "Fetch - Workflow")
                            checkWorkflowStatus(workflow = workflowResult.data!!)
                        } else {
                            Logger.log(LoggerEvents.NETWORK_ERROR, "Fetch - Workflow")
                        }
                    }
                }
            }
        }
    }

    suspend fun createExternalBankAccount(publicToken: String, account: LinkAccount?) {

        if (!Cybrid.invalidToken) {
            this.viewModelScope.let { scope ->
                val waitFor = scope.async {

                    val assetCurrency = if (account?.balance?.currency == null && BuildConfig.DEBUG) {
                        defaultAssetCurrency
                    } else { account?.balance?.currency }

                    if (assetIsSupported(asset = assetCurrency)) {

                        val externalBankAccountResult = getResult {
                            externalBankAccountsService.createExternalBankAccount(
                                postExternalBankAccountBankModel = PostExternalBankAccountBankModel(
                                    name = account?.name ?: "",
                                    accountKind = PostExternalBankAccountBankModel.AccountKind.plaid,
                                    asset = assetCurrency!!,
                                    customerGuid = customerGuid,
                                    plaidPublicToken = publicToken,
                                    plaidAccountId = account?.id ?: ""
                                )
                            )
                        }

                        externalBankAccountResult.let {
                            if (isSuccessful(it.code ?: 500)) {
                                Logger.log(LoggerEvents.DATA_FETCHED, "Create - External BankAccount")
                                externalAccountJob = Polling { fetchExternalBankAccount(externalBankAccountResult.data?.guid!!) }
                            } else {
                                Logger.log(LoggerEvents.NETWORK_ERROR, "Create - External BankAccount")
                                uiState.value = BankAccountsViewState.ERROR
                            }
                        }
                    } else {
                        uiState.value = BankAccountsViewState.ERROR
                    }
                }
                waitFor.await()
            }
        }
    }

    internal fun fetchExternalBankAccount(guid: String) {

        if (!Cybrid.invalidToken) {
            this.viewModelScope.let { scope ->
                scope.launch {
                    val accountResult = getResult {
                        externalBankAccountsService.getExternalBankAccount(guid)
                    }
                    accountResult.let {
                        if (isSuccessful(it.code ?: 500)) {
                            Logger.log(LoggerEvents.DATA_REFRESHED, "Fetch - Workflow")
                            checkExternalBankAccountStatus(accountResult.data!!)
                        } else {
                            Logger.log(LoggerEvents.NETWORK_ERROR, "Fetch - Workflow")
                        }
                    }
                }
            }
        }
    }

    suspend fun assetIsSupported(asset: String?): Boolean {

        if (asset == null) {
            Logger.log(LoggerEvents.ERROR, "Account asset can't be null")
            return false
        } else {

            var allowed = false
            this.viewModelScope.let { scope ->
                val waitFor = scope.async {

                    val customer = Cybrid.customer
                    if (customer != null) {
                        val bank = Cybrid.bank
                        if (bank != null) {
                            if (bank.supportedFiatAccountAssets != null) {
                                if (bank.supportedFiatAccountAssets!!.contains(asset)) {
                                    allowed = true
                                } else {
                                    Logger.log(LoggerEvents.ERROR, "Asset is not supported")
                                }
                            } else {
                                Logger.log(LoggerEvents.ERROR, "Bank don't have fiat assets")
                            }
                        } else {
                            Logger.log(LoggerEvents.ERROR, "You need to set a bank in SDKConfig")
                        }
                    } else {
                        Logger.log(LoggerEvents.ERROR, "You need to set a customer in SDKConfig")
                    }
                }
                waitFor.await()
            }
            return allowed
        }
    }

    fun checkWorkflowStatus(workflow: WorkflowWithDetailsBankModel) {

        if (workflow.plaidLinkToken != null && workflow.plaidLinkToken != "") {

            this.workflowJob?.stop()
            this.workflowJob = null
            this.latestWorkflow = workflow
            this.buttonAddAccountsState.value = BankAccountsView.AddAccountButtonState.READY
        }
    }

    internal fun checkExternalBankAccountStatus(account: ExternalBankAccountBankModel) {

        if (account.state != ExternalBankAccountBankModel.State.storing) {

            this.externalAccountJob?.stop()
            this.externalAccountJob = null
            this.uiState.value = BankAccountsViewState.DONE
        }
    }

    fun showExternalBankAccountDetail(account: ExternalBankAccountBankModel) {

        this.currentAccount = account
        this.accountDetailState.value = BankAccountsView.ModalState.CONTENT
        this.showAccountDetailModal.value = true
    }

    fun dismissExternalBankAccountDetail() {

        this.currentAccount = ExternalBankAccountBankModel()
        this.showAccountDetailModal.value = false
        this.accountDetailState.value = BankAccountsView.ModalState.CONTENT
    }

    suspend fun disconnectExternalBankAccount() {

        val currentAccountId = this.currentAccount.guid ?: ""
        this.dismissExternalBankAccountDetail()
        uiState.value = BankAccountsViewState.LOADING
        buttonAddAccountsState.value = BankAccountsView.AddAccountButtonState.LOADING
        if (!Cybrid.invalidToken) {
            this.viewModelScope.let { scope ->
                val waitFor = scope.async {
                    val accountsResult = getResult {
                        externalBankAccountsService.deleteExternalBankAccount(currentAccountId)
                    }
                    accountsResult.let {
                        if (isSuccessful(it.code ?: 500)) {

                            Logger.log(LoggerEvents.DATA_REFRESHED, "Delete - External Bank Accounts")
                            fetchExternalBankAccounts()

                        } else {
                            Logger.log(LoggerEvents.NETWORK_ERROR, "Delete - External Bank Accounts")
                        }
                    }
                }
                waitFor.await()
            }
        }
    }

    // -- Methods to refresh external bank account

    suspend fun refreshAccount() {

        val account = this.currentAccount
        this.accountDetailState.value = BankAccountsView.ModalState.LOADING
        val workflow = createUpdateWorkflow(account = account)
        this.workflowUpdateJob = Polling { fetchUpdateWorkflow(guid = workflow?.guid!! ) }
    }

    internal suspend fun createUpdateWorkflow(account: ExternalBankAccountBankModel): WorkflowBankModel? {

        var workflow: WorkflowBankModel? = null
        if (!Cybrid.invalidToken) {
            this.viewModelScope.let { scope ->
                val waitFor = scope.async {

                    val workflowResult = getResult {
                        workflowService.createWorkflow(
                            postWorkflowBankModel = PostWorkflowBankModel(
                                type = PostWorkflowBankModel.Type.plaid,
                                kind = PostWorkflowBankModel.Kind.update,
                                customerGuid = customerGuid,
                                externalBankAccountGuid = account.guid,
                                language = getLanguage(Locale.getDefault().language),
                                linkCustomizationName = plaidCustomizationName,
                                androidPackageName = androidPackageName
                            )
                        )
                    }

                    workflowResult.let {
                        if (isSuccessful(it.code ?: 500)) {
                            Logger.log(LoggerEvents.DATA_FETCHED, "Create - Workflow")
                            workflow = workflowResult.data
                            return@async workflow

                        } else {
                            Logger.log(LoggerEvents.NETWORK_ERROR, "Create - Workflow")
                            uiState.value = BankAccountsViewState.ERROR
                        }
                    }
                }
                waitFor.await()
            }
        }
        return workflow
    }

    internal fun fetchUpdateWorkflow(guid: String) {

        if (!Cybrid.invalidToken) {
                this.viewModelScope.let { scope ->
                    scope.launch {
                        val workflowResult = getResult {
                            workflowService.getWorkflow(workflowGuid = guid)
                        }
                        workflowResult.let {
                            if (isSuccessful(it.code ?: 500)) {
                                Logger.log(LoggerEvents.DATA_REFRESHED, "Fetch - Workflow")
                                checkWorkflowUpdateStatus(workflow = workflowResult.data!!)
                            } else {
                                Logger.log(LoggerEvents.NETWORK_ERROR, "Fetch - Workflow")
                            }
                        }
                    }
                }
            }
    }

    internal fun checkWorkflowUpdateStatus(workflow: WorkflowWithDetailsBankModel) {

        if (workflow.plaidLinkToken != null && workflow.plaidLinkToken != "") {

            this.workflowUpdateJob?.stop()
            this.workflowUpdateJob = null
            this.latestWorkflowUpdate = workflow
            if (this.getPlaidUpdateResult != null) {
                BankAccountsView.openPlaid(latestWorkflowUpdate?.plaidLinkToken!!, getPlaidUpdateResult!!)
            }
        }
    }

    suspend fun updateExternalBankAccount(state: PatchExternalBankAccountBankModel.State) {

        if (!Cybrid.invalidToken) {
            this.viewModelScope.let { scope ->
                val waitFor = scope.async {

                    val externalBAnkAccountPatch = PatchExternalBankAccountBankModel(
                        state = state
                    )

                    val externalBankAccountResult = getResult {
                        externalBankAccountsService.patchExternalBankAccount(
                            externalBankAccountGuid = currentAccount.guid!!,
                            patchExternalBankAccountBankModel = externalBAnkAccountPatch
                        )
                    }

                    externalBankAccountResult.let {
                        if (isSuccessful(it.code ?: 500)) {

                            Logger.log(LoggerEvents.DATA_FETCHED, "Create - External BankAccount")
                            dismissExternalBankAccountDetail()
                            fetchExternalBankAccounts()

                        } else {
                            Logger.log(LoggerEvents.NETWORK_ERROR, "Create - External BankAccount")
                            uiState.value = BankAccountsViewState.ERROR
                        }
                    }
                }
                waitFor.await()
            }
        }
    }

    fun showAuthScreen() {
        this.uiState.value = BankAccountsView.State.AUTH
    }
}