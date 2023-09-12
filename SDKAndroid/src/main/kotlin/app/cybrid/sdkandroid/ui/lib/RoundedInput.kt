package app.cybrid.sdkandroid.ui.lib

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import app.cybrid.sdkandroid.R

@Composable
fun RoundedInput(
    modifier: Modifier,
    inputState: MutableState<TextFieldValue>,
    placeholder: String = "",
    fontSize: TextUnit = 19.sp,
    weight: Int = 400,
    textColor: Color = Color.Black,
    backgroundColor: Color = colorResource(id = R.color.external_wallets_view_add_wallet_input_color),
    rightIcon: Int = 0,
    rightIconClick: () -> Unit = {}
) {
    ConstraintLayout(
        modifier = modifier
            .clip(shape = RoundedCornerShape(10))
            .background(backgroundColor)
    ) {
        val (input, icon) = createRefs()

        TextField(
            value = inputState.value,
            onValueChange = { value -> inputState.value = value },
            placeholder = {
                Text(
                    text = placeholder,
                    fontSize = fontSize,
                    color = colorResource(id = R.color.external_wallets_view_add_wallet_input_placeholder_color)
                )
            },
            modifier = Modifier
                .constrainAs(input) {
                    start.linkTo(parent.start, margin = 2.5.dp)
                    top.linkTo(parent.top, margin = 1.dp)
                    end.linkTo(icon.start, margin = 2.5.dp)
                    bottom.linkTo(parent.bottom, margin = 1.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                },
            textStyle = TextStyle(
                fontSize = fontSize,
                lineHeight = 22.sp,
                fontFamily = FontFamily(Font(R.font.inter_regular)),
                fontWeight = FontWeight(weight),
                color = textColor,
                textAlign = TextAlign.Left
            ),
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                textColor = textColor,
                cursorColor = colorResource(id = R.color.primary_color),
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )

        IconButton(
            onClick = rightIconClick,
            modifier = Modifier
                .constrainAs(icon) {
                    end.linkTo(parent.end, margin = 15.dp)
                    centerVerticallyTo(parent)
                    width = Dimension.value( if (rightIcon == 0) 0.dp else 25.dp )
                    height = Dimension.value( if (rightIcon == 0) 0.dp else 25.dp )
                }
        ) {
            if (rightIcon != 0) {
                Image(painter = painterResource(id = rightIcon), contentDescription = "iconDesc")
            }
        }
    }
}

@Composable
fun RoundedLabelInput(
    modifier: Modifier,
    titleText: String,
    titleColor: Color = colorResource(id = R.color.external_wallets_view_add_wallet_input_title_color),
    titleSize: TextUnit = 15.5.sp,
    titleWeight: Int = 400,
    inputState: MutableState<TextFieldValue>,
    placeholder: String = "",
    rightIcon: Int = 0,
    rightIconClick: () -> Unit = {}
) {
    ConstraintLayout(
        modifier = modifier
    ) {
        val (title, input) = createRefs()

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

        RoundedInput(
            modifier = Modifier
                .constrainAs(input) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(title.bottom, margin = 10.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                    height = Dimension.value(60.dp)
                    width = Dimension.fillToConstraints
                },
            inputState = inputState,
            placeholder = placeholder,
            rightIcon = rightIcon,
            rightIconClick = rightIconClick)
    }
}