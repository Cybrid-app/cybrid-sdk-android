package app.cybrid.sdkandroid.components.wallets.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import app.cybrid.cybrid_api_bank.client.models.PostExternalWalletBankModel
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.wallets.view.ExternalWalletViewModel
import app.cybrid.sdkandroid.ui.lib.AssetLabelView
import app.cybrid.sdkandroid.ui.lib.AssetView
import app.cybrid.sdkandroid.ui.lib.RoundedButton

@Composable
fun ExternalWalletsView_Wallet(
    externalWalletViewModel: ExternalWalletViewModel
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        // -- Vars
        val assets = Cybrid.assets
        val wallet = externalWalletViewModel.currentWallet!!

        // -- Refs
        val (title, asset, name, address, tag, deleteButton) = createRefs()

        // -- Content
        // -- Title
        Text(
            modifier = Modifier
                .constrainAs(title) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(parent.top, margin = 15.dp)
                },
            text = "My wallet",
            style = TextStyle(
                fontSize = 26.sp,
                lineHeight = 32.sp,
                fontFamily = FontFamily(Font(R.font.roboto_regular)),
                fontWeight = FontWeight(800),
                color = Color.Black,
                textAlign = TextAlign.Left,
            )
        )

        // -- Asset
        AssetLabelView(
            modifier = Modifier.constrainAs(asset) {
                start.linkTo(parent.start, margin = 0.dp)
                top.linkTo(title.bottom, margin = 40.dp)
                end.linkTo(parent.end, margin = 0.dp)
                width = Dimension.fillToConstraints
            },
            titleText = "Asset",
            asset = assets.first { it.code == wallet.asset }
        )

        // -- Name
        ExternalWalletsView_Wallet_Item(
            modifier = Modifier
                .constrainAs(name) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(asset.bottom, margin = 25.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                    width = Dimension.fillToConstraints
                },
            titleText = "Name",
            labelText = wallet.name ?: ""
        )

        // -- Address
        ExternalWalletsView_Wallet_Item(
            modifier = Modifier
                .constrainAs(address) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(name.bottom, margin = 25.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                    width = Dimension.fillToConstraints
                },
            titleText = "Address",
            labelText = wallet.address ?: ""
        )

        // -- Address
        ExternalWalletsView_Wallet_Item(
            modifier = Modifier
                .constrainAs(tag) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(address.bottom, margin = 25.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                    width = Dimension.fillToConstraints
                },
            titleText = "Tag",
            labelText = wallet.tag ?: ""
        )

        // -- Delete Button
        RoundedButton(
            modifier = Modifier
                .constrainAs(deleteButton) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(tag.bottom, margin = 25.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.value(50.dp)
                },
            onClick = {},
            backgroundColor = colorResource(id = R.color.external_wallets_view_wallet_delete_button_color),
            text = "Delete"
        )
    }
}

@Composable
fun ExternalWalletsView_Wallet_Item(
    modifier: Modifier,
    titleText: String,
    labelText: String
) {
    ConstraintLayout(
        modifier = modifier
    ) {
        val (title, label) = createRefs()

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
                fontSize = 15.5.sp,
                lineHeight = 22.sp,
                fontFamily = FontFamily(Font(R.font.inter_regular)),
                fontWeight = FontWeight(400),
                color = colorResource(id = R.color.external_wallets_view_add_wallet_input_title_color),
                textAlign = TextAlign.Left
            )
        )

        Text(
            text = labelText,
            modifier = Modifier
                .constrainAs(label) {
                    start.linkTo(parent.start, margin = 0.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                    top.linkTo(title.bottom, margin = 10.dp)
                    width = Dimension.fillToConstraints
                },
            style = TextStyle(
                fontSize = 17.5.sp,
                lineHeight = 22.sp,
                fontFamily = FontFamily(Font(R.font.inter_regular)),
                fontWeight = FontWeight(500),
                color = Color.Black,
                textAlign = TextAlign.Left
            )
        )
    }
}