package app.cybrid.sdkandroid.components.wallets.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout

@Composable
fun ExternalWalletsView_Wallets() {

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Magenta)
    ) {

        // -- Vars
        val (title) = createRefs()
    }
}