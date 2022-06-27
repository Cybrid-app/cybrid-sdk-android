package app.cybrid.sdkandroid.components.quote.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.cybrid_api_bank.client.models.QuoteBankModel
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.ui.Theme.robotoFont

@Composable
fun QuoteConfirmationModal(
    model: MutableState<QuoteBankModel>,
    asset: MutableState<AssetBankModel>,
    pairAsset: AssetBankModel,
    refreshTime: Long = 5000
) {

    // -- Sub Title
    val subTitleText = buildAnnotatedString {
        append(stringResource(id = R.string.trade_flow_pre_quote_confirmation_modal_sub_title_1))
        withStyle(style = SpanStyle(
            color = colorResource(id = R.color.modal_sub_title_refresh_color),
            fontFamily = robotoFont,
            fontWeight = FontWeight.Bold)
        ) {
            append(" " + (refreshTime/1000) + " ")
        }
        append(stringResource(id = R.string.trade_flow_pre_quote_confirmation_modal_sub_title_2))
    }

    Dialog(
        onDismissRequest = {}
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = colorResource(id = R.color.modal_color)
        ) {
            Box() {
                Column() {
                    Text(
                        text = stringResource(id = R.string.trade_flow_pre_quote_confirmation_modal_title),
                        modifier = Modifier
                            .padding(start = 24.dp, top = 24.dp),
                        fontFamily = robotoFont,
                        fontWeight = FontWeight.Normal,
                        fontSize = 24.sp,
                        color = colorResource(id = R.color.modal_title_color)
                    )
                    Text(
                        text = subTitleText,
                        modifier = Modifier
                            .padding(start = 24.dp, top = 16.dp, end = 24.dp),
                        fontFamily = robotoFont,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = colorResource(id = R.color.modal_sub_title_color)
                    )
                    Row() {

                    }
                }
            }
        }
    }
}