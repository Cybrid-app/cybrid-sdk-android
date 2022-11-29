package app.cybrid.sdkandroid.components.bankAccounts.view

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cybrid.cybrid_api_bank.client.apis.ExternalBankAccountsApi
import app.cybrid.cybrid_api_bank.client.apis.WorkflowsApi
import app.cybrid.cybrid_api_bank.client.infrastructure.ApiClient
import app.cybrid.cybrid_api_bank.client.models.PostExternalBankAccountBankModel
import app.cybrid.cybrid_api_bank.client.models.PostWorkflowBankModel
import app.cybrid.cybrid_api_bank.client.models.WorkflowWithDetailsBankModel
import app.cybrid.sdkandroid.AppModule
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.util.*
import app.cybrid.sdkandroid.components.BankAccountsView.BankAccountsViewState as BankAccountsViewState
import com.plaid.link.result.LinkAccount
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class BankAccountsViewModel: ViewModel() {

    private var workflowService = AppModule.getClient().createService(WorkflowsApi::class.java)
    private var externalBankAccountsService = AppModule.getClient().createService(ExternalBankAccountsApi::class.java)

    var customerGuid = Cybrid.instance.customerGuid

    var UIState: MutableState<BankAccountsViewState> = mutableStateOf(BankAccountsViewState.LOADING)
    var workflowJob: Polling? = null

    var latestWorflow: WorkflowWithDetailsBankModel? = null

    fun setDataProvider(dataProvider: ApiClient)  {

        workflowService = dataProvider.createService(WorkflowsApi::class.java)
        externalBankAccountsService = dataProvider.createService(ExternalBankAccountsApi::class.java)
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
                                    externalBankAccountGuid = "3aa2bc487ae469bbc03cb42cf72e3aa3",
                                    language = PostWorkflowBankModel.Language.en,
                                    linkCustomizationName = "default",
                                    androidPackageName = "app.cybrid.sdkandroid"
                                )
                            )
                        }
                        Logger.log(LoggerEvents.DATA_FETCHED, "Create - Workflow")
                        workflowResult.let {
                            if (isSuccessful(it.code ?: 500)) {
                                workflowJob = Polling { fetchWorkflow(guid = workflowResult.data?.guid!!) }
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
                        Logger.log(LoggerEvents.DATA_REFRESHED, "Fetch - Workflow")
                        workflowResult.let {
                            if (isSuccessful(it.code ?: 500)) {
                                checkWorkflowStatus(workflow = workflowResult.data!!)
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

                        val externalBankAccountResult = getResult {
                            externalBankAccountsService.createExternalBankAccount(
                                postExternalBankAccountBankModel = PostExternalBankAccountBankModel(
                                    name = account?.name ?: "",
                                    accountKind = PostExternalBankAccountBankModel.AccountKind.plaid,
                                    asset = "USD",
                                    customerGuid = customerGuid,
                                    plaidPublicToken = publicToken,
                                    plaidAccountId = account?.id ?: ""
                                )
                            )
                        }
                        Logger.log(LoggerEvents.DATA_FETCHED, "Create - External BankAccount")
                        externalBankAccountResult.let {
                            if (isSuccessful(it.code ?: 500)) {
                                UIState.value = BankAccountsViewState.DONE
                            }
                        }
                    }
                    waitFor.await()
                }
            }
        }
    }

    fun checkWorkflowStatus(workflow: WorkflowWithDetailsBankModel) {

        if (workflow.plaidLinkToken != null && workflow.plaidLinkToken != "") {

            this.workflowJob?.stop()
            this.workflowJob = null
            this.latestWorflow = workflow
            this.UIState.value = BankAccountsViewState.REQUIRED
        }
    }
}