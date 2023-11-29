package app.cybrid.sdkandroid.components.accounts.compose

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.core.Constants
import app.cybrid.sdkandroid.ui.Theme.robotoFont

@Composable
fun AccountsView_Loading() {

    Box(
        modifier = Modifier
            .height(120.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.transfer_view_component_loading_view_title),
                fontFamily = robotoFont,
                fontWeight = FontWeight.Normal,
                fontSize = 17.sp,
                color = colorResource(id = R.color.primary_color)
            )
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .testTag(Constants.AccountsViewTestTags.Loading.id),
                color = colorResource(id = R.color.primary_color)
            )
        }
    }
}