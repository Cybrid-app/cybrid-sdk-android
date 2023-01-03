package app.cybrid.sdkandroid.components.transfer.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.core.Constants

@Composable
fun TransferView_Modal_Loading() {

    Box(
        modifier = Modifier
            .height(120.dp)
            .testTag(Constants.TransferView.LoadingView.id)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .testTag(Constants.BankAccountsView.LoadingViewIndicator.id),
                color = colorResource(id = R.color.primary_color)
            )
        }
    }
}