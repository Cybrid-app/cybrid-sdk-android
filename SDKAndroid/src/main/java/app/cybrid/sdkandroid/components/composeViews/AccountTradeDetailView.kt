package app.cybrid.sdkandroid.components.composeViews

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import app.cybrid.cybrid_api_bank.client.models.TradeBankModel
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.accounts.view.AccountsViewModel
import app.cybrid.sdkandroid.components.listprices.view.ListPricesViewModel
import app.cybrid.sdkandroid.core.Constants
import app.cybrid.sdkandroid.ui.Theme.robotoFont
import app.cybrid.sdkandroid.util.getDateInFormat
import app.cybrid.sdkandroid.util.getSpannableStyle
import java.time.OffsetDateTime

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AccountTradeDetailView(
    showDialog: MutableState<Boolean>,
    trade: TradeBankModel,
    listPricesViewModel: ListPricesViewModel?,
    accountsViewModel: AccountsViewModel?
) {

    // -- Content
    Dialog(
        onDismissRequest = {
            showDialog.value = false
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = colorResource(id = R.color.modal_color),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp)
        ) {
            AccountTradeDetailContent(
                showDialog = showDialog,
                trade = trade,
                listPricesViewModel = listPricesViewModel,
                accountsViewModel = accountsViewModel
            )
        }
    }
}

@Composable
fun AccountTradeDetailContent(
    showDialog: MutableState<Boolean>,
    trade: TradeBankModel,
    listPricesViewModel: ListPricesViewModel?,
    accountsViewModel: AccountsViewModel?
) {

    // -- Vars
    val assetCode = accountsViewModel?.getCurrentTradeAccount()?.accountAssetCode ?: ""
    val fiatCode = accountsViewModel?.getCurrentTradeAccount()?.pairAsset?.code ?: ""
    val assetValue = accountsViewModel?.getTradeAmount(trade, listPricesViewModel?.assets)
    val fiatValue = accountsViewModel?.getTradeFiatAmount(trade, listPricesViewModel?.assets)

    val titleType = if (trade.side == TradeBankModel.Side.sell) {
        stringResource(id = R.string.accounts_view_trade_detail_sold)
    } else {
        stringResource(id = R.string.accounts_view_trade_detail_bought)
    }

    val title = String.format(titleType, assetCode)
    val fiatAssetTitle = String.format(
        stringResource(id = R.string.accounts_view_trade_detail_fiat_asset),
        fiatValue, fiatCode, assetCode)

    val fiatAssetValueString = getSpannableStyle(
        text = "$fiatValue ", secondaryText = fiatCode,
        style = Constants.getAnnotatedStyle(15.sp))

    val assetValueString = getSpannableStyle(
        text = "$assetValue ", secondaryText = assetCode,
        style = Constants.getAnnotatedStyle(15.sp))

    val tradeDate = getDateInFormat(
        date = trade.createdAt ?: OffsetDateTime.now(),
        pattern = "MMMM dd, YYYY hh:mm a"
    )

    // -- Content
    Box() {
        Column(
            modifier = Modifier
                .padding(start = 24.dp, end = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    modifier = Modifier,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = 24.sp,
                    lineHeight = 32.sp,
                    color = colorResource(id = R.color.modal_title_color)
                )
                Box(
                    modifier = Modifier
                        .weight(4f)
                ) {}
                Icon(
                    Icons.Outlined.Close,
                    contentDescription = null,
                    tint = colorResource(id = R.color.primary_color),
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                        .size(25.dp)
                        .clickable {
                            showDialog.value = false
                        }
                )
            }
            Text(
                text = fiatAssetTitle,
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 16.dp),
                fontFamily = robotoFont,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                color = colorResource(id = R.color.modal_title_color)
            )
            // -- Elements
            AccountTradeDetailContentItem(
                titleLabel = stringResource(id = R.string.accounts_view_trade_detail_status),
                subTitleLabel = AnnotatedString(trade.state?.value ?: "")
            )
            AccountTradeDetailContentItem(
                titleLabel = stringResource(id = R.string.accounts_view_trade_detail_order_placed),
                subTitleLabel = fiatAssetValueString
            )
            AccountTradeDetailContentItem(
                titleLabel = stringResource(id = R.string.accounts_view_trade_detail_order_finalized),
                subTitleLabel = assetValueString
            )
            AccountTradeDetailContentItem(
                titleLabel = stringResource(id = R.string.accounts_view_trade_detail_order_date),
                subTitleLabel = AnnotatedString(tradeDate)
            )
            AccountTradeDetailContentItem(
                titleLabel = stringResource(id = R.string.accounts_view_trade_detail_order_account_id),
                subTitleLabel = AnnotatedString(accountsViewModel?.getCurrentTradeAccount()?.accountGuid ?: "")
            )
            AccountTradeDetailContentItem(
                titleLabel = stringResource(id = R.string.accounts_view_trade_detail_order_order_id),
                subTitleLabel = AnnotatedString(trade.guid ?: "")
            )
            // -- Close Button
            Text(
                text = stringResource(id = R.string.accounts_view_trade_detail_order_close_button),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, bottom = 13.dp)
                    .clickable {
                        showDialog.value = false
                    },
                lineHeight = 20.sp,
                color = colorResource(id = R.color.primary_color),
                fontFamily = robotoFont,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AccountTradeDetailContentItem(
    titleLabel: String,
    subTitleLabel: AnnotatedString
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp)
    ) {
        Text(
            text = titleLabel,
            fontFamily = robotoFont,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 24.sp,
            color = colorResource(id = R.color.account_trade_detail_content_item_color)
        )
        Text(
            text = subTitleLabel,
            modifier = Modifier
                .padding(top = 5.dp),
            fontFamily = robotoFont,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            lineHeight = 16.sp,
            color = Color.Black
        )
    }
}