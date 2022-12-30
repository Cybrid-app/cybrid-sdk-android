package app.cybrid.sdkandroid.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import app.cybrid.cybrid_api_bank.client.models.ExternalBankAccountBankModel
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.bankTransfer.compose.BankTransferView_Accounts
import app.cybrid.sdkandroid.components.bankTransfer.compose.BankTransferView_ActionsModal
import app.cybrid.sdkandroid.components.bankTransfer.view.BankTransferViewModel
import app.cybrid.sdkandroid.core.Constants
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BankTransferView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0):
Component(context, attrs, defStyle) {

    enum class ViewState { LOADING, IN_LIST }

    private var currentState = mutableStateOf(ViewState.LOADING)
    var bankTransferViewModel: BankTransferViewModel? = null

    init {

        LayoutInflater.from(context).inflate(R.layout.bank_transfer_component, this, true)
        this.composeView = findViewById(R.id.composeContent)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun setViewModel(bankTransferViewModel: BankTransferViewModel) {

        this.bankTransferViewModel = bankTransferViewModel
        this.currentState = bankTransferViewModel.uiState
        this.initComposeView()
        GlobalScope.launch {

            bankTransferViewModel.fetchAccounts()
            bankTransferViewModel.fetchExternalAccounts()
        }
    }

    private fun initComposeView() {

        this.composeView?.let { compose ->
            compose.setContent {
                BankTransferView(
                    currentState = currentState,
                    bankTransferViewModel = bankTransferViewModel
                )
            }
        }
    }
}

/**
 * Composable Function for Bank Transfer
 **/

@Composable
fun BankTransferView(
    currentState: MutableState<BankTransferView.ViewState>,
    bankTransferViewModel: BankTransferViewModel?
) {

    // -- Vars for views
    val showDialog = remember { mutableStateOf(false) }
    val selectedTabIndex = remember { mutableStateOf(0) }
    val externalBankAccount: MutableState<ExternalBankAccountBankModel?> = remember { mutableStateOf(null) }
    val amountMutableState = remember { mutableStateOf("") }

    // -- Content
    Surface(modifier = Modifier.testTag(Constants.TransferView.Surface.id)) {

        BankTransferView_Accounts(
            bankTransferViewModel = bankTransferViewModel,
            selectedTabIndex = selectedTabIndex,
            externalBankAccount = externalBankAccount,
            amountMutableState = amountMutableState,
            showDialog = showDialog
        )

        // -- UIState
        /*when(currentState.value) {

            BankTransferView.ViewState.LOADING -> {
                BankTransferView_Loading()
            }

            BankTransferView.ViewState.IN_LIST -> {
                BankTransferView_Accounts(
                    bankTransferViewModel = bankTransferViewModel,
                    selectedTabIndex = selectedTabIndex,
                    externalBankAccount = externalBankAccount,
                    amountMutableState = amountMutableState,
                    showDialog = showDialog
                )
            }
        }*/

        // -- Dialog
        if (showDialog.value) {
            BankTransferView_ActionsModal(
                bankTransferViewModel = bankTransferViewModel,
                externalBankAccount = externalBankAccount.value,
                showDialog = showDialog,
                selectedTabIndex = selectedTabIndex,
                amountMutableState = amountMutableState
            )
        }
    }
}