package app.cybrid.sdkandroid.components.accounts.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Outbound
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cybrid.cybrid_api_bank.client.models.TransferBankModel
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.AccountsViewStyles
import app.cybrid.sdkandroid.components.accounts.view.AccountsViewModel
import app.cybrid.sdkandroid.components.getImage
import app.cybrid.sdkandroid.core.BigDecimal
import app.cybrid.sdkandroid.core.BigDecimalPipe
import app.cybrid.sdkandroid.ui.Theme.interFont
import app.cybrid.sdkandroid.ui.Theme.robotoFont
import app.cybrid.sdkandroid.util.getDateInFormat
import app.cybrid.sdkandroid.util.getSpannableStyle
import java.time.OffsetDateTime

@Composable
fun AccountsView_Transfers(
    accountsViewModel: AccountsViewModel,
) {

    // -- Content
    Column {

        AccountsView_Transfers_BalanceAndHoldings(
            accountsViewModel = accountsViewModel
        )
        AccountsView_Transfers_List(
            accountsViewModel = accountsViewModel,
        )
    }
}

@Composable
fun AccountsView_Transfers_BalanceAndHoldings(
    accountsViewModel: AccountsViewModel,
    customStyles: AccountsViewStyles = AccountsViewStyles(),
) {

    // -- Vars
    val fiatCode = accountsViewModel.currentAccountSelected?.accountAssetCode ?: ""
    val imageID = getImage(LocalContext.current, "ic_${fiatCode.lowercase()}")
    val fiatName = accountsViewModel.currentAccountSelected?.assetName ?: ""
    val assetBalance = getSpannableStyle(
        text = accountsViewModel.currentAccountSelected?.accountAvailableFormattedString ?: "",
        secondaryText = " $fiatCode",
        style = SpanStyle(
            color = colorResource(id = R.color.list_prices_asset_component_code_color),
            fontFamily = robotoFont,
            fontWeight = FontWeight.Normal,
            fontSize = 19.sp
        )
    )
    
    val balance = accountsViewModel.currentAccountSelected?.accountBalance ?: java.math.BigDecimal(0)
    val available = accountsViewModel.currentAccountSelected?.accountAvailable ?: BigDecimal(0)
    val accountPendingBalance = balance - available.toJavaBigDecimal()
    var accountPendingBalanceString = BigDecimalPipe.transform(BigDecimal(accountPendingBalance), accountsViewModel.currentAccountSelected?.pairAsset!!)
    accountPendingBalanceString = "$accountPendingBalanceString ${stringResource(id = R.string.accounts_view_pending_deposit_label)}"

    // -- Content
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(id = imageID),
            contentDescription = "{$fiatName}",
            modifier = Modifier
                .padding(horizontal = 0.dp)
                .padding(0.dp)
                .size(28.dp),
            contentScale = ContentScale.Fit
        )
        Text(
            text = fiatName,
            modifier = Modifier
                .padding(start = 10.dp),
            fontFamily = robotoFont,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            lineHeight = 28.sp,
            color = customStyles.itemsTextColor
        )
    }
    Text(
        text = assetBalance,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        fontFamily = robotoFont,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        color = Color.Black,
        textAlign = TextAlign.Center
    )
    Text(
        text = accountPendingBalanceString,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        fontFamily = robotoFont,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 24.sp,
        color = colorResource(id = R.color.accounts_pending_deposit_color),
        textAlign = TextAlign.Center
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AccountsView_Transfers_List(
    accountsViewModel: AccountsViewModel?,
) {

    LazyColumn(
        modifier = Modifier
            .padding(top = 25.dp, bottom = 20.dp)
    ) {
        stickyHeader {
            AccountsView_Transfers_List_Header()
        }
        itemsIndexed(items = accountsViewModel?.transfers ?: listOf()) { index, item ->
            AccountsView_Transfers_List_Item(
                transfer = item,
                index = index,
                accountsViewModel = accountsViewModel,
            )
        }
    }
}

@Composable
fun AccountsView_Transfers_List_Header(
    styles: AccountsViewStyles = AccountsViewStyles()
) {

    val priceColor = if (styles.headerTextColor != Color(R.color.list_prices_asset_component_header_color)) {
        styles.headerTextColor
    } else {
        Color.Black
    }

    Surface(color = Color.White) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 10.dp)
        ) {

            Text(
                text = stringResource(id = R.string.accounts_view_transfers_list_title),
                fontFamily = robotoFont,
                fontWeight = FontWeight.Bold,
                fontSize = styles.headerTextSize,
                color = priceColor
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.accounts_view_transfers_amount_title),
                    modifier = Modifier.align(Alignment.End),
                    textAlign = TextAlign.End,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = styles.headerTextSize,
                    color = priceColor
                )
            }
            Box(
                modifier = Modifier
                    .height(1.dp)
                    .background(colorResource(id = R.color.accounts_view_trades_separator))
                    .padding(top = 9.dp)
            )
        }
    }
}

@Composable
fun AccountsView_Transfers_List_Item(
    transfer: TransferBankModel, index: Int,
    accountsViewModel: AccountsViewModel?,
    customStyles: AccountsViewStyles = AccountsViewStyles()
) {

    // -- Vars
    var side = stringResource(id = R.string.accounts_view_transfers_deposit)
    var icon = Icons.Outlined.Outbound
    var iconColor = colorResource(id = R.color.accounts_view_trades_buy)
    var rotate = 90f
    val date = getDateInFormat(
        date = transfer.createdAt ?: OffsetDateTime.now()
    )
    val transferFiatAmount = accountsViewModel?.getTransferFiatAmount(
        transfer = transfer
    )
    val transferFiatAmountCode = buildAnnotatedString {
        append(transferFiatAmount ?: "")
        withStyle(style = SpanStyle(
            color = colorResource(id = R.color.list_prices_asset_component_code_color),
            fontFamily = robotoFont,
            fontWeight = FontWeight.Normal
        )
        ) {
            append(" ${transfer.asset}")
        }
    }
    // -- Side Logic
    if (transfer.side == TransferBankModel.Side.withdrawal) {

        side = stringResource(id = R.string.accounts_view_transfers_withdraw)
        icon = Icons.Outlined.Outbound
        iconColor = colorResource(id = R.color.accounts_view_trades_sell)
        rotate = 0f
    }

    // -- Content
    Surface(color = Color.Transparent) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 0.dp)
                .height(66.dp)
                .clickable {
                    //accountsViewModel?.showTradeDetail(trade)
                },
        ) {

            Icon(
                icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier
                    .padding(horizontal = 0.dp)
                    .padding(0.dp)
                    .size(27.dp)
                    .rotate(rotate)
            )
            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
            ) {
                Row {
                    Text(
                        text = side,
                        modifier = Modifier,
                        fontFamily = robotoFont,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = Color.Black
                    )
                    AccountsView_Transfers_List_Item_Chip(
                        state = transfer.state ?: TransferBankModel.State.pending
                    )
                }
                Text(
                    text = date,
                    modifier = Modifier,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    color = colorResource(id = R.color.list_prices_asset_component_code_color)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text =  transferFiatAmountCode,
                    modifier = Modifier.align(Alignment.End),
                    textAlign = TextAlign.End,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    color = customStyles.itemsTextColor
                )
            }

        }
    }
}

@Composable
fun AccountsView_Transfers_List_Item_Chip(
    state: TransferBankModel.State
) {

    var text = stringResource(id = R.string.accounts_view_list_item_failed)
    var backgroundColor = colorResource(id = R.color.accounts_view_list_item_chip_failed)
    var textColor = Color.White

    if (state == TransferBankModel.State.pending ||
        state == TransferBankModel.State.storing ||
        state == TransferBankModel.State.initiating) {

        backgroundColor = colorResource(id = R.color.accounts_view_list_item_chip_pending)
        textColor = Color.Black
        text = stringResource(id = R.string.accounts_view_list_item_pending)
    }

    if (state == TransferBankModel.State.pending ||
        state == TransferBankModel.State.storing ||
        state == TransferBankModel.State.failed ||
        state == TransferBankModel.State.initiating) {

        Text(
            text = text,
            modifier = Modifier
                .padding(start = 12.dp)
                .background(
                    backgroundColor,
                    shape = RoundedCornerShape(43.dp)
                )
                .width(62.dp)
                .height(18.dp),
            textAlign = TextAlign.Center,
            fontFamily = interFont,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = textColor
        )
    }
}