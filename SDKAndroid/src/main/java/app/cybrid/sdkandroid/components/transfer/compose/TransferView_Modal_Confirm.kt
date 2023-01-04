package app.cybrid.sdkandroid.components.transfer.compose

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
import app.cybrid.cybrid_api_bank.client.models.ExternalBankAccountBankModel
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.TransferView
import app.cybrid.sdkandroid.components.transfer.view.TransferViewModel
import app.cybrid.sdkandroid.ui.Theme.robotoFont
import app.cybrid.sdkandroid.util.getDateInFormat
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun TransferView_Modal_Confirm(
    transferViewModel: TransferViewModel?,
    externalBankAccount: ExternalBankAccountBankModel?,
    selectedTabIndex: MutableState<Int>
) {

    // -- Vars
    val titleText = if (selectedTabIndex.value == 0) {
        stringResource(id = R.string.transfer_view_component_modal_confirm_deposit_title)
    } else {
        stringResource(id = R.string.transfer_view_component_modal_confirm_withdraw_title)
    }

    // -- Amount
    val amountFormatted = transferViewModel?.transformQuoteAmountInLabelString(transferViewModel.currentQuote)
    val amountValue = buildAnnotatedString {
        append(amountFormatted ?: "")
        withStyle(style = SpanStyle(
            color = colorResource(id = R.color.list_prices_asset_component_code_color),
            fontFamily = robotoFont
        )
        ) {
            append(" " + transferViewModel?.currentFiatCurrency)
        }
    }

    // -- Date
    val dateValue = buildAnnotatedString {
        append(if (selectedTabIndex.value == 0) {
            getDateInFormat(OffsetDateTime.now())
        } else {
            stringResource(id = R.string.transfer_view_component_modal_confirm_withdraw_date_label)
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
            TransferView_Modal_Confirm__Item(
                titleLabel = stringResource(id = R.string.transfer_view_component_modal_confirm_amount_title),
                subTitleLabel = amountValue,
                subTitleTestTag = app.cybrid.sdkandroid.core.Constants.TransferView.ModalContentAmount.id
            )

            // -- Date
            TransferView_Modal_Confirm__Item(
                titleLabel = if (selectedTabIndex.value == 0) {
                    stringResource(id = R.string.transfer_view_component_modal_confirm_date_deposit_title)
                } else {
                    stringResource(id = R.string.transfer_view_component_modal_confirm_date_withdraw_title)
                },
                subTitleLabel = dateValue,
                subTitleTestTag = app.cybrid.sdkandroid.core.Constants.TransferView.ModalContentDate.id
            )

            // -- From-To
            TransferView_Modal_Confirm__Item(
                titleLabel = if (selectedTabIndex.value == 0) {
                    stringResource(id = R.string.transfer_view_component_modal_confirm_from_title)
                } else {
                    stringResource(id = R.string.transfer_view_component_modal_confirm_to_title)
                },
                subTitleLabel = fromTo,
                subTitleTestTag = app.cybrid.sdkandroid.core.Constants.TransferView.ModalContentFromTo.id
            )

            // -- Continue Button
            TransferView_Modal_Confirm__Button(
                transferViewModel = transferViewModel,
                externalBankAccount = externalBankAccount
            )
        }
    }
}

@Composable
private fun TransferView_Modal_Confirm__Item(
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

@DelicateCoroutinesApi
@Composable
private fun TransferView_Modal_Confirm__Button(
    transferViewModel: TransferViewModel?,
    externalBankAccount: ExternalBankAccountBankModel?
) {

    Row(
        modifier = Modifier
            .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 24.dp)
    ) {
        Button(
            onClick = {

                transferViewModel?.modalUiState?.value = TransferView.ModalViewState.LOADING
                GlobalScope.launch {
                    transferViewModel?.createTransfer(externalBankAccount!!)
                }
            },
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
                text = stringResource(id = R.string.transfer_view_component_modal_confirm_button),
                color = Color.White,
                fontFamily = robotoFont,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}