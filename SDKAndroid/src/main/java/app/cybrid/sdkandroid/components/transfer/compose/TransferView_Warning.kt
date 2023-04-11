package app.cybrid.sdkandroid.components.transfer.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.ui.Theme.interFont
import org.bouncycastle.math.raw.Mod

@Composable
fun TransferView_Warning(modifier: Modifier) {
    
    // -- View
    Box(
        modifier = modifier
            .fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .align(Alignment.Center)
                .background(Color.Red),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Some of your accounts have a problem, please reconnect it.",
                modifier = Modifier,
                fontFamily = interFont,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                lineHeight = 10.sp,
                color = colorResource(id = R.color.white)
            )
        }
    }
}