package app.cybrid.sdkandroid.ui.lib

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.ui.Theme.robotoFont
import app.cybrid.sdkandroid.util.bottomBorder

@Composable
fun AmountInput(
    modifier: Modifier,
    amountState: MutableState<String>,
    assetState: MutableState<AssetBankModel?>,
    counterAsset: AssetBankModel?,
    isAmountInFiat: MutableState<Boolean>
) {

    // -- Focus Manger
    val focusManager = LocalFocusManager.current

    // -- Content
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth()
            .background(Color.White)
            .clickable {}
            .bottomBorder(1.dp, Color(0xFFC6C6C8))
    ) {

        // -- Asset Code
        val assetCode = if (isAmountInFiat.value) { counterAsset?.code } else { assetState.value?.code }
        Text(
            modifier = Modifier,
            text = assetCode ?: "",
            style = TextStyle(
                fontSize = 17.sp,
                lineHeight = 24.sp,
                fontFamily = FontFamily(Font(R.font.inter_regular)),
                fontWeight = FontWeight(400),
                color = Color(0xFF757575),
                letterSpacing = 0.5.sp,
            )
        )
        // -- Separator
        Box(
            modifier = Modifier
                .padding(start = 15.dp)
                .width(1.dp)
                .height(22.dp)
                .background(
                    color = colorResource(id = R.color.pre_quote_value_input_separator)
                )
        )
        // -- Input
        TextField(
            value = amountState.value.filter { it.isDigit() || it == '.' },
            onValueChange = { value ->
                amountState.value = value.filter { it.isDigit() || it == '.' }
            },
            placeholder = {
                Text(
                    text = stringResource(id = R.string.trade_flow_text_field_amount_placeholder),
                    color = colorResource(id = R.color.black)
                )
            },
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus(true) }
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .padding(start = 0.dp, end = 0.dp)
                .weight(0.88f)
                .testTag("PreQuoteAmountInputTextFieldTag"),
            textStyle = TextStyle(
                fontFamily = robotoFont,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            ),
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.Black,
                cursorColor = colorResource(id = R.color.primary_color),
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
        // -- SwapIcon
        Icon(
            Icons.Filled.SwapVert,
            contentDescription = "",
            tint = colorResource(id = R.color.primary_color),
            modifier = Modifier

                .size(24.dp)
                .padding(end = 0.5.dp)
                .weight(0.12f)
                .clickable {
                    isAmountInFiat.value = !isAmountInFiat.value
                }
        )
    }
}

@Composable
fun AmountLabelInput(
    modifier: Modifier,
    amountState: MutableState<String>,
    assetState: MutableState<AssetBankModel?>,
    counterAsset: AssetBankModel?,
    isAmountInFiat: MutableState<Boolean>,
    titleText: String,
    titleColor: Color = colorResource(id = R.color.external_wallets_view_add_wallet_input_title_color),
    titleSize: TextUnit = 15.5.sp,
    titleWeight: Int = 400,
) {
    ConstraintLayout(
        modifier = modifier
    ) {

        // -- Refs
        val (title, input) = createRefs()

        // -- Content
        Text(
            text = titleText,
            modifier = Modifier
                .constrainAs(title) {
                    start.linkTo(parent.start, margin = 0.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                    top.linkTo(parent.top, margin = 0.dp)
                    width = Dimension.fillToConstraints
                },
            style = TextStyle(
                fontSize = titleSize,
                lineHeight = 22.sp,
                fontFamily = FontFamily(Font(R.font.inter_regular)),
                fontWeight = FontWeight(titleWeight),
                color = titleColor,
                textAlign = TextAlign.Left
            )
        )
        AmountInput(
            modifier = Modifier
                .constrainAs(input) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(title.bottom, margin = 10.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                    height = Dimension.value(60.dp)
                    width = Dimension.fillToConstraints
                },
            amountState = amountState,
            assetState = assetState,
            counterAsset = counterAsset,
            isAmountInFiat = isAmountInFiat
        )
    }
}