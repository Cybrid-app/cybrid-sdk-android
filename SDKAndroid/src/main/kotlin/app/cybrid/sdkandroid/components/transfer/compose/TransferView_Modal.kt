package app.cybrid.sdkandroid.components.transfer.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import app.cybrid.cybrid_api_bank.client.models.*
import app.cybrid.sdkandroid.components.TransferView
import app.cybrid.sdkandroid.components.transfer.view.TransferViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferView_Modal(
    transferViewModel: TransferViewModel?,
    externalBankAccount: MutableState<ExternalBankAccountBankModel?>,
    showDialog: MutableState<Boolean>,
    selectedTabIndex: MutableState<Int>
) {

    // -- Vars
    val modalUiState: MutableState<TransferView.ModalViewState> = transferViewModel!!.modalUiState

    // -- Compose Content
    ModalBottomSheet(
        onDismissRequest = {
            showDialog.value = false
            transferViewModel.modalUiState.value = TransferView.ModalViewState.LOADING
        },
        containerColor = Color.White,
        windowInsets = WindowInsets(0)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            when(modalUiState.value) {

                TransferView.ModalViewState.LOADING -> {
                    TransferView_Modal_Loading()
                }

                TransferView.ModalViewState.CONFIRM -> {
                    TransferView_Modal_Confirm(
                        transferViewModel = transferViewModel,
                        externalBankAccount = externalBankAccount.value,
                        selectedTabIndex = selectedTabIndex
                    )
                }

                TransferView.ModalViewState.DETAILS -> {
                    TransferView_Modal_Details(
                        transferViewModel = transferViewModel,
                        externalBankAccount = externalBankAccount.value,
                        selectedTabIndex = selectedTabIndex,
                        showDialog = showDialog
                    )
                }
            }
        }
    }
}