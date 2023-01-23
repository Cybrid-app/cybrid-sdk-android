package app.cybrid.sdkandroid.components.bankAccounts.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.BankAccountsView
import app.cybrid.sdkandroid.components.bankAccounts.view.BankAccountsViewModel
import app.cybrid.sdkandroid.components.trade.compose.TradeView_QuoteModal_Loading
import app.cybrid.sdkandroid.ui.lib.BottomSheetDialog

@Composable
fun BankAccountsView_Modal(
    bankAccountsViewModel: BankAccountsViewModel
) {

    // -- Content
    BottomSheetDialog(
        onDismissRequest = {

            bankAccountsViewModel.dismissExternalBankAccountDetail()
        }
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = colorResource(id = R.color.white),
            modifier = Modifier
                .fillMaxWidth()
        ) {

            when(bankAccountsViewModel.accountDetailState.value) {

                BankAccountsView.ModalState.LOADING -> {
                    BankAccountsView_Modal_Loading()
                }

                else -> {}
            }
        }
    }
}