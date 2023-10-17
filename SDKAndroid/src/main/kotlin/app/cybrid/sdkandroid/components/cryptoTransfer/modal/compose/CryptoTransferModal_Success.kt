package app.cybrid.sdkandroid.components.cryptoTransfer.modal.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalProvider
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import app.cybrid.sdkandroid.components.Component
import app.cybrid.sdkandroid.components.CryptoTransferView
import app.cybrid.sdkandroid.components.cryptoTransfer.view.CryptoTransferViewModel
import app.cybrid.sdkandroid.ui.lib.ContinueButton

@Composable
fun CryptoTransferModal_Success(
    cryptoTransferViewModel: CryptoTransferViewModel
) {

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            //.height(180.dp)
    ) {

        // -- Vars
        val (success) = createRefs()

        // -- Content
        Column(
            modifier = Modifier
                .constrainAs(success) {
                    centerHorizontallyTo(parent)
                    centerVerticallyTo(parent)
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // -- Success
            Component.CreateSuccess(
                modifier = Modifier.padding(top = 20.dp),
                message = "Transfer done correctly"
            )
            // -- Continue Button
            ContinueButton(
                modifier = Modifier
                    .padding(20.dp)
                    .height(48.dp)
                    .fillMaxWidth(),
                text = "Continue"
            ) {
                cryptoTransferViewModel.closeModal()
            }
        }
    }
}