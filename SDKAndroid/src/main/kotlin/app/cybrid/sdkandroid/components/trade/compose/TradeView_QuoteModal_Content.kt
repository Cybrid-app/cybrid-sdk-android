package app.cybrid.sdkandroid.components.trade.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import androidx.lifecycle.viewModelScope
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.trade.view.TradeViewModel
import app.cybrid.sdkandroid.core.AssetPipe
import app.cybrid.sdkandroid.core.BigDecimalPipe
import app.cybrid.sdkandroid.ui.Theme.robotoFont
import kotlinx.coroutines.launch
import java.math.BigDecimal

@Composable
fun TradeView_QuoteModal_Content(
    tradeViewModel: TradeViewModel,
    asset: MutableState<AssetBankModel?>,
    pairAsset: AssetBankModel?,
    selectedTabIndex: MutableState<Int>,
    updateInterval: Long = 5000
) {

    // -- Sub Title
    val subTitleText = buildAnnotatedString {
        append(stringResource(id = R.string.trade_flow_quote_confirmation_modal_sub_title))
        withStyle(style = SpanStyle(
            color = colorResource(id = R.color.modal_sub_title_refresh_color),
            fontFamily = robotoFont,
            fontWeight = FontWeight.Bold)
        ) {
            append(" " + (updateInterval/1000) + " ")
        }
        append(stringResource(id = R.string.trade_flow_quote_confirmation_modal_sub_title_unit))
    }

    // -- Purchase amount
    val amount = if (selectedTabIndex.value == 0) tradeViewModel.quoteBankModel.deliverAmount else tradeViewModel.quoteBankModel.receiveAmount
    val amountAsset: AssetBankModel = pairAsset!!
    val deliverAmountBD = app.cybrid.sdkandroid.core.BigDecimal(amount ?: BigDecimal(0))
    val purchaseValue = buildAnnotatedString {
        append(BigDecimalPipe.transform(deliverAmountBD, amountAsset))
        withStyle(style = SpanStyle(
            color = colorResource(id = R.color.list_prices_asset_component_code_color),
            fontFamily = robotoFont
        )
        ) {
            append(" ${amountAsset.code}")
        }
    }

    // -- Purchase quantity
    val quantity = if (selectedTabIndex.value == 0) tradeViewModel.quoteBankModel.receiveAmount else tradeViewModel.quoteBankModel.deliverAmount
    val quantityAsset: AssetBankModel = asset.value!!
    val receiveAmountBD = app.cybrid.sdkandroid.core.BigDecimal(quantity ?: BigDecimal(0))
    val receiveValue = buildAnnotatedString {
        append(AssetPipe.transform(receiveAmountBD, quantityAsset, AssetPipe.AssetPipeTrade).toPlainString())
        withStyle(style = SpanStyle(
            color = colorResource(id = R.color.list_prices_asset_component_code_color),
            fontFamily = robotoFont
        )
        ) {
            append(" ${quantityAsset.code}")
        }
    }

    // -- Transaction fee
    val transactionFeeBD =
        app.cybrid.sdkandroid.core.BigDecimal(tradeViewModel.quoteBankModel.fee ?: BigDecimal(0))
    val transactionFeeValue = buildAnnotatedString {
        append(BigDecimalPipe.transform(transactionFeeBD, pairAsset))
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
                text = stringResource(id = R.string.trade_flow_quote_confirmation_modal_title),
                modifier = Modifier
                    .padding(start = 24.dp, top = 24.dp),
                fontFamily = robotoFont,
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                color = colorResource(id = R.color.modal_title_color)
            )
            Text(
                text = subTitleText,
                modifier = Modifier
                    .padding(start = 24.dp, top = 16.dp, end = 24.dp),
                fontFamily = robotoFont,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                color = colorResource(id = R.color.modal_sub_title_color)
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
internal fun TradeView_QuoteModal_Content__Item(
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

@Composable
private fun TradeView_QuoteModal_Content__Buttons(
    tradeViewModel: TradeViewModel
) {

    Row(
        modifier = Modifier
            .padding(top = 24.dp, end = 24.dp, bottom = 24.dp)
    ) {

        Spacer(modifier = Modifier.weight(1f))
        // -- Cancel Button
        Button(
            onClick = {
                tradeViewModel.modalBeDismissed()
            },
            modifier = Modifier
                .padding(end = 18.dp),
            elevation = null,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Transparent,
                contentColor = Color.White
            )
        ) {
            Text(
                text = stringResource(id = R.string.trade_flow_quote_confirmation_modal_cancel),
                color = colorResource(id = R.color.primary_color),
                fontFamily = robotoFont,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            )
        }
        // -- Continue Button
        Button(
            onClick = {
                tradeViewModel.viewModelScope.launch {
                    tradeViewModel.createTrade()
                }
            },
            modifier = Modifier
                .width(120.dp)
                .height(44.dp),
            shape = RoundedCornerShape(4.dp),
            elevation = ButtonDefaults.elevation(
                defaultElevation = 4.dp,
                pressedElevation = 4.dp,
                disabledElevation = 0.dp
            ),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = colorResource(id = R.color.primary_color),
                contentColor = Color.White
            )
        ) {
            Text(
                text = stringResource(id = R.string.trade_flow_quote_confirmation_modal_confirm),
                color = Color.White,
                fontFamily = robotoFont,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
            )
        }
    }
}