package app.cybrid.sdkandroid.components.wallets.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.Component

@Composable
fun ExternalWalletsView_Loading() {

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {

        // -- Vars
        val (loader) = createRefs()

        // -- Content
        Component.CreateLoader(modifier =
            Modifier.constrainAs(loader) {
                centerHorizontallyTo(parent)
                centerVerticallyTo(parent)
            },
            message = stringResource(id = R.string.wallets_view_loading_title)
        )
    }
}