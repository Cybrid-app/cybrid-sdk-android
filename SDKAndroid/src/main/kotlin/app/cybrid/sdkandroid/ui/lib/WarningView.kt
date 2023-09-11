package app.cybrid.sdkandroid.ui.lib

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import app.cybrid.sdkandroid.R

@Composable
fun WarningView(
    text: String,
    titleText: String,
    modifier: Modifier
) {
    ConstraintLayout(
        modifier = modifier
            .clip(shape = RoundedCornerShape(10))
            .background(colorResource(id = R.color.external_wallets_view_warning_background_color))
    ) {

        // -- Refs
        val (icon, title, label) = createRefs()

        // -- Content
        Image(
            painter = painterResource(id = R.drawable.ic_warning),
            contentDescription = "iconDesc",
            modifier = Modifier
                .constrainAs(icon) {
                    start.linkTo(parent.start, margin = 10.dp)
                    top.linkTo(parent.top, margin = 20.dp)
                    width = Dimension.value(20.dp)
                    height = Dimension.value(20.dp)
                }
        )
        Text(
            text = titleText,
            modifier = Modifier
                .constrainAs(title) {
                    start.linkTo(icon.end, margin = 10.dp)
                    top.linkTo(parent.top, margin = 18.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                    width = Dimension.fillToConstraints
                },
            style = TextStyle(
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontFamily = FontFamily(Font(R.font.inter_regular)),
                fontWeight = FontWeight(800),
                color = Color.Black,
                textAlign = TextAlign.Justify
            )
        )
        Text(
            text = text,
            modifier = Modifier
                .constrainAs(label) {
                    start.linkTo(parent.start, margin = 15.dp)
                    top.linkTo(title.bottom, margin = 15.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                    bottom.linkTo(parent.bottom, margin = 20.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                },
            style = TextStyle(
                fontSize = 15.sp,
                lineHeight = 22.sp,
                fontFamily = FontFamily(Font(R.font.inter_regular)),
                fontWeight = FontWeight(400),
                color = colorResource(id = R.color.external_wallets_view_warning_label_color),
                textAlign = TextAlign.Justify
            )
        )
    }
}