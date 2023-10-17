package app.cybrid.sdkandroid.components.kyc.compose

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.activity.KYCActivity
import app.cybrid.sdkandroid.ui.Theme.interFont
import app.cybrid.sdkandroid.ui.lib.BottomSheetDialog_

@Composable
fun KYCView_Modal_Warning(
    showDialog: MutableState<Boolean>,
    context: Context
) {

    // -- Compose Content
    BottomSheetDialog_(
        onDismissRequest = {
            showDialog.value = false
        }
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = colorResource(id = R.color.white),
            modifier = Modifier
        ) {

            Column(
                modifier = Modifier
                    .padding(20.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.kyc_view_modal_warning),
                    modifier = Modifier
                        .padding(top = 10.dp),
                    fontFamily = interFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = 17.sp,
                    lineHeight = 22.5.sp,
                    color = colorResource(id = R.color.black)
                )
                // -- Done Button
                Button(
                    onClick = {

                        showDialog.value = false
                        context.startActivity(Intent(context, KYCActivity::class.java))
                    },
                    modifier = Modifier
                        .padding(top = 25.dp, bottom = 5.dp)
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 4.dp,
                        disabledElevation = 0.dp
                    ),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = colorResource(id = R.color.accent_blue),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.kyc_view_modal_warning_begin_button),
                        color = Color.White,
                        fontFamily = interFont,
                        fontWeight = FontWeight.Medium,
                        fontSize = 17.sp,
                    )
                }
            }

        }
    }
}