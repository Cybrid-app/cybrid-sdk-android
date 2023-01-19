package app.cybrid.sdkandroid.components.trade.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.quote.view.QuoteViewModel
import app.cybrid.sdkandroid.components.trade.view.TradeViewModel
import app.cybrid.sdkandroid.core.AssetPipe
import app.cybrid.sdkandroid.core.BigDecimalPipe
import app.cybrid.sdkandroid.ui.Theme.robotoFont
import java.math.BigDecimal

@Composable
fun TradeView_QuoteModal_Done(
    tradeViewModel: TradeViewModel,
    asset: MutableState<AssetBankModel?>,
    pairAsset: AssetBankModel?,
    selectedTabIndex: MutableState<Int>,
) {

    // -- Purchase amount
    val deliverAmountBD = app.cybrid.sdkandroid.core.BigDecimal(
        tradeViewModel.tradeBankModel.deliverAmount ?: BigDecimal(0)
    )
    val purchaseValue = buildAnnotatedString {
        append(BigDecimalPipe.transform(deliverAmountBD, pairAsset!!))
        withStyle(style = SpanStyle(
            color = colorResource(id = R.color.list_prices_asset_component_code_color),
            fontFamily = robotoFont
        )
        ) {
            append(" ${pairAsset.code}")
        }
    }

    // -- Purchase quantity
    val receiveAmountBD = app.cybrid.sdkandroid.core.BigDecimal(
        tradeViewModel.tradeBankModel.receiveAmount ?: BigDecimal(0)
    )
    val receiveValue = buildAnnotatedString {
        append(AssetPipe.transform(receiveAmountBD, asset.value!!, AssetPipe.AssetPipeTrade).toPlainString())
        withStyle(style = SpanStyle(
            color = colorResource(id = R.color.list_prices_asset_component_code_color),
            fontFamily = robotoFont
        )
        ) {
            append(" ${asset.value!!.code}")
        }
    }

    // -- Transaction fee
    val transactionFeeBD =
        app.cybrid.sdkandroid.core.BigDecimal(tradeViewModel.tradeBankModel.fee ?: BigDecimal(0))
    val transactionFeeValue = buildAnnotatedString {
        append(BigDecimalPipe.transform(transactionFeeBD, pairAsset!!))
        withStyle(style = SpanStyle(
            color = colorResource(id = R.color.list_prices_asset_component_code_color),
            fontFamily = robotoFont
        )
        ) {
            append(" ${pairAsset.code}")
        }
    }

    // -- Content
    Box() {
        Column() {
            Text(
                text = stringResource(id = R.string.trade_flow_confirmation_modal_title),
                modifier = Modifier
                    .padding(start = 24.dp, top = 24.dp),
                fontFamily = robotoFont,
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                color = colorResource(id = R.color.modal_title_color)
            )
            Text(
                text = stringResource(id = R.string.trade_flow_confirmation_modal_sub_title),
                modifier = Modifier
                    .padding(start = 24.dp, top = 16.dp, end = 24.dp),
                fontFamily = robotoFont,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = colorResource(id = R.color.modal_sub_title_color)
            )
            // -- Header items
            TradeView_QuoteModal_Done__Items(
                tradeViewModel = tradeViewModel
            )
            // -- Purchase amount
            TradeView_QuoteModal_Content__Item(
                titleLabel = stringResource(
                    id = if (selectedTabIndex.value == 0) {
                        R.string.trade_flow_quote_confirmation_modal_purchase_amount_title
                    } else {
                        R.string.trade_flow_quote_confirmation_modal_sell_amount_title

                    }),
                subTitleLabel = purchaseValue,
                subTitleTestTag = "PurchaseAmountValueId"
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
                subTitleTestTag = "PurchaseQuantityValueId"
            )
            // -- Transaction Fee
            TradeView_QuoteModal_Content__Item(
                titleLabel = stringResource(
                    id = R.string.trade_flow_quote_confirmation_modal_transaction_fee_title),
                subTitleLabel = transactionFeeValue,
                subTitleTestTag = "PurchaseFeeValueId"
            )
            // -- Buy Button
            Row(
                modifier = Modifier
                    .padding(top = 24.dp, end = 24.dp, bottom = 24.dp)
            ) {

                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(id = R.string.trade_flow_confirmation_modal_button),
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .clickable {
                            tradeViewModel.modalBeDismissed()
                        },
                    lineHeight = 20.sp,
                    color = colorResource(id = R.color.primary_color),
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                )
            }
        }
    }
}

@Composable
private fun TradeView_QuoteModal_Done__Items(
    tradeViewModel: TradeViewModel
) {

    Row(
        modifier = Modifier
            .padding(top = 24.dp, start = 24.dp)
    ) {
        Column() {
            Text(
                text = stringResource(id = R.string.trade_flow_confirmation_modal_transaction_id),
                fontFamily = robotoFont,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = colorResource(id = R.color.modal_title_color)
            )
            Text(
                text = "#${tradeViewModel.tradeBankModel.guid}",
                modifier = Modifier
                    .padding(top = 5.dp),
                fontFamily = robotoFont,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = colorResource(id = R.color.modal_sub_title_color)
            )
        }
    }
}