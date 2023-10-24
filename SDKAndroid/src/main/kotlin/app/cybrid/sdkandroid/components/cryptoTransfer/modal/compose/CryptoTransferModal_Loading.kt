package app.cybrid.sdkandroid.components.cryptoTransfer.modal.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.Component

@Composable
fun CryptoTransferModal_Loading() {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {

        // -- Vars
        val (loader) = createRefs()

        // -- Content
        Component.CreateLoader(modifier =
        Modifier.constrainAs(loader) {
            centerHorizontallyTo(parent)
            centerVerticallyTo(parent)
        },
            message = stringResource(id = R.string.crypto_transfer_view_modal_loading_title)
        )
    }
}