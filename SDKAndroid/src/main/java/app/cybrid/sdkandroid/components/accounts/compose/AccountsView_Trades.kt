package app.cybrid.sdkandroid.components.accounts.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Outbound
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cybrid.cybrid_api_bank.client.models.TradeBankModel
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.AccountsViewStyles
import app.cybrid.sdkandroid.components.accounts.view.AccountsViewModel
import app.cybrid.sdkandroid.ui.Theme.interFont
import app.cybrid.sdkandroid.ui.Theme.robotoFont
import app.cybrid.sdkandroid.util.getDateInFormat
import app.cybrid.sdkandroid.util.getImageUrl
import app.cybrid.sdkandroid.util.getSpannableStyle
import coil.compose.rememberAsyncImagePainter
import java.time.OffsetDateTime

@Composable
fun AccountsView_Trades(
    accountsViewModel: AccountsViewModel?,
) {

    // -- Content
    Column {

        AccountsView_Trades_BalanceAndHoldings(
            accountsViewModel = accountsViewModel!!
        )
        AccountsView_Trades_List(
            accountsViewModel = accountsViewModel,
        )
    }
}

@Composable
fun AccountsView_Trades_BalanceAndHoldings(
    accountsViewModel: AccountsViewModel,
    customStyles: AccountsViewStyles = AccountsViewStyles(),
) {

    // -- Vars
    val cryptoCode = accountsViewModel.currentAccountSelected?.accountAssetCode ?: ""
    val imagePainter = rememberAsyncImagePainter(getImageUrl(cryptoCode.lowercase()))
    val cryptoName = accountsViewModel.currentAccountSelected?.assetName ?: ""
    val assetBalance = getSpannableStyle(
        text = accountsViewModel.currentAccountSelected?.accountBalanceFormattedString ?: "",
        secondaryText = " $cryptoCode",
        style = SpanStyle(
            color = colorResource(id = R.color.list_prices_asset_component_code_color),
            fontFamily = robotoFont,
            fontWeight = FontWeight.Normal,
            fontSize = 19.sp
        )
    )
    val assetBalanceFiat = getSpannableStyle(
        text = accountsViewModel.currentAccountSelected?.accountBalanceInFiatFormatted ?: "",
        secondaryText = " ${accountsViewModel.currentAccountSelected?.pairAsset?.code}",
        style = SpanStyle(
            color = colorResource(id = R.color.list_prices_asset_component_code_color),
            fontFamily = robotoFont,
            fontWeight = FontWeight.Normal
        )
    )

    // -- Content
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        Image(
            painter = imagePainter,
            contentDescription = "{$cryptoName}",
            modifier = Modifier
                .padding(horizontal = 0.dp)
                .padding(0.dp)
                .size(28.dp),
            contentScale = ContentScale.Fit
        )
        Text(
            text = cryptoName,
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
        text = assetBalanceFiat,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        fontFamily = robotoFont,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 24.sp,
        color = Color.Black,
        textAlign = TextAlign.Center
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AccountsView_Trades_List(
    accountsViewModel: AccountsViewModel?,
) {

    LazyColumn(
        modifier = Modifier
            .padding(top = 25.dp, bottom = 20.dp)
    ) {
        stickyHeader {
            AccountsView_Trades_List_Header(
                accountsViewModel = accountsViewModel
            )
        }
        itemsIndexed(items = accountsViewModel?.trades ?: listOf()) { _, item ->
            AccountsView_Trades_List_Item(
                trade = item,
                accountsViewModel = accountsViewModel,
            )
        }
    }
}

@Composable
fun AccountsView_Trades_List_Header(
    accountsViewModel: AccountsViewModel?,
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
                text = stringResource(id = R.string.accounts_view_trades_list_title),
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
                    text = stringResource(id = R.string.accounts_view_trades_list_sub_title),
                    modifier = Modifier.align(Alignment.End),
                    textAlign = TextAlign.End,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = styles.headerTextSize,
                    color = priceColor
                )
                Text(
                    text = accountsViewModel?.currentFiatCurrency ?: "",
                    modifier = Modifier.align(Alignment.End),
                    textAlign = TextAlign.End,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = styles.itemsCodeTextSize,
                    color = styles.itemsCodeTextColor
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
fun AccountsView_Trades_List_Item(
    trade: TradeBankModel,
    accountsViewModel: AccountsViewModel?,
    customStyles: AccountsViewStyles = AccountsViewStyles()
) {

    // -- Vars
    var side = stringResource(id = R.string.accounts_view_trades_list_buy)
    var icon = Icons.Outlined.Outbound
    var iconColor = colorResource(id = R.color.accounts_view_trades_buy)
    var rotate = 90f
    val code = trade.symbol?.split("-")?.get(0) ?: ""
    val date = getDateInFormat(
        date = trade.createdAt ?: OffsetDateTime.now()
    )

    val tradeAmount = accountsViewModel?.getTradeAmount(
        trade = trade
    )
    val tradeFiatAmount = accountsViewModel?.getTradeFiatAmount(
        trade = trade
    )
    val tradeAmountFormatted = getSpannableStyle(
        text = tradeAmount ?: "",
        secondaryText = " $code",
        style = SpanStyle(
            color = colorResource(id = R.color.list_prices_asset_component_code_color),
            fontFamily = robotoFont,
            fontWeight = FontWeight.Normal
        )
    )
    // -- Side Logic
    if (trade.side == TradeBankModel.Side.sell) {

        side = stringResource(id = R.string.accounts_view_trades_list_sell)
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
                    accountsViewModel?.showTradeDetail(trade)
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
                    AccountsView_Trades_List_Item_Chip(
                        state = trade.state ?: TradeBankModel.State.pending
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
                    text = tradeAmountFormatted,
                    modifier = Modifier.align(Alignment.End),
                    textAlign = TextAlign.End,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    color = customStyles.itemsTextColor
                )
                Text(
                    text = tradeFiatAmount ?: "",
                    modifier = Modifier.align(Alignment.End),
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    color = customStyles.itemsCodeTextColor
                )
            }

        }
    }
}

@Composable
fun AccountsView_Trades_List_Item_Chip(
    state: TradeBankModel.State
) {

    var text = stringResource(id = R.string.accounts_view_list_item_failed)
    var backgroundColor = colorResource(id = R.color.accounts_view_list_item_chip_failed)
    var textColor = Color.White

    if (state == TradeBankModel.State.pending || state == TradeBankModel.State.storing) {

        backgroundColor = colorResource(id = R.color.accounts_view_list_item_chip_pending)
        textColor = Color.Black
        text = stringResource(id = R.string.accounts_view_list_item_pending)
    }

    if (state == TradeBankModel.State.pending ||
        state == TradeBankModel.State.storing ||
        state == TradeBankModel.State.failed) {

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