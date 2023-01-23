package app.cybrid.sdkandroid.components.bankAccounts.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.cybrid_api_bank.client.models.ExternalBankAccountBankModel
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.trade.compose.TradeView_QuoteModal_Content__Buttons
import app.cybrid.sdkandroid.components.trade.compose.TradeView_QuoteModal_Content__Item
import app.cybrid.sdkandroid.core.AssetPipe
import app.cybrid.sdkandroid.core.BigDecimalPipe
import app.cybrid.sdkandroid.ui.Theme.robotoFont
import java.math.BigDecimal

@Composable
fun BankAccountsView_Modal_Content(account: ExternalBankAccountBankModel) {

    // -- Content
    Box() {
        Column() {
            Text(
                text = stringResource(id = R.string.bank_accounts_modal_content_title),
                modifier = Modifier
                    .padding(start = 24.dp, top = 24.dp),
                fontFamily = robotoFont,
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                color = colorResource(id = R.color.modal_title_color)
            )

            // -- Account name
            TradeView_QuoteModal_Content__Item(
                titleLabel = stringResource(
                    id = if (selectedTabIndex.value == 0) {
                        R.string.trade_flow_quote_confirmation_modal_purchase_amount_title
                    } else {
                        R.string.trade_flow_quote_confirmation_modal_sell_amount_title

                    }),
                subTitleLabel = purchaseValue,
                subTitleTestTag = "PurchaseAmountId"
            )
            // -- Purchase quantity
            TradeView_QuoteModal_Content__Item(
                titleLabel = stringResource(
                    id = if (selectedTabIndex.value == 0) {
                        R.string.trade_flow_quote_confirmation_modal_purchase_quantity_title
                    } else {
                        R.string.trade_flow_quote_confirmation_modal_sell_quantity_title
                    }),
                subTitleLabel = receiveValue,
                subTitleTestTag = "PurchaseQuantityId"
            )
            // -- Transaction Fee
            TradeView_QuoteModal_Content__Item(
                titleLabel = stringResource(
                    id = R.string.trade_flow_quote_confirmation_modal_transaction_fee_title),
                subTitleLabel = transactionFeeValue,
                subTitleTestTag = "PurchaseFeeId"
            )
            // -- Buttons
            TradeView_QuoteModal_Content__Buttons(
                tradeViewModel = tradeViewModel
            )
        }
    }
}

@Composable
internal fun BankAccountsView_Modal_Content_Item(
    titleLabel: String,
    subTitleLabel: AnnotatedString,
    subTitleTestTag: String
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp)
    ) {
        Text(
            text = titleLabel,
            fontFamily = robotoFont,
            fontWeight = FontWeight.Medium,
            fontSize = 14.5.sp,
            lineHeight = 14.sp,
            color = colorResource(id = R.color.modal_title_color)
        )
        Text(
            text = subTitleLabel,
            modifier = Modifier
                .padding(top = 7.dp)
                .testTag(subTitleTestTag),
            fontFamily = robotoFont,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            color = colorResource(id = R.color.modal_sub_title_color)
        )
        Box(
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(color = colorResource(id = R.color.modal_divider))
        )
    }
}