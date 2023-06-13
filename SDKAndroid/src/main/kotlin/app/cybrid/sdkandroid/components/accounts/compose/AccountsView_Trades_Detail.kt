package app.cybrid.sdkandroid.components.accounts.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cybrid.cybrid_api_bank.client.models.TradeBankModel
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.accounts.view.AccountsViewModel
import app.cybrid.sdkandroid.ui.Theme.robotoFont
import app.cybrid.sdkandroid.ui.lib.BottomSheetDialog
import app.cybrid.sdkandroid.util.getAnnotatedStyle
import app.cybrid.sdkandroid.util.getDateInFormat
import app.cybrid.sdkandroid.util.getImageUrl
import app.cybrid.sdkandroid.util.getSpannableStyle
import coil.compose.rememberAsyncImagePainter
import java.time.OffsetDateTime

@Composable
fun AccountsView_Trades_Detail(
    accountsViewModel: AccountsViewModel
) {

    // -- Content
    BottomSheetDialog(
        onDismissRequest = {
            accountsViewModel.dismissTradeDetail()
        }
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = colorResource(id = R.color.white),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 5.dp, end = 5.dp)
        ) {
            AccountsView_Trades_Detail_Content(
                accountsViewModel = accountsViewModel
            )
        }
    }
}

@Composable
fun AccountsView_Trades_Detail_Content(
    accountsViewModel: AccountsViewModel
) {

    // -- Vars
    val currentTrade = accountsViewModel.currentTrade
    val assetCode = accountsViewModel.currentAccountSelected?.accountAssetCode ?: ""
    val fiatCode = accountsViewModel.currentAccountSelected?.pairAsset?.code ?: ""
    val assetValue = accountsViewModel.getTradeAmount(currentTrade)
    val fiatValue = accountsViewModel.getTradeFiatAmount(currentTrade)
    val imagePainter = rememberAsyncImagePainter(getImageUrl(assetCode.lowercase()))

    val titleType = if (currentTrade.side == TradeBankModel.Side.sell) {
        stringResource(id = R.string.accounts_view_trade_detail_sold)
    } else {
        stringResource(id = R.string.accounts_view_trade_detail_bought)
    }

    val fiatAssetTitle = String.format(
        stringResource(id = R.string.accounts_view_trade_detail_fiat_asset),
        fiatValue, fiatCode, assetCode)

    val fiatAssetValueString = getSpannableStyle(
        text = "$fiatValue ", secondaryText = fiatCode,
        style = getAnnotatedStyle(15.sp))

    val assetValueString = getSpannableStyle(
        text = "$assetValue ", secondaryText = assetCode,
        style = getAnnotatedStyle(15.sp))

    val tradeDate = getDateInFormat(
        date = currentTrade.createdAt ?: OffsetDateTime.now(),
        pattern = "MMMM dd, YYYY hh:mm a"
    )

    // -- Content
    Box {
        Column(
            modifier = Modifier
                .padding(start = 24.dp, end = 19.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = imagePainter,
                    contentDescription = assetCode,
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .padding(0.dp)
                        .size(26.dp),
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = titleType,
                    modifier = Modifier
                        .padding(start = 10.dp),
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = 24.sp,
                    lineHeight = 32.sp,
                    color = colorResource(id = R.color.modal_title_color)
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
            AccountsView_Trades_Detail_Item(
                titleLabel = stringResource(id = R.string.accounts_view_trade_detail_status),
                subTitleLabel = AnnotatedString(currentTrade.state?.value ?: "")
            )
            AccountsView_Trades_Detail_Item(
                titleLabel = stringResource(id = R.string.accounts_view_trade_detail_order_placed),
                subTitleLabel = fiatAssetValueString
            )
            AccountsView_Trades_Detail_Item(
                titleLabel = stringResource(id = R.string.accounts_view_trade_detail_order_finalized),
                subTitleLabel = assetValueString
            )
            AccountsView_Trades_Detail_Item(
                titleLabel = stringResource(id = R.string.accounts_view_trade_detail_order_date),
                subTitleLabel = AnnotatedString(tradeDate)
            )
            AccountsView_Trades_Detail_Item(
                titleLabel = stringResource(id = R.string.accounts_view_trade_detail_order_order_id),
                subTitleLabel = AnnotatedString(currentTrade.guid ?: "")
            )
            // -- Close Button
            Text(
                text = stringResource(id = R.string.accounts_view_trade_detail_order_close_button),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, bottom = 30.dp)
                    .clickable {
                        accountsViewModel.dismissTradeDetail()
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
private fun AccountsView_Trades_Detail_Item(
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