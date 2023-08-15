package app.cybrid.sdkandroid.components.depositAddress.view

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import app.cybrid.cybrid_api_bank.client.apis.DepositAddressesApi
import app.cybrid.sdkandroid.AppModule
import app.cybrid.sdkandroid.components.DepositAddressView

class DepositAddressViewModel: ViewModel() {

    private var depositAddressService = AppModule.getClient().createService(DepositAddressesApi::class.java)

    var uiState: MutableState<DepositAddressView.State> = mutableStateOf(DepositAddressView.State.LOADING)
}