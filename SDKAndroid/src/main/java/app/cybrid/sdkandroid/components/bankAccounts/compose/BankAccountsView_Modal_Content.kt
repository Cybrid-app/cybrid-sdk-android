package app.cybrid.sdkandroid.components.bankAccounts.compose

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import app.cybrid.cybrid_api_bank.client.models.ExternalBankAccountBankModel
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.BankAccountsView
import app.cybrid.sdkandroid.components.bankAccounts.view.BankAccountsViewModel
import app.cybrid.sdkandroid.ui.Theme.robotoFont
import com.plaid.link.OpenPlaidLink
import com.plaid.link.result.LinkExit
import com.plaid.link.result.LinkSuccess
import kotlinx.coroutines.launch

@Composable
fun BankAccountsView_Modal_Content(
    bankAccountsViewModel: BankAccountsViewModel
) {

    // -- Content
    Box {
        Column {
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
            BankAccountsView_Modal_Content_Item(
                titleLabel = stringResource(
                    id = R.string.bank_accounts_modal_content_account_name),
                subTitleLabel = bankAccountsViewModel.currentAccount.plaidAccountName ?: "",
                subTitleTestTag = "PurchaseAmountId"
            )
            // -- Purchase quantity
            BankAccountsView_Modal_Content_Item(
                titleLabel = stringResource(
                    id = R.string.bank_accounts_modal_content_account_status),
                subTitleLabel = bankAccountsViewModel.currentAccount.state?.value ?: "",
                subTitleTestTag = "PurchaseQuantityId"
            )
            // -- Transaction Fee
            BankAccountsView_Modal_Content_Item(
                titleLabel = stringResource(
                    id = R.string.bank_accounts_modal_content_account_number),
                subTitleLabel = bankAccountsViewModel.currentAccount.plaidAccountMask ?: "",
                subTitleTestTag = "PurchaseFeeId"
            )
            // -- Buttons
            BankAccountsView_Modal_Content_Buttons(
                bankAccountsViewModel = bankAccountsViewModel
            )
        }
    }
}

@Composable
internal fun BankAccountsView_Modal_Content_Item(
    titleLabel: String,
    subTitleLabel: String,
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
private fun BankAccountsView_Modal_Content_Buttons(
    bankAccountsViewModel: BankAccountsViewModel
) {

    Row(
        modifier = Modifier
            .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 24.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        // -- Cancel Button
        Button(
            onClick = {
                bankAccountsViewModel.dismissExternalBankAccountDetail()
            },
            modifier = Modifier
                .weight(1f)
                .height(44.dp),
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
        // -- Disconnect Button
        Button(
            onClick = {
                bankAccountsViewModel.accountDetailState.value = BankAccountsView.ModalState.CONFIRM
            },
            modifier = Modifier
                .weight(1f)
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
                text = stringResource(id = R.string.bank_accounts_modal_content_disconnect),
                color = Color.White,
                fontFamily = robotoFont,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
            )
        }

        // -- Refresh Button
        if (bankAccountsViewModel.currentAccount.state == ExternalBankAccountBankModel.State.refreshRequired) {

            Button(
                onClick = {
                    bankAccountsViewModel.viewModelScope.launch {
                        bankAccountsViewModel.refreshAccount()
                    }
                },
                modifier = Modifier
                    .weight(1f)
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
                    text = stringResource(id = R.string.bank_accounts_modal_content_refresh),
                    color = Color.White,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                )
            }
        }
    }
}