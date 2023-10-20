package app.cybrid.sdkandroid.components.cryptoTransfer.modal

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import app.cybrid.sdkandroid.components.CryptoTransferView
import app.cybrid.sdkandroid.components.cryptoTransfer.modal.compose.CryptoTransferModal_Loading
import app.cybrid.sdkandroid.components.cryptoTransfer.modal.compose.CryptoTransferModal_Quote
import app.cybrid.sdkandroid.components.cryptoTransfer.modal.compose.CryptoTransferModal_Success
import app.cybrid.sdkandroid.components.cryptoTransfer.view.CryptoTransferViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoTransferModal(
    cryptoTransferViewModel: CryptoTransferViewModel
) {

    ModalBottomSheet(
        onDismissRequest = { cryptoTransferViewModel.closeModal() },
        containerColor = Color.White,
        windowInsets = WindowInsets(0)
    ) {

        Surface(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            when(cryptoTransferViewModel.modalUiState.value) {

                CryptoTransferView.ModalState.LOADING -> {
                    CryptoTransferModal_Loading()
                }

                CryptoTransferView.ModalState.QUOTE -> {
                    CryptoTransferModal_Quote(cryptoTransferViewModel)
                }

                CryptoTransferView.ModalState.DONE -> {
                    CryptoTransferModal_Success(cryptoTransferViewModel)
                }

                else -> {}
            }
        }
    }
}