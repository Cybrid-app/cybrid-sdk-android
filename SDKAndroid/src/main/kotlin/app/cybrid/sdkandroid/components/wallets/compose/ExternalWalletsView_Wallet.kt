package app.cybrid.sdkandroid.components.wallets.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.Visibility
import app.cybrid.cybrid_api_bank.client.models.ExternalWalletBankModel
import app.cybrid.cybrid_api_bank.client.models.TransferBankModel
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.Component
import app.cybrid.sdkandroid.components.ExternalWalletsView
import app.cybrid.sdkandroid.components.wallets.view.ExternalWalletViewModel
import app.cybrid.sdkandroid.core.AssetPipe
import app.cybrid.sdkandroid.ui.Theme.robotoFont
import app.cybrid.sdkandroid.ui.lib.AssetLabelView
import app.cybrid.sdkandroid.ui.lib.RoundedButton
import app.cybrid.sdkandroid.util.getDateInFormat
import app.cybrid.sdkandroid.util.getSpannableStyle
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.OffsetDateTime

@OptIn(DelicateCoroutinesApi::class)
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
        val walletTagIsVisible = wallet.tag?.isNotEmpty() ?: false
        val walletTagVisibility = if (walletTagIsVisible) Visibility.Visible else Visibility.Gone

        // -- Refs
        val (title, status, asset, name,
            address, tag, recentTransfersTitle,
            recentTransfers, deleteButton) = createRefs()

        // -- Content
        // -- Title
        Text(
            modifier = Modifier
                .constrainAs(title) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(parent.top, margin = 15.dp)
                },
            text = stringResource(R.string.wallets_view_wallet_title),
            style = TextStyle(
                fontSize = 26.sp,
                lineHeight = 32.sp,
                fontFamily = FontFamily(Font(R.font.roboto_regular)),
                fontWeight = FontWeight(800),
                color = Color.Black,
                textAlign = TextAlign.Left,
            )
        )

        // -- Wallet Chip
        ExternalWalletsView_Wallets_Item_Chip(
            state = wallet.state ?: "pending",
            modifier = Modifier
                .constrainAs(status) {
                    top.linkTo(parent.top, margin = 25.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                    width = Dimension.value(80.dp)
                    height = Dimension.value(26.dp)
                }
        )

        // -- Asset
        AssetLabelView(
            modifier = Modifier.constrainAs(asset) {
                start.linkTo(parent.start, margin = 0.dp)
                top.linkTo(title.bottom, margin = 40.dp)
                end.linkTo(parent.end, margin = 0.dp)
                width = Dimension.fillToConstraints
            },
            titleText = stringResource(R.string.wallets_view_create_asset_title),
            asset = assets.first { it.code == wallet.asset }
        )

        // -- Name
        ExternalWalletsView_Wallet_TextItem(
            modifier = Modifier
                .constrainAs(name) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(asset.bottom, margin = 25.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                    width = Dimension.fillToConstraints
                },
            titleText = stringResource(R.string.wallets_view_create_name_title),
            labelText = wallet.name ?: ""
        )

        // -- Address
        ExternalWalletsView_Wallet_TextItem(
            modifier = Modifier
                .constrainAs(address) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(name.bottom, margin = 25.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                    width = Dimension.fillToConstraints
                },
            titleText = stringResource(R.string.wallets_view_create_address_title),
            labelText = wallet.address ?: ""
        )

        // -- Tag
        ExternalWalletsView_Wallet_TextItem(
            modifier = Modifier
                .constrainAs(tag) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(address.bottom, margin = 25.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                    width = Dimension.fillToConstraints
                    visibility = walletTagVisibility
                },
            titleText = stringResource(R.string.wallets_view_create_tag_title),
            labelText = wallet.tag ?: ""
        )

        // -- Recent transfers
        Text(
            text = stringResource(id = R.string.wallets_view_wallet_recent_transfers_title),
            modifier = Modifier
                .constrainAs(recentTransfersTitle) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(tag.bottom, margin = 25.dp, goneMargin = 25.dp)
                    end.linkTo(parent.end, margin = 0.dp)
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

        when(externalWalletViewModel.transfersUiState.value) {

            ExternalWalletsView.TransfersState.LOADING -> {
                ExternalWalletsView_Wallet_Loading_Transfers(
                    modifier = Modifier.constrainAs(recentTransfers) {
                        start.linkTo(parent.start, margin = 0.dp)
                        top.linkTo(recentTransfersTitle.bottom, margin = 25.dp)
                        end.linkTo(parent.end, margin = 0.dp)
                        width = Dimension.fillToConstraints
                    }
                )
            }

            ExternalWalletsView.TransfersState.EMPTY -> {

                ExternalWalletsView_Wallet_Empty_Transfers(
                    modifier = Modifier.constrainAs(recentTransfers) {
                        start.linkTo(parent.start, margin = 0.dp)
                        top.linkTo(recentTransfersTitle.bottom, margin = 25.dp)
                        end.linkTo(parent.end, margin = 0.dp)
                        width = Dimension.fillToConstraints
                    }
                )
            }

            ExternalWalletsView.TransfersState.TRANSFERS -> {

                LazyColumn(
                    modifier = Modifier.constrainAs(recentTransfers) {
                        start.linkTo(parent.start, margin = 0.dp)
                        top.linkTo(recentTransfersTitle.bottom, margin = 7.5.dp)
                        end.linkTo(parent.end, margin = 0.dp)
                        width = Dimension.fillToConstraints
                        height = Dimension.value( (66 * externalWalletViewModel.transfers.value.count()).dp )
                    },
                    userScrollEnabled = false,
                ) {
                    itemsIndexed(items = externalWalletViewModel.transfers.value) { _, item ->
                        ExternalWalletsView_Wallet_Transfer_Item(
                            transfer = item
                        )
                    }
                }
            }
        }

        // -- Delete Button
        RoundedButton(
            modifier = Modifier
                .constrainAs(deleteButton) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(recentTransfers.bottom, margin = 25.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                    bottom.linkTo(parent.bottom, margin = 20.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.value(50.dp)
                },
            onClick = {
                GlobalScope.let { it.launch { externalWalletViewModel.deleteExternalWallet() } }
            },
            backgroundColor = colorResource(id = R.color.external_wallets_view_wallet_delete_button_color),
            text = stringResource(id = R.string.wallets_view_wallet_delete_button)
        )
    }
}

@Composable
fun ExternalWalletsView_Wallet_TextItem(
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

@Composable
fun ExternalWalletsView_Wallet_Loading_Transfers(
    modifier: Modifier
) {
    ConstraintLayout(
        modifier = modifier
    ) {

        // -- Vars
        val (loader) = createRefs()

        // -- Content
        Component.CreateLoader(modifier =
        Modifier.constrainAs(loader) {
            centerHorizontallyTo(parent)
            centerVerticallyTo(parent)
        },
            message = stringResource(id = R.string.wallets_view_wallet_recent_transfers_loading_label)
        )
    }
}

@Composable
fun ExternalWalletsView_Wallet_Empty_Transfers(
    modifier: Modifier
) {
    ConstraintLayout(
        modifier = modifier
    ) {

        // -- Refs
        val (icon, label) = createRefs()

        // -- Content
        Image(
            painter = painterResource(id = R.drawable.ic_empty),
            contentDescription = "iconDesc",
            modifier = Modifier.constrainAs(icon) {
                centerHorizontallyTo(parent)
                top.linkTo(parent.top, margin = 0.dp)
                width = Dimension.value(45.dp)
                height = Dimension.value(45.dp)
            }
        )
        Text(
            text = stringResource(id = R.string.wallets_view_wallet_recent_transfers_empty_label),
            modifier = Modifier
                .constrainAs(label) {
                    start.linkTo(parent.start, margin = 10.dp)
                    top.linkTo(icon.bottom, margin = 15.dp)
                    end.linkTo(parent.end, margin = 10.dp)
                    width = Dimension.fillToConstraints
                },
            style = TextStyle(
                fontSize = 15.5.sp,
                lineHeight = 22.sp,
                fontFamily = FontFamily(Font(R.font.inter_regular)),
                fontWeight = FontWeight(400),
                color = colorResource(id = R.color.external_wallets_view_add_wallet_input_title_color),
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
fun ExternalWalletsView_Wallet_Transfer_Item(
    transfer: TransferBankModel
) {

    // -- Vars
    val assetCode = transfer.asset ?: ""
    val asset = Cybrid.assets.find { it.code == assetCode }
    val amount = transfer.amount ?: BigDecimal.ZERO
    val amountValue = AssetPipe.transform(
        value = amount,
        asset = asset!!,
        unit = AssetPipe.AssetPipeTrade
    ).toPlainString()
    val amountValueFormatted = getSpannableStyle(
        text = amountValue,
        secondaryText = " $assetCode",
        style = SpanStyle(
            color = colorResource(id = R.color.list_prices_asset_component_code_color),
            fontFamily = robotoFont,
            fontWeight = FontWeight.Normal
        )
    )
    val date = getDateInFormat(
        date = transfer.createdAt ?: OffsetDateTime.now()
    )

    // -- Content
    Surface(color = Color.Transparent) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(66.dp)
                .fillMaxWidth(),
        ) {

            Image(
                modifier = Modifier.size(32.dp),
                painter = painterResource(id = R.drawable.ic_crypto_transfer),
                contentDescription = ""
            )

            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = "Withdraw",
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
                    text = date,
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 24.sp,
                        fontFamily = FontFamily(Font(R.font.roboto_regular)),
                        fontWeight = FontWeight(500),
                        color = colorResource(id = R.color.external_wallets_view_wallets_item_asset_color),
                        textAlign = TextAlign.Left,
                    )
                )
            }
            Text(
                text = amountValueFormatted,
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(
                    fontSize = 17.5.sp,
                    lineHeight = 28.sp,
                    fontFamily = FontFamily(Font(R.font.roboto_regular)),
                    fontWeight = FontWeight(600),
                    color = Color.Black,
                    textAlign = TextAlign.End,
                )
            )
        }
    }
}