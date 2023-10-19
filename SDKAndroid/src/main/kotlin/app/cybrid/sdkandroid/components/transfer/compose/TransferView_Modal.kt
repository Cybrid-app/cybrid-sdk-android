package app.cybrid.sdkandroid.components.transfer.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import app.cybrid.cybrid_api_bank.client.models.*
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.TransferView
import app.cybrid.sdkandroid.components.transfer.view.TransferViewModel
import app.cybrid.sdkandroid.ui.lib.BottomSheetDialog_

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
    BottomSheetDialog_(
        onDismissRequest = {

            showDialog.value = false
            transferViewModel.modalUiState.value = TransferView.ModalViewState.LOADING
        }
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = colorResource(id = R.color.white),
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