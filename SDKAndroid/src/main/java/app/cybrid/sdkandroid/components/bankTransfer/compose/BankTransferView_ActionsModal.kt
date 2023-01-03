package app.cybrid.sdkandroid.components.bankTransfer.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import app.cybrid.cybrid_api_bank.client.models.*
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.bankTransfer.view.BankTransferViewModel
import app.cybrid.sdkandroid.components.quote.view.*
import app.cybrid.sdkandroid.core.BigDecimalPipe
import app.cybrid.sdkandroid.core.Constants
import app.cybrid.sdkandroid.ui.Theme.robotoFont
import app.cybrid.sdkandroid.ui.lib.BottomSheetDialog

enum class ViewState { LOADING, CONTENT }

@Composable
fun BankTransferView_ActionsModal(
    bankTransferViewModel: BankTransferViewModel?,
    externalBankAccount: ExternalBankAccountBankModel?,
    showDialog: MutableState<Boolean>,
    selectedTabIndex: MutableState<Int>,
    amountMutableState: MutableState<String>
) {

    // -- Vars
    val modalUiState: MutableState<ViewState> = remember { mutableStateOf(ViewState.LOADING) }

    // -- Compose Content
    BottomSheetDialog(
        onDismissRequest = { showDialog.value = false }
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = colorResource(id = R.color.white),
            modifier = Modifier
                .fillMaxWidth()
        ) {

            when(modalUiState.value) {

                ViewState.CONTENT -> {
                    BankTransferView_ActionsModal_Content(
                        bankTransferViewModel = bankTransferViewModel,
                        externalBankAccount = externalBankAccount,
                        selectedTabIndex = selectedTabIndex,
                        amountMutableState = amountMutableState
                    )
                }

                else -> {}
            }
        }
    }
}

@Composable
fun BankTransferView_ActionsModal_Content(
    bankTransferViewModel: BankTransferViewModel?,
    externalBankAccount: ExternalBankAccountBankModel?,
    selectedTabIndex: MutableState<Int>,
    amountMutableState: MutableState<String>
) {

    // -- Vars
    val titleText = if (selectedTabIndex.value == 0) { 
        stringResource(id = R.string.transfer_view_component_modal_content_trade_title)
    } else { 
        stringResource(id = R.string.transfer_view_component_modal_content_withdraw_title)
    }

    // -- Amount
    val amountFormatted = BigDecimalPipe.transform(amountMutableState.value, Constants.USD_ASSET)
    val purchaseValue = buildAnnotatedString {
        append(amountFormatted ?: "")
        withStyle(style = SpanStyle(
            color = colorResource(id = R.color.list_prices_asset_component_code_color),
            fontFamily = robotoFont)
        ) {
            append(" USD")
        }
    }

    // -- Date
    val date = buildAnnotatedString {
        append(if (selectedTabIndex.value == 0) {
            "December 20, 2022"
        } else {
            "1-2 business days"
        })
    }

    // -- From-To
    val accountMask = externalBankAccount?.plaidAccountMask ?: ""
    val accountName = externalBankAccount?.plaidAccountName ?: ""
    val accountID = externalBankAccount?.plaidInstitutionId ?: ""
    val accountNameToDisplay = "$accountID - $accountName ($accountMask)"
    val fromTo = buildAnnotatedString { append( accountNameToDisplay ) }

    // -- Content
    Box() {
        Column() {
            Text(
                text = titleText,
                modifier = Modifier
                    .padding(start = 24.dp, top = 24.dp),
                fontFamily = robotoFont,
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                color = colorResource(id = R.color.modal_title_color)
            )

            // -- Amount
            BankTransferView_ActionsModal_Content__Item(
                titleLabel = "Amount",
                subTitleLabel = purchaseValue,
                subTitleTestTag = "PurchaseAmountId"
            )

            // -- Date
            BankTransferView_ActionsModal_Content__Item(
                titleLabel = if (selectedTabIndex.value == 0) {
                        "Deposit date"
                    } else {
                        "Withdraw time"
                    },
                subTitleLabel = date,
                subTitleTestTag = "PurchaseAmountId"
            )

            // -- From-To
            BankTransferView_ActionsModal_Content__Item(
                titleLabel = if (selectedTabIndex.value == 0) {
                    "From"
                } else {
                    "To"
                },
                subTitleLabel = fromTo,
                subTitleTestTag = "PurchaseAmountId"
            )

            // -- Continue Button
            Row(
                modifier = Modifier
                    .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 24.dp)
            ) {
                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
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
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun BankTransferView_ActionsModal_Content__Item(
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