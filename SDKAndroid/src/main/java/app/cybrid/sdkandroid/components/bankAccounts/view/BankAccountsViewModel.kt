package app.cybrid.sdkandroid.components.bankAccounts.view

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
import app.cybrid.sdkandroid.util.*
import app.cybrid.sdkandroid.components.BankAccountsView.BankAccountsViewState as BankAccountsViewState
import com.plaid.link.result.LinkAccount
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Locale

class BankAccountsViewModel: ViewModel() {

    private val plaidCustomizationName = "default"
    private val androidPackageName = "app.cybrid.sdkandroid"
    private val defaultAssetCurrency = "USD"

    private var workflowService = AppModule.getClient().createService(WorkflowsApi::class.java)
    private var externalBankAccountsService = AppModule.getClient().createService(ExternalBankAccountsApi::class.java)
    private var customerService = AppModule.getClient().createService(CustomersApi::class.java)
    private var bankService = AppModule.getClient().createService(BanksApi::class.java)

    var customerGuid = Cybrid.instance.customerGuid

    var uiState: MutableState<BankAccountsViewState> = mutableStateOf(BankAccountsViewState.LOADING)
    var workflowJob: Polling? = null
    var latestWorkflow: WorkflowWithDetailsBankModel? = null

    fun setDataProvider(dataProvider: ApiClient)  {

        workflowService = dataProvider.createService(WorkflowsApi::class.java)
        externalBankAccountsService = dataProvider.createService(ExternalBankAccountsApi::class.java)
        customerService = dataProvider.createService(CustomersApi::class.java)
        bankService = dataProvider.createService(BanksApi::class.java)
    }

    suspend fun createWorkflow() {

        Cybrid.instance.let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.let { scope ->
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
    }

    fun fetchWorkflow(guid: String) {

        Cybrid.instance.let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.let { scope ->
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
    }

    suspend fun createExternalBankAccount(publicToken: String, account: LinkAccount?) {

        Cybrid.instance.let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.let { scope ->
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
                                    uiState.value = BankAccountsViewState.DONE
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
    }

    suspend fun getCustomer(): CustomerBankModel? {

        var customer: CustomerBankModel? = null
        Cybrid.instance.let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.let { scope ->
                    val waitFor = scope.async {
                        val customerResult = getResult {
                            customerService.getCustomer(customerGuid = customerGuid)
                        }
                        customerResult.let {
                            if (isSuccessful(it.code ?: 500)) {
                                Logger.log(LoggerEvents.DATA_FETCHED, "Fetch - Customer")
                                customer = it.data
                                return@async customer
                            } else {
                                Logger.log(LoggerEvents.NETWORK_ERROR, "Fetch - Customer")
                            }
                        }
                    }
                    waitFor.await()
                }
            }
        }
        return customer
    }

    suspend fun getBank(guid: String): BankBankModel? {

        var bank: BankBankModel? = null
        Cybrid.instance.let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.let { scope ->
                    val waitFor = scope.async {
                        val bankResult = getResult {
                            bankService.getBank(bankGuid = guid)
                        }
                        bankResult.let {
                            if (isSuccessful(it.code ?: 500)) {
                                Logger.log(LoggerEvents.DATA_FETCHED, "Fetch - Bank")
                                bank = it.data
                                return@async bank
                            } else {
                                Logger.log(LoggerEvents.NETWORK_ERROR, "Fetch - Bank")
                            }
                        }
                    }
                    waitFor.await()
                }
            }
        }
        return bank
    }

    suspend fun assetIsSupported(asset: String?): Boolean {

        if (asset == null) {
            Logger.log(LoggerEvents.ERROR, "Account asset can't be null")
            return false
        } else {

            var allowed = false
            Cybrid.instance.let { cybrid ->
                viewModelScope.let { scope ->
                    val waitFor = scope.async {

                        val customer = getCustomer()
                        if (customer != null) {
                            if (customer.bankGuid != null) {
                                val bank = getBank(customer.bankGuid!!)
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
                                    Logger.log(LoggerEvents.ERROR, "Bank has a problem")
                                }
                            }
                        } else {
                            Logger.log(LoggerEvents.ERROR, "Customer has a problem")
                        }
                    }
                    waitFor.await()
                }
            }
            return allowed
        }
    }

    fun checkWorkflowStatus(workflow: WorkflowWithDetailsBankModel) {

        if (workflow.plaidLinkToken != null && workflow.plaidLinkToken != "") {

            this.workflowJob?.stop()
            this.workflowJob = null
            this.latestWorkflow = workflow
            this.uiState.value = BankAccountsViewState.REQUIRED
        }
    }
}