package app.cybrid.sdkandroid.components.cryptoTransfer.view

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.components.CryptoTransferView
import app.cybrid.sdkandroid.components.ExternalWalletsView

class CryptoTransferViewModel: ViewModel() {

    // -- Internal properties
    internal var customerGuid = Cybrid.customerGuid

    // -- Public properties
    var uiState: MutableState<CryptoTransferView.State> = mutableStateOf(CryptoTransferView.State.LOADING)
    var modalUiState: MutableState<CryptoTransferView.ModalState> = mutableStateOf(
        CryptoTransferView.ModalState.LOADING)
}