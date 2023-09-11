package app.cybrid.sdkandroid.ui.lib

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.util.getImageUrl
import coil.compose.rememberAsyncImagePainter

@Composable
fun AssetView(
    asset: AssetBankModel,
    modifier: Modifier,
    backgroundColor: Color = colorResource(id = R.color.external_wallets_view_add_wallet_input_color),
    fontSize: TextUnit = 19.sp,
    weight: Int = 400,
    textColor: Color = Color.Black
) {

    // -- Content
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(shape = RoundedCornerShape(10))
            .background(backgroundColor)
    ) {

        // -- vars
        val (icon, text) = createRefs()

        // -- Content
        // -- Icon Image
        val imagePainter = rememberAsyncImagePainter(getImageUrl( asset.code.lowercase()))
        Image(
            painter = imagePainter,
            contentDescription = "imageDesc",
            modifier = Modifier
                .constrainAs(icon) {
                    start.linkTo(parent.start, margin = 15.dp)
                    centerVerticallyTo(parent)
                    width = Dimension.value(25.dp)
                    height = Dimension.value(25.dp)
                }
        )

        // -- Text
        Text(
            text = asset.name,
            modifier = Modifier
                .constrainAs(text) {
                    start.linkTo(parent.start, margin = 55.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                    centerVerticallyTo(parent)
                    width = Dimension.fillToConstraints
                },
            style = TextStyle(
                fontSize = fontSize,
                lineHeight = 22.sp,
                fontFamily = FontFamily(Font(R.font.inter_regular)),
                fontWeight = FontWeight(weight),
                color = textColor,
                textAlign = TextAlign.Left
            )
        )
    }
}

@Composable
fun AssetLabelView(
    modifier: Modifier,
    titleText: String,
    titleColor: Color = colorResource(id = R.color.external_wallets_view_add_wallet_input_title_color),
    titleSize: TextUnit = 15.5.sp,
    titleWeight: Int = 400,
    asset: AssetBankModel,
    backgroundColor: Color = colorResource(id = R.color.external_wallets_view_add_wallet_input_color),
    fontSize: TextUnit = 19.sp,
    weight: Int = 400,
    textColor: Color = Color.Black
) {
    ConstraintLayout(
        modifier = modifier
    ) {
        val (title, select) = createRefs()

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

        AssetView(
            asset = asset,
            modifier = Modifier
                .constrainAs(select) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(title.bottom, margin = 10.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                    height = Dimension.value(60.dp)
                    width = Dimension.fillToConstraints
                },
            backgroundColor = backgroundColor,
            fontSize = fontSize,
            weight = weight,
            textColor = textColor
        )
    }
}