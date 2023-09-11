package app.cybrid.sdkandroid.components.wallets.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.ui.lib.RoundedLabelInput
import app.cybrid.sdkandroid.ui.lib.RoundedLabelSelect

@Composable
fun ExternalWalletsView_CreateWallet() {

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {

        // -- Vars
        val assets: List<AssetBankModel> =
            Cybrid.assets
                .filter { it.type == AssetBankModel.Type.crypto }
                .sortedBy { it.name }
        val selectExpandedMutableState = remember { mutableStateOf(false) }
        val selectedAssetMutableState: MutableState<AssetBankModel?> = remember {
            if (assets.isEmpty()) mutableStateOf(null)
            else mutableStateOf(assets[0])
        }
        val nameMutableState = remember { mutableStateOf(TextFieldValue("")) }
        val addressMutableState = remember { mutableStateOf(TextFieldValue("")) }
        val tagMutableState = remember { mutableStateOf(TextFieldValue("")) }

        // -- Refs
        val (title, asset, name, address, tag) = createRefs()

        // -- Content
        // -- Title
        Text(
            modifier = Modifier
                .constrainAs(title) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(parent.top, margin = 15.dp)
                },
            text = "Add new wallet",
            style = TextStyle(
                fontSize = 26.sp,
                lineHeight = 32.sp,
                fontFamily = FontFamily(Font(R.font.roboto_regular)),
                fontWeight = FontWeight(700),
                color = Color.Black,
                textAlign = TextAlign.Left,
            )
        )

        // -- Asset Section
        RoundedLabelSelect(
            modifier = Modifier.constrainAs(asset) {
                start.linkTo(parent.start, margin = 0.dp)
                top.linkTo(title.bottom, margin = 40.dp)
                end.linkTo(parent.end, margin = 0.dp)
                width = Dimension.fillToConstraints
            },
            selectExpandedMutableState = selectExpandedMutableState,
            selectedAssetMutableState = selectedAssetMutableState,
            titleText = "Asset",
            items = assets)

        // -- Name Section
        RoundedLabelInput(
            modifier = Modifier.constrainAs(name) {
                start.linkTo(parent.start, margin = 0.dp)
                top.linkTo(asset.bottom, margin = 20.dp)
                end.linkTo(parent.end, margin = 0.dp)
                width = Dimension.fillToConstraints
            },
            titleText = "Name",
            inputState = nameMutableState,
            placeholder = "Enter wallet name")

        // -- Address Section
        RoundedLabelInput(
            modifier = Modifier.constrainAs(address) {
                start.linkTo(parent.start, margin = 0.dp)
                top.linkTo(name.bottom, margin = 20.dp)
                end.linkTo(parent.end, margin = 0.dp)
                width = Dimension.fillToConstraints
            },
            titleText = "Address",
            inputState = addressMutableState,
            placeholder = "Enter wallet address")

        // -- Tag Section
        RoundedLabelInput(
            modifier = Modifier.constrainAs(tag) {
                start.linkTo(parent.start, margin = 0.dp)
                top.linkTo(address.bottom, margin = 20.dp)
                end.linkTo(parent.end, margin = 0.dp)
                width = Dimension.fillToConstraints
            },
            titleText = "Tag",
            inputState = tagMutableState,
            placeholder = "Enter tag")
    }
}