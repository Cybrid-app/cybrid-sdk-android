package app.cybrid.sdkandroid.components.kyc.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cybrid.sdkandroid.AppModule
import app.cybrid.cybrid_api_bank.client.apis.CustomersApi
import app.cybrid.cybrid_api_bank.client.apis.IdentityRecordsApi
import app.cybrid.cybrid_api_bank.client.models.CustomerBankModel
import app.cybrid.cybrid_api_bank.client.models.IdentityRecordBankModel
import app.cybrid.cybrid_api_bank.client.models.PostCustomerBankModel
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.util.getResult
import kotlinx.coroutines.launch

class IdentityVerificationViewModel: ViewModel() {

    private val customerService = AppModule.getClient().createService(CustomersApi::class.java)
    private val identityService = AppModule.getClient().createService(IdentityRecordsApi::class.java)

    var customerGuid = Cybrid.instance.customerGuid
    var customerState: CustomerBankModel.State by mutableStateOf(CustomerBankModel.State.storing)

    private fun createCustomerTest() {

        Cybrid.instance.let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.let { scope ->
                    scope.launch {

                        val customerResult = getResult {
                            customerService.createCustomer(
                                postCustomerBankModel = PostCustomerBankModel(
                                    type = PostCustomerBankModel.Type.individual)
                            )
                        }
                        customerGuid = customerResult.data?.guid ?: customerGuid
                    }
                }
            }
        }
    }

    fun getCustomerStatus() {

        Cybrid.instance.let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.let { scope ->
                    scope.launch {

                        val customerResult = getResult {
                            customerService.getCustomer(
                                customerGuid = customerGuid)
                        }
                        checkCustomerStatus(customerResult.data?.state ?: CustomerBankModel.State.storing)
                    }
                }
            }
        }
    }

    private suspend fun getLastIdentityVerification(): IdentityRecordBankModel? {

        var verification: IdentityRecordBankModel? = null
        Cybrid.instance.let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.let { scope ->
                    scope.launch {

                        val identityResponse = getResult {
                            identityService.listIdentityRecords(
                                customerGuid = customerGuid
                            )
                        }
                        val verifications = identityResponse.data?.objects
                        verifications?.sortedBy { it.createdAt }
                        verification = verifications?.get(0)
                    }
                }
            }
        }
        return verification
    }

    fun getIdentityVerificationDetail() {

        Cybrid.instance.let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.let { scope ->
                    scope.launch {

                        val lastVerification = getLastIdentityVerification()
                        print(lastVerification)
                    }
                }
            }
        }
    }

    private fun checkCustomerStatus(state: CustomerBankModel.State) {

        when(state) {

            CustomerBankModel.State.storing -> {
                if (customerState != CustomerBankModel.State.storing) {
                    customerState = CustomerBankModel.State.storing
                }
            }

            else -> {}
        }
    }
}