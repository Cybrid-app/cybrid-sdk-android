package app.cybrid.sdkandroid.components.kyc.view

import android.util.Log
import androidx.compose.runtime.MutableState
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
import app.cybrid.sdkandroid.components.KYCView
import app.cybrid.sdkandroid.util.Pooling
import java.math.BigDecimal as JavaBigDecimal
import app.cybrid.sdkandroid.util.getResult
import kotlinx.coroutines.*

class IdentityVerificationViewModel: ViewModel() {

    private val customerService = AppModule.getClient().createService(CustomersApi::class.java)
    private val identityService = AppModule.getClient().createService(IdentityVerificationsApi::class.java)

    protected var customerJob: Pooling? = null
    protected var identityJob: Pooling? = null

    var customerGuid = Cybrid.instance.customerGuid
    var UIState: MutableState<KYCView.KYCViewState>? = null

    var latestIdentityVerification: IdentityVerificationBankModel? = null

    suspend fun createCustomerTest() {

        Cybrid.instance.let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.let { scope ->
                    val waitFor = scope.async {

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
                    waitFor.await()
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
                        Log.d("DXGOP", "CUSTOMER STATUS")
                        Log.d("DXGOP", customerResult.toString())
                        checkCustomerStatus(customerResult.data?.state ?: CustomerBankModel.State.storing)
                    }
                }
            }
        }
    }

    fun getIdentityVerificationStatus(record: IdentityVerificationBankModel? = null) {

        Cybrid.instance.let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.let { scope ->
                    scope.launch {

                        var lastVerification = record ?: getLastIdentityVerification()
                        Log.d("DXGOP", "---> LAST VERIFICATION")
                        Log.d("DXGOP", lastVerification.toString())

                        if (lastVerification == null ||
                            lastVerification.state == IdentityVerificationBankModel.State.expired ||
                            lastVerification.personaState == IdentityVerificationBankModel.PersonaState.expired) {

                            lastVerification = createIdentityVerification()
                            Log.d("DXGOP", "---> CREATION")
                            Log.d("DXGOP", lastVerification.toString())
                        }
                        val recordResponse = getResult {
                            identityService.getIdentityVerification(
                                identityVerificationGuid = lastVerification?.guid ?: ""
                            )
                        }
                        Log.d("DXGOP", "IDENTITY STATUS")
                        Log.d("DXGOP", recordResponse.toString())
                        checkIdentityRecordStatus(recordResponse.data)
                    }
                }
            }
        }
    }

    private suspend fun getLastIdentityVerification(): IdentityVerificationBankModel? {

        var verification: IdentityVerificationBankModel? = null
        Cybrid.instance.let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.let { scope ->
                    val waitFor = scope.async {

                        val identityResponse = getResult {
                            identityService.listIdentityVerifications(
                                customerGuid = customerGuid,
                                page = JavaBigDecimal(1),
                                perPage = JavaBigDecimal(1)
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
                    val waitFor = scope.async {

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
                        return@async verification
                    }
                    waitFor.await()
                }
            }
        }
        return verification
    }

    private fun checkCustomerStatus(state: CustomerBankModel.State) {

        when (state) {

            CustomerBankModel.State.storing -> {

                if (customerJob == null) {
                    customerJob = Pooling { getCustomerStatus() }
                }
            }

            CustomerBankModel.State.verified -> {

                customerJob?.stop()
                UIState?.value = KYCView.KYCViewState.VERIFIED
            }

            CustomerBankModel.State.unverified -> {

                customerJob?.stop()
                getIdentityVerificationStatus()
            }

            CustomerBankModel.State.rejected -> {

                customerJob?.stop()
                UIState?.value = KYCView.KYCViewState.ERROR
            }
        }
    }

    private fun checkIdentityRecordStatus(record: IdentityVerificationBankModel?) {

        when(record?.state) {

            IdentityVerificationBankModel.State.storing -> {

                if (identityJob == null) {
                    identityJob = Pooling { getIdentityVerificationStatus(record = record) }
                }
            }

            IdentityVerificationBankModel.State.waiting -> {

                if (record.personaState != IdentityVerificationBankModel.PersonaState.completed) {

                    identityJob?.stop()
                    identityJob = null
                    checkIdentityPersonaStatus(record)
                } else {
                    identityJob = Pooling { getIdentityVerificationStatus(record = record) }
                }
            }

            IdentityVerificationBankModel.State.expired -> {

                identityJob?.stop()
                identityJob = null
                getIdentityVerificationStatus(null)
            }

            IdentityVerificationBankModel.State.completed -> {

                identityJob?.stop()
                identityJob = null
                UIState?.value = KYCView.KYCViewState.VERIFIED
            }
        }
    }

    private fun checkIdentityPersonaStatus(record: IdentityVerificationBankModel?) {

        when(record?.personaState) {

            IdentityVerificationBankModel.PersonaState.waiting -> {

                this.latestIdentityVerification = record
                UIState?.value = KYCView.KYCViewState.REQUIRED
            }

            IdentityVerificationBankModel.PersonaState.pending -> {

                this.latestIdentityVerification = record
                UIState?.value = KYCView.KYCViewState.REQUIRED
            }

            IdentityVerificationBankModel.PersonaState.reviewing -> {

                UIState?.value = KYCView.KYCViewState.REVIEWING
            }

            IdentityVerificationBankModel.PersonaState.unknown -> {

                UIState?.value = KYCView.KYCViewState.ERROR
            }
        }
    }
}