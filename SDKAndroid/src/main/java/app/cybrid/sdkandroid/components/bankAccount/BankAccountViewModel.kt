package app.cybrid.sdkandroid.components.bankAccount

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cybrid.cybrid_api_bank.client.apis.WorkflowsApi
import app.cybrid.cybrid_api_bank.client.models.PostWorkflowBankModel
import app.cybrid.cybrid_api_bank.client.models.WorkflowWithDetailsBankModel
import app.cybrid.sdkandroid.AppModule
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.components.BankAccountsView
import app.cybrid.sdkandroid.components.BankAccountsView.BankAccountsViewState as BankAccountsViewState
import app.cybrid.sdkandroid.util.Logger
import app.cybrid.sdkandroid.util.LoggerEvents
import app.cybrid.sdkandroid.util.Polling
import app.cybrid.sdkandroid.util.getResult
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class BankAccountViewModel: ViewModel() {

    private var workflowService = AppModule.getClient().createService(WorkflowsApi::class.java)

    private var customerGuid = Cybrid.instance.customerGuid

    var UIState: MutableState<BankAccountsViewState> = mutableStateOf(BankAccountsViewState.LOADING)
    var workflowJob: Polling? = null

    fun createWorkflow() {

        Cybrid.instance.let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.let { scope ->
                    scope.launch {

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
                        Logger.log(LoggerEvents.DATA_REFRESHED, "Create - Workflow")
                        workflowJob = Polling { fetchWorkflow(guid = workflowResult.data?.guid!!) }
                    }
                }
            }
        }
    }

    fun fetchWorkflow(guid: String) {

        Cybrid.instance.let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.let { scope ->
                    scope.async {
                        val workflowResult = getResult {
                            workflowService.getWorkflow(workflowGuid = guid)
                        }
                        Logger.log(LoggerEvents.DATA_REFRESHED, "Fetch - Workflow")
                        checkWorkflowStatus(workflow = workflowResult.data!!)
                    }
                }
            }
        }
    }

    fun checkWorkflowStatus(workflow: WorkflowWithDetailsBankModel) {

        if (workflow.plaidLinkToken != null && workflow.plaidLinkToken != "") {

            this.workflowJob?.stop()
            this.workflowJob = null
            this.UIState.value = BankAccountsViewState.REQUIRED
        }
    }
}