@file:OptIn(ExperimentalComposeUiApi::class)

package app.cybrid.sdkandroid.components.quote.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.cybrid_api_bank.client.models.PostTradeBankModel
import app.cybrid.cybrid_api_bank.client.models.TradeBankModel
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.core.*
import app.cybrid.sdkandroid.core.AssetPipe.AssetPipeTrade
import app.cybrid.sdkandroid.ui.Theme.robotoFont
import java.math.BigDecimal as JavaBigDecimal

private enum class QuoteConfirmationState {
    PENDING, CONTENT, SUBMITTED, DONE
}

@Composable
fun QuoteConfirmationModal(
    viewModel: QuoteViewModel,
    asset: MutableState<AssetBankModel>,
    pairAsset: AssetBankModel,
    showDialog: MutableState<Boolean>,
    selectedTabIndex: MutableState<Int>,
    updateInterval: Long = 5000
) {

    // -- Modal State
    val modalState:MutableState<QuoteConfirmationState> =
        remember { mutableStateOf(QuoteConfirmationState.PENDING) }

    // -- Check state Pending -> Content
    if (viewModel.quoteBankModel.guid != null) {
        modalState.value = QuoteConfirmationState.CONTENT
    }

    // -- Check state Submitted -> Done
    if (viewModel.tradeBankModel.guid != null) {
        modalState.value = QuoteConfirmationState.DONE
    }

    // -- Content
    Dialog(
        onDismissRequest = {

            viewModel.canUpdateQuote = true
            showDialog.value = false
            dismissModal(modalState, viewModel)
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

            when(modalState.value) {

                QuoteConfirmationState.PENDING -> {
                    QuoteConfirmationLoading(
                        textID = R.string.trade_flow_quote_confirmation_modal_pending_title)
                }

                QuoteConfirmationState.CONTENT -> {
                    QuoteConfirmationContent(
                        viewModel = viewModel,
                        asset = asset,
                        pairAsset = pairAsset,
                        showDialog = showDialog,
                        modalState = modalState,
                        selectedTabIndex = selectedTabIndex,
                        refreshTime = updateInterval)
                }

                QuoteConfirmationState.SUBMITTED -> {
                    QuoteConfirmationLoading(
                        textID = R.string.trade_flow_quote_confirmation_modal_submitted_title)
                }

                QuoteConfirmationState.DONE -> {
                    QuoteConfirmationContentDone(
                        viewModel = viewModel,
                        asset = asset,
                        pairAsset = pairAsset,
                        showDialog = showDialog,
                        selectedTabIndex = selectedTabIndex,
                        modalState = modalState
                    )
                }
            }
        }
    }
}

@Composable
fun QuoteConfirmationLoading(textID:Int) {

    Box(
        modifier = Modifier
            .height(320.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = textID),
                fontFamily = robotoFont,
                fontWeight = FontWeight.Normal,
                fontSize = 17.sp,
                color = colorResource(id = R.color.primary_color)
            )
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .testTag(Constants.QuoteConfirmation.LoadingIndicator.id),
                color = colorResource(id = R.color.primary_color)
            )
        }
    }
}

@Composable
private fun QuoteConfirmationContent(
    viewModel: QuoteViewModel,
    asset: MutableState<AssetBankModel>,
    pairAsset: AssetBankModel,
    showDialog: MutableState<Boolean>,
    modalState:MutableState<QuoteConfirmationState>,
    selectedTabIndex: MutableState<Int>,
    refreshTime: Long = 5000
) {

    // -- Sub Title
    val subTitleText = buildAnnotatedString {
        append(stringResource(id = R.string.trade_flow_quote_confirmation_modal_sub_title))
        withStyle(style = SpanStyle(
            color = colorResource(id = R.color.modal_sub_title_refresh_color),
            fontFamily = robotoFont,
            fontWeight = FontWeight.Bold)
        ) {
            append(" " + (refreshTime/1000) + " ")
        }
        append(stringResource(id = R.string.trade_flow_quote_confirmation_modal_sub_title_unit))
    }

    // -- Purchase amount
    val deliverAmountBD = BigDecimal(viewModel.quoteBankModel.deliverAmount ?: JavaBigDecimal(0))
    val purchaseValue = buildAnnotatedString {
        append(BigDecimalPipe.transform(deliverAmountBD, pairAsset)!!)
        withStyle(style = SpanStyle(
            color = colorResource(id = R.color.list_prices_asset_component_code_color),
            fontFamily = robotoFont)
        ) {
            append(" ${pairAsset.code}")
        }
    }

    // -- Purchase quantity
    val receiveAmountBD = BigDecimal(viewModel.quoteBankModel.receiveAmount ?: JavaBigDecimal(0))
    val receiveValue = buildAnnotatedString {
        append(AssetPipe.transform(receiveAmountBD, asset.value, AssetPipeTrade).toPlainString())
        withStyle(style = SpanStyle(
            color = colorResource(id = R.color.list_prices_asset_component_code_color),
            fontFamily = robotoFont)
        ) {
            append(" ${asset.value.code}")
        }
    }

    // -- Transaction fee
    val transactionFeeBD = BigDecimal(viewModel.quoteBankModel.fee ?: JavaBigDecimal(0))
    val transactionFeeValue = buildAnnotatedString {
        append(BigDecimalPipe.transform(transactionFeeBD, pairAsset)!!)
        withStyle(style = SpanStyle(
            color = colorResource(id = R.color.list_prices_asset_component_code_color),
            fontFamily = robotoFont)
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
            QuoteConfirmationContentItem(
                titleLabel = stringResource(
                    id = if (selectedTabIndex.value == 0) {
                        R.string.trade_flow_quote_confirmation_modal_purchase_amount_title
                    } else {
                        R.string.trade_flow_quote_confirmation_modal_sell_amount_title

                    }),
                subTitleLabel = purchaseValue
            )
            // -- Purchase quantity
            QuoteConfirmationContentItem(
                titleLabel = stringResource(
                    id = if (selectedTabIndex.value == 0) {
                        R.string.trade_flow_quote_confirmation_modal_purchase_quantity_title
                    } else {
                        R.string.trade_flow_quote_confirmation_modal_sell_quantity_title
                    }),
                subTitleLabel = receiveValue
            )
            // -- Transaction Fee
            QuoteConfirmationContentItem(
                titleLabel = stringResource(
                    id = R.string.trade_flow_quote_confirmation_modal_transaction_fee_title),
                subTitleLabel = transactionFeeValue
            )
            // -- Buttons
            QuoteConfirmationButtons(
                viewModel = viewModel,
                showDialog = showDialog,
                modalState = modalState
            )
        }
    }
}

@Composable
private fun QuoteConfirmationContentItem(
    titleLabel: String,
    subTitleLabel: AnnotatedString
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
                .padding(top = 7.dp),
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
private fun QuoteConfirmationButtons(
    viewModel: QuoteViewModel,
    showDialog: MutableState<Boolean>,
    modalState:MutableState<QuoteConfirmationState>
) {

    Row(
        modifier = Modifier
            .padding(top = 24.dp, end = 24.dp, bottom = 24.dp)
    ) {

        Spacer(modifier = Modifier.weight(1f))
        // -- Cancel Button
        Button(
            onClick = {

                showDialog.value = false
                viewModel.canUpdateQuote = true
                modalState.value = QuoteConfirmationState.PENDING
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

                viewModel.canUpdateQuote = false
                modalState.value = QuoteConfirmationState.SUBMITTED

                // --
                val postTradeBankModel = PostTradeBankModel(
                    quoteGuid = viewModel.quoteBankModel.guid ?: ""
                )
                viewModel.createTrade(postTradeBankModel)
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

@Composable
private fun QuoteConfirmationContentDone(
    viewModel: QuoteViewModel,
    asset: MutableState<AssetBankModel>,
    pairAsset: AssetBankModel,
    showDialog: MutableState<Boolean>,
    selectedTabIndex: MutableState<Int>,
    modalState:MutableState<QuoteConfirmationState>
) {

    // -- Purchase amount
    val deliverAmountBD = BigDecimal(viewModel.tradeBankModel.deliverAmount ?: JavaBigDecimal(0))
    val purchaseValue = buildAnnotatedString {
        append(BigDecimalPipe.transform(deliverAmountBD, pairAsset)!!)
        withStyle(style = SpanStyle(
            color = colorResource(id = R.color.list_prices_asset_component_code_color),
            fontFamily = robotoFont)
        ) {
            append(" ${pairAsset.code}")
        }
    }

    // -- Purchase quantity
    val receiveAmountBD = BigDecimal(viewModel.tradeBankModel.receiveAmount ?: JavaBigDecimal(0))
    val receiveValue = buildAnnotatedString {
        append(AssetPipe.transform(receiveAmountBD, asset.value, AssetPipeTrade).toPlainString())
        withStyle(style = SpanStyle(
            color = colorResource(id = R.color.list_prices_asset_component_code_color),
            fontFamily = robotoFont)
        ) {
            append(" ${asset.value.code}")
        }
    }

    // -- Transaction fee
    val transactionFeeBD = BigDecimal(viewModel.tradeBankModel.fee ?: JavaBigDecimal(0))
    val transactionFeeValue = buildAnnotatedString {
        append(BigDecimalPipe.transform(transactionFeeBD, pairAsset)!!)
        withStyle(style = SpanStyle(
            color = colorResource(id = R.color.list_prices_asset_component_code_color),
            fontFamily = robotoFont)
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
            QuoteConfirmationContentDoneItems(
                viewModel = viewModel
            )
            // -- Purchase amount
            QuoteConfirmationContentItem(
                titleLabel = stringResource(
                    id = if (selectedTabIndex.value == 0) {
                        R.string.trade_flow_quote_confirmation_modal_purchase_amount_title
                    } else {
                        R.string.trade_flow_quote_confirmation_modal_sell_amount_title

                    }),
                subTitleLabel = purchaseValue
            )
            // -- Purchase quantity
            QuoteConfirmationContentItem(
                titleLabel = stringResource(
                    id = if (selectedTabIndex.value == 0) {
                        R.string.trade_flow_quote_confirmation_modal_purchase_quantity_title
                    } else {
                        R.string.trade_flow_quote_confirmation_modal_sell_quantity_title
                    }),
                subTitleLabel = receiveValue
            )
            // -- Transaction Fee
            QuoteConfirmationContentItem(
                titleLabel = stringResource(
                    id = R.string.trade_flow_quote_confirmation_modal_transaction_fee_title),
                subTitleLabel = transactionFeeValue
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

                            showDialog.value = false
                            viewModel.canUpdateQuote = true
                            dismissModal(modalState, viewModel)
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
private fun QuoteConfirmationContentDoneItems(
    viewModel: QuoteViewModel
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
                text = "#${viewModel.tradeBankModel.guid}",
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

private fun dismissModal(
    modalState:MutableState<QuoteConfirmationState>,
    viewModel: QuoteViewModel
) {

    if (modalState.value == QuoteConfirmationState.SUBMITTED ||
        modalState.value == QuoteConfirmationState.DONE ||
        modalState.value == QuoteConfirmationState.CONTENT) {
        modalState.value = QuoteConfirmationState.PENDING
    }
    if (viewModel.tradeBankModel.guid != null) {
        viewModel.tradeBankModel = TradeBankModel()
    }
}