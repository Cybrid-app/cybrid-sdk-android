package app.cybrid.sdkandroid.ui.lib

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import app.cybrid.cybrid_api_bank.client.models.AccountBankModel
import app.cybrid.cybrid_api_bank.client.models.ExternalWalletBankModel
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.core.AssetPipe
import app.cybrid.sdkandroid.core.BigDecimal
import app.cybrid.sdkandroid.core.toBigDecimal
import app.cybrid.sdkandroid.util.getImageUrl
import coil.compose.rememberAsyncImagePainter

@Composable
fun RoundedWalletSelector(
    modifier: Modifier,
    selectExpandedMutableState: MutableState<Boolean>,
    selectedWalletMutableState: MutableState<ExternalWalletBankModel?>,
    backgroundColor: Color = colorResource(id = R.color.external_wallets_view_add_wallet_input_color),
    items: MutableState<List<ExternalWalletBankModel>>,
    onClick: (wallet: ExternalWalletBankModel) -> Unit
) {
    Box(
        modifier = modifier
    ) {

        // -- Vars
        val currencyInputWidth = remember { mutableStateOf(Size.Zero) }

        // -- Content
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(shape = RoundedCornerShape(10))
                .background(backgroundColor)
                .onGloballyPositioned { coordinates ->
                    currencyInputWidth.value = coordinates.size.toSize()
                }
                .clickable { selectExpandedMutableState.value = !selectExpandedMutableState.value }
        ) {

            // -- vars
            val (firstAccount, openClose) = createRefs()
            val iconPainter = if (selectExpandedMutableState.value) {
                Icons.Filled.ArrowDropUp
            } else {
                Icons.Filled.ArrowDropDown
            }

            // -- Content
            if (items.value.isNotEmpty()) {

                selectedWalletMutableState.value?.let {

                    RoundedWalletSelector__Item(
                        modifier = Modifier
                            .constrainAs(firstAccount) {
                                start.linkTo(parent.start, margin = 15.dp)
                                centerVerticallyTo(parent)
                            },
                        wallet = it
                    )
                }

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
                        .clickable {
                            selectExpandedMutableState.value = !selectExpandedMutableState.value
                        }
                )
            }
        }

        // -- Dropdown
        val itemsToUse = if (selectedWalletMutableState.value == null) items.value
        else items.value.filter { it.guid != selectedWalletMutableState.value!!.guid }
        DropdownMenu(
            expanded = selectExpandedMutableState.value,
            onDismissRequest = { selectExpandedMutableState.value = false },
            modifier = Modifier
                .padding(top = 5.dp)
                .width(with(LocalDensity.current) { currencyInputWidth.value.width.toDp() })
        ) {
            itemsToUse.forEach { wallet ->

                DropdownMenuItem(
                    onClick = {
                        onClick(wallet)
                        selectExpandedMutableState.value = false
                    }
                ) {
                    RoundedWalletSelector__Item(
                        wallet = wallet
                    )
                }
            }
        }
    }
}

@Composable
fun RoundedWalletSelector__Item(
    modifier: Modifier = Modifier,
    wallet: ExternalWalletBankModel,
    weight: Int = 400,
    textColor: Color = Color.Black
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        // -- Vars
        val asset = Cybrid.assets.find { it.code == wallet.asset }
        val assetCode = asset?.code ?: ""
        val walletName = wallet.name ?: ""

        // -- Icon Image
        val imagePainter = rememberAsyncImagePainter(getImageUrl(assetCode.lowercase()))
        Image(
            painter = imagePainter,
            contentDescription = "imageDesc",
            modifier = Modifier.size(25.dp)
        )

        // -- Account Asset
        Text(
            text = walletName,
            modifier = Modifier.padding(start = 15.dp),
            style = TextStyle(
                fontSize = 17.sp,
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
fun RoundedWalletSelectorLabelSelector(
    modifier: Modifier,
    selectExpandedMutableState: MutableState<Boolean>,
    selectedWalletMutableState: MutableState<ExternalWalletBankModel?>,
    titleText: String,
    titleColor: Color = colorResource(id = R.color.external_wallets_view_add_wallet_input_title_color),
    titleSize: TextUnit = 15.5.sp,
    titleWeight: Int = 400,
    items: MutableState<List<ExternalWalletBankModel>>,
    onClick: (wallet: ExternalWalletBankModel) -> Unit
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

        RoundedWalletSelector(
            modifier = Modifier
                .constrainAs(select) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(title.bottom, margin = 10.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                    height = Dimension.value(60.dp)
                    width = Dimension.fillToConstraints
                },
            selectExpandedMutableState = selectExpandedMutableState,
            selectedWalletMutableState = selectedWalletMutableState,
            items = items,
            onClick = onClick
        )
    }
}