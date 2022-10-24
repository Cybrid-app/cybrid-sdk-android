package app.cybrid.sdkandroid.components.kyc.view

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cybrid.sdkandroid.AppModule
import app.cybrid.cybrid_api_bank.client.apis.CustomersApi
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.util.getResult
import kotlinx.coroutines.launch

class IdentityVerificationViewModel: ViewModel() {

    fun getCustomerStatus() {

        val customerService = AppModule.getClient().createService(CustomersApi::class.java)
        Cybrid.instance.let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.let { scope ->
                    scope.launch {

                        val customerResult = getResult { customerService.getCustomer(customerGuid = Cybrid.instance.customerGuid) }
                        Log.d("DXGOP", customerResult.data.toString())
                    }
                }
            }
        }
    }
}