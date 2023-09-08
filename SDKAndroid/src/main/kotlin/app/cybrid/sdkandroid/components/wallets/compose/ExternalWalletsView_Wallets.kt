package app.cybrid.sdkandroid.components.wallets.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import app.cybrid.cybrid_api_bank.client.models.ExternalWalletBankModel
import app.cybrid.cybrid_api_bank.client.models.TransferBankModel
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.ExternalWalletsView
import app.cybrid.sdkandroid.components.wallets.view.ExternalWalletViewModel
import app.cybrid.sdkandroid.ui.Theme.interFont
import app.cybrid.sdkandroid.ui.lib.RoundedButton
import app.cybrid.sdkandroid.util.getImageUrl
import coil.compose.rememberAsyncImagePainter

@Composable
fun ExternalWalletsView_Wallets(
    externalWalletViewModel: ExternalWalletViewModel
) {

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {

        // -- Vars
        val (title, walletList, addButton) = createRefs()

        // -- Content
        // -- Title
        Text(
            modifier = Modifier
                .constrainAs(title) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(parent.top, margin = 15.dp)
                },
            text = "My wallets",
            style = TextStyle(
                fontSize = 26.sp,
                lineHeight = 32.sp,
                fontFamily = FontFamily(Font(R.font.roboto_regular)),
                fontWeight = FontWeight(700),
                color = Color.Black,
                textAlign = TextAlign.Left,
            )
        )

        // -- Wallets List
        LazyColumn(
            modifier = Modifier.constrainAs(walletList) {
                start.linkTo(parent.start, margin = 0.dp)
                top.linkTo(title.bottom, margin = 30.dp)
                end.linkTo(parent.end, margin = 0.dp)
                bottom.linkTo(addButton.top, margin = 10.dp)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
        ) {
            itemsIndexed(items = externalWalletViewModel.externalWalletsActive) { _, item ->
                ExternalWalletsView_Wallets_Item(
                    wallet = item
                )
            }
        }

        // -- Add button
        RoundedButton(
            modifier = Modifier
                .constrainAs(addButton) {
                    start.linkTo(parent.start, margin = 0.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                    bottom.linkTo(parent.bottom, margin = 12.5.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.value(50.dp)
                },
            onClick = { externalWalletViewModel.uiState.value = ExternalWalletsView.State.CREATE },
            text = "Add wallet")
    }
}

@Composable
fun ExternalWalletsView_Wallets_Item(
    wallet: ExternalWalletBankModel
) {

    // -- Vars
    val walletAsset = wallet.asset ?: ""
    val walletAssetImage = rememberAsyncImagePainter(getImageUrl(walletAsset.lowercase()))
    val walletName = wallet.name ?: ""
    val walletStatus = wallet.state ?: ExternalWalletBankModel.State.pending

    // -- Content
    Surface(
        modifier = Modifier
            .height(55.dp),
        color = Color.Transparent
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {

            // -- Refs
            val (icon, name, asset, status) = createRefs()

            // -- Content
            Image(
                painter = walletAssetImage,
                contentDescription = "{$walletAsset}",
                modifier = Modifier.constrainAs(icon) {
                    start.linkTo(parent.start, margin = 0.dp)
                    centerVerticallyTo(parent)
                    width = Dimension.value(25.dp)
                    height = Dimension.value(25.dp)
                },
                contentScale = ContentScale.Fit
            )
            Text(
                modifier = Modifier
                    .constrainAs(name) {
                        start.linkTo(icon.end, margin = 10.dp)
                        centerVerticallyTo(parent)
                    },
                text = walletName,
                style = TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 28.sp,
                    fontFamily = FontFamily(Font(R.font.roboto_regular)),
                    fontWeight = FontWeight(400),
                    color = Color.Black,
                    textAlign = TextAlign.Left,
                )
            )
            Text(
                modifier = Modifier
                    .constrainAs(asset) {
                        start.linkTo(name.end, margin = 5.dp)
                        centerVerticallyTo(parent)
                    },
                text = walletAsset.uppercase(),
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 24.sp,
                    fontFamily = FontFamily(Font(R.font.roboto_regular)),
                    fontWeight = FontWeight(500),
                    color = colorResource(id = R.color.external_wallets_view_wallets_item_asset_color),
                    textAlign = TextAlign.Left,
                )
            )
            ExternalWalletsView_Wallets_Item_Chip(
                state = walletStatus,
                modifier = Modifier
                    .constrainAs(status) {
                        end.linkTo(parent.end, margin = 0.dp)
                        centerVerticallyTo(parent)
                        width = Dimension.value(80.dp)
                        height = Dimension.value(26.dp)
                    }
            )
        }
    }
}

@Composable
fun ExternalWalletsView_Wallets_Item_Chip(
    state: ExternalWalletBankModel.State,
    modifier: Modifier
) {

    var text = ""
    var backgroundColor = colorResource(id = R.color.external_wallets_view_wallets_item_chip_pending)
    var textColor = Color.White
    var hidden = false

    when (state) {

        ExternalWalletBankModel.State.pending -> {

            backgroundColor = colorResource(id = R.color.external_wallets_view_wallets_item_chip_pending)
            textColor = Color.Black
            text = "Pending"
        }

        ExternalWalletBankModel.State.storing -> {

            backgroundColor = colorResource(id = R.color.external_wallets_view_wallets_item_chip_pending)
            textColor = Color.Black
            text = "Pending"
        }

        ExternalWalletBankModel.State.failed -> {

            backgroundColor = colorResource(id = R.color.external_wallets_view_wallets_item_chip_failed)
            textColor = Color.White
            text = "Failed"
        }

        ExternalWalletBankModel.State.completed -> {

            backgroundColor = colorResource(id = R.color.external_wallets_view_wallets_item_chip_completed)
            textColor = Color.White
            text = "Approved"
        }

        else -> {
            hidden = true
        }
    }

    Text(
        text = text,
        modifier = modifier
            .background(
                backgroundColor,
                shape = RoundedCornerShape(43.dp)
            )
            .wrapContentHeight(),
        style = TextStyle(
            fontSize = 13.sp,
            lineHeight = 25.sp,
            fontFamily = FontFamily(Font(R.font.roboto_regular)),
            fontWeight = FontWeight(400),
            color = textColor,
            textAlign = TextAlign.Center
        )
    )
}