package app.cybrid.sdkandroid.components.bankAccounts.compose

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import app.cybrid.cybrid_api_bank.client.models.PatchExternalBankAccountBankModel
import app.cybrid.sdkandroid.components.BankAccountsView
import app.cybrid.sdkandroid.components.bankAccounts.view.BankAccountsViewModel
import com.plaid.link.OpenPlaidLink
import com.plaid.link.result.LinkExit
import com.plaid.link.result.LinkSuccess
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankAccountsView_Modal(
    bankAccountsViewModel: BankAccountsViewModel
) {

    // -- Vars
    // -- Activity Result for Plaid
    val getPlaidUpdateResult = rememberLauncherForActivityResult(OpenPlaidLink()) {
        when (it) {
            is LinkSuccess -> {
                bankAccountsViewModel.viewModelScope.launch {
                    bankAccountsViewModel.updateExternalBankAccount(
                        state = PatchExternalBankAccountBankModel.State.completed)
                }
            }
            is LinkExit -> {
                bankAccountsViewModel.viewModelScope.launch {
                    bankAccountsViewModel.updateExternalBankAccount(
                        state = PatchExternalBankAccountBankModel.State.refreshRequired)
                }
            }
        }
    }
    bankAccountsViewModel.getPlaidUpdateResult = getPlaidUpdateResult

    // -- Content
    ModalBottomSheet(
        onDismissRequest = { bankAccountsViewModel.dismissExternalBankAccountDetail() },
        containerColor = Color.White,
        windowInsets = WindowInsets(0)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            when(bankAccountsViewModel.accountDetailState.value) {

                BankAccountsView.ModalState.CONTENT -> {
                    BankAccountsView_Modal_Content(
                        bankAccountsViewModel = bankAccountsViewModel
                    )
                }

                BankAccountsView.ModalState.CONFIRM -> {
                    BankAccountsView_Modal_Confirm(
                        bankAccountsViewModel = bankAccountsViewModel
                    )
                }

                BankAccountsView.ModalState.LOADING -> {
                    BankAccountsView_Modal_Loading()
                }
            }
        }
    }
}