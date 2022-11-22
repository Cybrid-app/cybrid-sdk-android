package app.cybrid.sdkandroid.components.bankAccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cybrid.cybrid_api_bank.client.apis.WorkflowsApi
import app.cybrid.cybrid_api_bank.client.models.PostWorkflowBankModel
import app.cybrid.sdkandroid.AppModule
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.util.Logger
import app.cybrid.sdkandroid.util.LoggerEvents
import app.cybrid.sdkandroid.util.getResult
import kotlinx.coroutines.async

class BankAccountViewModel: ViewModel() {

    private var workflowService = AppModule.getClient().createService(WorkflowsApi::class.java)

    private var customerGuid = Cybrid.instance.customerGuid

    //var workflowJob: Po

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
                        Logger.log(LoggerEvents.DATA_REFRESHED, "Create - Workflow")
                    }
                    waitFor.await()
                }
            }
        }
    }
}