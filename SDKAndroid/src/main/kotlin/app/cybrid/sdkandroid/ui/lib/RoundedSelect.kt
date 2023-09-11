package app.cybrid.sdkandroid.ui.lib

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import app.cybrid.sdkandroid.ui.Theme.robotoFont
import app.cybrid.sdkandroid.util.getImageUrl
import coil.compose.rememberAsyncImagePainter

@Composable
fun RoundedSelect(
    modifier: Modifier,
    selectExpandedMutableState: MutableState<Boolean>,
    selectedAssetMutableState: MutableState<AssetBankModel?>,
    backgroundColor: Color = colorResource(id = R.color.external_wallets_view_add_wallet_input_color),
    items: List<AssetBankModel>,
    fontSize: TextUnit = 19.sp,
    weight: Int = 400,
    textColor: Color = Color.Black,
) {
    ConstraintLayout(
        modifier = modifier
    ) {

        // -- Vars
        val (inputLayout, dropdown) = createRefs()

        // -- Content
        ConstraintLayout(
            modifier = Modifier
                .constrainAs(inputLayout) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(parent.top, margin = 0.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                }
                .clip(shape = RoundedCornerShape(10))
                .background(backgroundColor)
                .clickable { selectExpandedMutableState.value = !selectExpandedMutableState.value }
        ) {

            // -- vars
            val (icon, text, openClose) = createRefs()
            val iconPainter = if (selectExpandedMutableState.value) {
                Icons.Filled.ArrowDropUp
            } else {
                Icons.Filled.ArrowDropDown
            }

            // -- Content
            if (items.isNotEmpty()) {

                // -- Icon Image
                val imagePainter = rememberAsyncImagePainter(getImageUrl( items[0].code.lowercase()))
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
                    text = items[0].name,
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

                // -- Open/Close Icon
                Icon(
                    iconPainter,
                    contentDescription = "",
                    modifier = Modifier
                        .constrainAs(openClose) {
                            end.linkTo(parent.end, margin = 15.dp)
                            centerVerticallyTo(parent)
                            width = Dimension.value(25.dp)
                            height = Dimension.value(25.dp)
                        }
                        .clickable { selectExpandedMutableState.value = !selectExpandedMutableState.value }
                )
            }
        }

        DropdownMenu(
            expanded = selectExpandedMutableState.value,
            onDismissRequest = { selectExpandedMutableState.value = false },
            modifier = Modifier
                .constrainAs(dropdown) {
                    start.linkTo(parent.start, margin = 1.dp)
                    top.linkTo(inputLayout.bottom, margin = 5.dp)
                    end.linkTo(parent.end, margin = 1.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.value(150.dp)
                }
        ) {
            items.forEach { crypto ->

                val imageName = crypto.code.lowercase()
                val imagePainter = rememberAsyncImagePainter(getImageUrl(imageName))
                DropdownMenuItem(
                    onClick = {
                        selectExpandedMutableState.value = false
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(vertical = 0.dp)
                    ) {

                        Image(
                            painter = imagePainter,
                            contentDescription = "{${crypto.code}}",
                            modifier = Modifier
                                .padding(horizontal = 0.dp)
                                .padding(0.dp)
                                .size(25.dp),
                            contentScale = ContentScale.Fit
                        )
                        Text(
                            text = crypto.name,
                            modifier = Modifier.padding(start = 16.dp),
                            fontFamily = robotoFont,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Text(
                            text = crypto.code,
                            modifier = Modifier.padding(start = 5.5.dp),
                            fontFamily = robotoFont,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = colorResource(id = R.color.list_prices_asset_component_code_color)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RoundedLabelSelect(
    modifier: Modifier,
    selectExpandedMutableState: MutableState<Boolean>,
    selectedAssetMutableState: MutableState<AssetBankModel?>,
    titleText: String,
    titleColor: Color = colorResource(id = R.color.external_wallets_view_add_wallet_input_title_color),
    titleSize: TextUnit = 15.5.sp,
    titleWeight: Int = 400,
    items: List<AssetBankModel>
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

        RoundedSelect(
            modifier = Modifier
                .constrainAs(select) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(title.bottom, margin = 10.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                    height = Dimension.value(60.dp)
                    width = Dimension.fillToConstraints
                },
            selectExpandedMutableState = selectExpandedMutableState,
            selectedAssetMutableState = selectedAssetMutableState,
            items = items
        )
    }
}