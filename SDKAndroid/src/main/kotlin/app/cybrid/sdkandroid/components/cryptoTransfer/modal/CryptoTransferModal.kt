package app.cybrid.sdkandroid.components.cryptoTransfer.modal

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.cybrid.sdkandroid.components.CryptoTransferView
import app.cybrid.sdkandroid.components.cryptoTransfer.modal.compose.CryptoTransferModal_Loading
import app.cybrid.sdkandroid.components.cryptoTransfer.modal.compose.CryptoTransferModal_Quote
import app.cybrid.sdkandroid.components.cryptoTransfer.modal.compose.CryptoTransferModal_Success
import app.cybrid.sdkandroid.components.cryptoTransfer.view.CryptoTransferViewModel
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialogProperties
import com.holix.android.bottomsheetdialog.compose.NavigationBarProperties

@Composable
fun CryptoTransferModal(
    cryptoTransferViewModel: CryptoTransferViewModel
) {

    BottomSheetDialog(
        properties = BottomSheetDialogProperties(
            enableEdgeToEdge = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            navigationBarProperties = NavigationBarProperties(
                color = Color.Black,
                navigationBarContrastEnforced = true
            )
        ),
        onDismissRequest = { cryptoTransferViewModel.closeModal() }
    ) {

        Surface(
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp, Color.Black),
            color = Color.White,
            elevation = 1.dp,
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