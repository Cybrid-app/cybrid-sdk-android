package app.cybrid.sdkandroid.components.bankAccounts.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.bankAccounts.view.BankAccountsViewModel
import app.cybrid.sdkandroid.ui.Theme.robotoFont
import kotlinx.coroutines.launch

@Composable
fun BankAccountsView_Modal_Confirm(
    bankAccountsViewModel: BankAccountsViewModel
) {

    // -- Vars
    val accountName = "${bankAccountsViewModel.currentAccount.plaidAccountName} (${bankAccountsViewModel.currentAccount.plaidAccountMask})"
    val confirmText = String.format(
        stringResource(id = R.string.bank_accounts_modal_confirm_text),
        accountName
    )

    // -- Content
    Box {
        Column {
            Text(
                text = stringResource(id = R.string.bank_accounts_modal_confirm_title),
                modifier = Modifier
                    .padding(start = 24.dp, top = 24.dp),
                fontFamily = robotoFont,
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                color = colorResource(id = R.color.modal_title_color)
            )
            Text(
                text = confirmText,
                modifier = Modifier
                    .padding(start = 24.dp, top = 45.dp, end = 24.dp),
                fontFamily = robotoFont,
                fontWeight = FontWeight.Normal,
                fontSize = 19.sp,
                color = colorResource(id = R.color.modal_title_color)
            )
            // -- Buttons
            BankAccountsView_Modal_Confirm_Buttons(
                bankAccountsViewModel = bankAccountsViewModel
            )
        }
    }
}

@Composable
private fun BankAccountsView_Modal_Confirm_Buttons(
    bankAccountsViewModel: BankAccountsViewModel
) {

    Row(
        modifier = Modifier
            .padding(top = 24.dp, end = 24.dp, bottom = 24.dp)
    ) {

        Spacer(modifier = Modifier.weight(1f))
        // -- Cancel Button
        Button(
            onClick = {
                bankAccountsViewModel.dismissExternalBankAccountDetail()
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
                bankAccountsViewModel.viewModelScope.launch {
                    bankAccountsViewModel.disconnectExternalBankAccount()
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
                text = stringResource(id = R.string.bank_accounts_modal_confirm_button),
                color = Color.White,
                fontFamily = robotoFont,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
            )
        }
    }
}