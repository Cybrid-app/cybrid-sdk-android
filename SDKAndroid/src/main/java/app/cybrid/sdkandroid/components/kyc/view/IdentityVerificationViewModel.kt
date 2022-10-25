package app.cybrid.sdkandroid.components.kyc.view

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cybrid.sdkandroid.AppModule
import app.cybrid.cybrid_api_bank.client.apis.CustomersApi
import app.cybrid.cybrid_api_bank.client.apis.IdentityVerificationsApi
import app.cybrid.cybrid_api_bank.client.models.*
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.util.Pooling
import java.math.BigDecimal as JavaBigDecimal
import app.cybrid.sdkandroid.util.getResult
import kotlinx.coroutines.*

class IdentityVerificationViewModel: ViewModel() {

    private val customerService = AppModule.getClient().createService(CustomersApi::class.java)
    private val identityService = AppModule.getClient().createService(IdentityVerificationsApi::class.java)

    protected var customerJob: Pooling? = null

    var customerGuid = "c2af9f1976fe17364092e6e725b36a68" // Cybrid.instance.customerGuid
    var customerState: CustomerBankModel.State by mutableStateOf(CustomerBankModel.State.storing)

    fun createCustomerTest() {

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
                        Log.d("DXGOP", "CREATE CUSTOMER")
                        Log.d("DXGOP", customerResult.toString())
                        customerGuid = customerResult.data?.guid ?: customerGuid
                        getCustomerStatus()
                    }
                }
            }
        }
    }

    // -- Loop
    fun getCustomerStatus() {

        Cybrid.instance.let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.let { scope ->
                    scope.launch {

                        val customerResult = getResult {
                            customerService.getCustomer(
                                customerGuid = customerGuid)
                        }
                        Log.d("DXGOP", "CUSTOMER STATUS")
                        Log.d("DXGOP", customerResult.toString())
                        checkCustomerStatus(customerResult.data?.state ?: CustomerBankModel.State.storing)
                    }
                }
            }
        }
    }

    // -- Loop
    fun getIdentityVerificationDetail(record: IdentityVerificationBankModel? = null) {

        Cybrid.instance.let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.let { scope ->
                    scope.launch {

                        val lastVerification = record ?: getLastIdentityVerification()
                        Log.d("DXGOP", lastVerification.toString())

                        /*if (lastVerification == null ||
                            lastVerification.state == IdentityVerificationBankModel.State.expired) {
                            lastVerification = createIdentityVerification()
                        }
                        val recordResponse = getResult {
                            identityService.getIdentityVerification(
                                identityVerificationGuid = lastVerification?.guid ?: ""
                            )
                        }
                        Log.d("DXGOP", "IDENTITY STATUS")
                        Log.d("DXGOP", recordResponse.toString())
                        checkIdentityRecordStatus(recordResponse.data)*/
                    }
                }
            }
        }
    }

    /*private fun getLastIdentityVerification(): IdentityVerificationBankModel? {

        var verification: IdentityVerificationBankModel? = null
        Cybrid.instance.let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.let { scope ->
                    scope.launch {

                        val identityResponse = getResult {
                            identityService.listIdentityVerifications(
                                customerGuid = customerGuid
                            )
                        }
                        Log.d("DXGOP", "VERIFICATIONS LIST")
                        Log.d("DXGOP", identityResponse.toString())

                        val total: JavaBigDecimal = identityResponse.data?.total ?: JavaBigDecimal(0)
                        if (total > JavaBigDecimal(0)) {

                            val verifications = identityResponse.data?.objects
                            verifications?.sortedBy { it.createdAt }
                            verification = verifications?.get(0)
                        }
                    }
                }
            }
        }
        return verification
    }*/

    private suspend fun getLastIdentityVerification(): IdentityVerificationBankModel? {

        var verification: IdentityVerificationBankModel? = null
        Cybrid.instance.let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.let { scope ->
                    val waitFor = scope.async {

                        val identityResponse = getResult {
                            identityService.listIdentityVerifications(
                                customerGuid = customerGuid
                            )
                        }
                        Log.d("DXGOP", "VERIFICATIONS LIST")
                        Log.d("DXGOP", identityResponse.toString())

                        val total: JavaBigDecimal = identityResponse.data?.total ?: JavaBigDecimal(0)
                        if (total > JavaBigDecimal(0)) {

                            val verifications = identityResponse.data?.objects
                            verifications?.sortedBy { it.createdAt }
                            verification = verifications?.get(0)
                        }
                        return@async verification
                    }
                    waitFor.await()
                }
            }
        }
        return verification
    }

    private suspend fun createIdentityVerification(): IdentityVerificationBankModel? {

        var verification: IdentityVerificationBankModel? = null
        Cybrid.instance.let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.let { scope ->
                    scope.launch {

                        val recordResponse = getResult {
                            identityService.createIdentityVerification(
                                postIdentityVerificationBankModel = PostIdentityVerificationBankModel(
                                    type = PostIdentityVerificationBankModel.Type.kyc,
                                    method = PostIdentityVerificationBankModel.Method.idAndSelfie,
                                    customerGuid = customerGuid,
                                )
                            )
                        }
                        Log.d("DXGOP", "CREATE VERIFICATION")
                        Log.d("DXGOP", recordResponse.toString())
                        verification = recordResponse.data
                    }
                }
            }
        }
        return verification
    }

    private fun checkCustomerStatus(state: CustomerBankModel.State) {

        when (state) {

            CustomerBankModel.State.storing -> {

                customerState = CustomerBankModel.State.storing
                if (customerJob == null) {
                    customerJob = Pooling { getCustomerStatus() }
                }
            }

            else -> {

                customerJob?.stop()
                customerState = state
            }
        }
    }

    private fun checkIdentityRecordStatus(record: IdentityVerificationBankModel?) {

        when(record?.state) {


        }
    }
}