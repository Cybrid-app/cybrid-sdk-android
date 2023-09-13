package app.cybrid.sdkandroid.components.wallets.compose

import android.widget.Toast
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import app.cybrid.cybrid_api_bank.client.models.PostExternalWalletBankModel
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.ExternalWalletsView
import app.cybrid.sdkandroid.components.wallets.view.ExternalWalletViewModel
import app.cybrid.sdkandroid.ui.lib.RoundedButton
import app.cybrid.sdkandroid.ui.lib.RoundedLabelInput
import app.cybrid.sdkandroid.ui.lib.RoundedLabelSelect
import app.cybrid.sdkandroid.ui.lib.WarningView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun ExternalWalletsView_CreateWallet(
    externalWalletViewModel: ExternalWalletViewModel
) {

    // -- Scroll
    val scrollMutableState = rememberScrollState()

    // -- Content
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollMutableState)
    ) {

        // -- Vars
        val context = LocalContext.current
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
        val (title, asset, name, address, tag, warning, addButton) = createRefs()

        // -- Content
        // -- Title
        Text(
            modifier = Modifier
                .constrainAs(title) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(parent.top, margin = 15.dp)
                },
            text = stringResource(id = R.string.wallets_view_create_title),
            style = TextStyle(
                fontSize = 26.sp,
                lineHeight = 32.sp,
                fontFamily = FontFamily(Font(R.font.roboto_regular)),
                fontWeight = FontWeight(800),
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
            titleText = stringResource(R.string.wallets_view_create_asset_title),
            items = assets)

        // -- Name Section
        RoundedLabelInput(
            modifier = Modifier.constrainAs(name) {
                start.linkTo(parent.start, margin = 0.dp)
                top.linkTo(asset.bottom, margin = 20.dp)
                end.linkTo(parent.end, margin = 0.dp)
                width = Dimension.fillToConstraints
            },
            titleText = stringResource(R.string.wallets_view_create_name_title),
            inputState = nameMutableState,
            placeholder = stringResource(R.string.wallets_view_create_name_placeholder))

        // -- Address Section
        RoundedLabelInput(
            modifier = Modifier.constrainAs(address) {
                start.linkTo(parent.start, margin = 0.dp)
                top.linkTo(name.bottom, margin = 20.dp)
                end.linkTo(parent.end, margin = 0.dp)
                width = Dimension.fillToConstraints
            },
            titleText = stringResource(id = R.string.wallets_view_create_address_title),
            inputState = addressMutableState,
            placeholder = stringResource(R.string.wallets_view_create_address_placeholder),
            rightIcon = R.drawable.ic_scan,
            rightIconClick = {}
        )

        // -- Tag Section
        RoundedLabelInput(
            modifier = Modifier.constrainAs(tag) {
                start.linkTo(parent.start, margin = 0.dp)
                top.linkTo(address.bottom, margin = 20.dp)
                end.linkTo(parent.end, margin = 0.dp)
                width = Dimension.fillToConstraints
            },
            titleText = stringResource(R.string.wallets_view_create_tag_title),
            inputState = tagMutableState,
            placeholder = stringResource(R.string.wallets_view_create_tag_placeholder))

        // -- Warning
        val warningTitle = stringResource(id = R.string.wallets_view_warning_title)
        val warningLabel = stringResource(id = R.string.wallets_view_warning_label)
        WarningView(
            text = warningLabel,
            titleText = warningTitle,
            modifier = Modifier.constrainAs(warning) {
                start.linkTo(parent.start, margin = 0.dp)
                top.linkTo(tag.bottom, margin = 20.dp)
                end.linkTo(parent.end, margin = 0.dp)
                width = Dimension.fillToConstraints
            }
        )

        // -- Add button
        val nameError = stringResource(R.string.wallets_view_create_name_error)
        val addressError = stringResource(R.string.wallets_view_create_address_error)
        RoundedButton(
            modifier = Modifier
                .constrainAs(addButton) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(warning.bottom, margin = 20.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                    bottom.linkTo(parent.bottom, margin = 15.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.value(50.dp)
                },
            onClick = {

                if (nameMutableState.value.text == "") {
                    Toast.makeText(context, nameError, Toast.LENGTH_SHORT).show()
                    GlobalScope.let { it.launch { scrollMutableState.scrollTo(0) } }
                    return@RoundedButton
                }

                if (addressMutableState.value.text == "") {
                    Toast.makeText(context, addressError, Toast.LENGTH_SHORT).show()
                    GlobalScope.let { it.launch { scrollMutableState.scrollTo(0) } }
                    return@RoundedButton
                }

                // -- Creating Wallet
                val tagValue = tagMutableState.value.text
                val postExternalWalletBankModel = PostExternalWalletBankModel(
                    name = nameMutableState.value.text,
                    asset = selectedAssetMutableState.value!!.code,
                    address = addressMutableState.value.text,
                    tag = if (tagValue == "") null else tagValue,
                    customerGuid = externalWalletViewModel.customerGuid
                )

                // -- Creating
                GlobalScope.let {
                    it.launch {
                        externalWalletViewModel.createWallet(postExternalWalletBankModel)
                    }
                }
            },
            text = stringResource(R.string.wallets_view_create_add_button))
    }
}