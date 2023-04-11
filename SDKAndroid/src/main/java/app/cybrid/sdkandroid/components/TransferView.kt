package app.cybrid.sdkandroid.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewModelScope
import app.cybrid.cybrid_api_bank.client.models.ExternalBankAccountBankModel
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.transfer.compose.TransferView_Accounts
import app.cybrid.sdkandroid.components.transfer.compose.TransferView_Modal
import app.cybrid.sdkandroid.components.transfer.compose.TransferView_Loading
import app.cybrid.sdkandroid.components.transfer.compose.TransferView_Warning
import app.cybrid.sdkandroid.components.transfer.view.TransferViewModel
import app.cybrid.sdkandroid.core.Constants
import kotlinx.coroutines.launch

class TransferView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0):
Component(context, attrs, defStyle) {

    enum class ViewState { LOADING, ACCOUNTS }
    enum class ModalViewState { LOADING, CONFIRM, DETAILS }

    private var currentState = mutableStateOf(ViewState.LOADING)
    var transferViewModel: TransferViewModel? = null
    var canDismissView = false

    init {

        LayoutInflater.from(context).inflate(R.layout.transfer_component, this, true)
        this.composeView = findViewById(R.id.composeContent)
    }

    fun setViewModel(transferViewModel: TransferViewModel) {

        this.transferViewModel = transferViewModel
        this.currentState = transferViewModel.uiState
        this.initComposeView()
        transferViewModel.viewModelScope.launch {
            transferViewModel.fetchAccounts()
        }
    }

    private fun initComposeView() {

        this.composeView?.let { compose ->
            compose.setContent {
                BankTransferView(
                    currentState = currentState,
                    transferViewModel = transferViewModel,
                )

                if (transferViewModel?.viewDismiss?.value == true && canDismissView) {
                    (context as AppCompatActivity).finish()
                }
            }
        }
    }
}

/**
 * Composable Function for Bank Transfer
 **/

@Composable
fun BankTransferView(
    currentState: MutableState<TransferView.ViewState>,
    transferViewModel: TransferViewModel?,
) {

    // -- Vars for views
    val showDialog = remember { mutableStateOf(false) }
    val selectedTabIndex = remember { mutableStateOf(0) }
    val externalBankAccount: MutableState<ExternalBankAccountBankModel?> = remember { mutableStateOf(null) }
    val amountMutableState = remember { mutableStateOf("") }

    // -- Content
    Surface(modifier = Modifier.testTag(Constants.TransferView.Surface.id)) {

        // -- UIState
        when(currentState.value) {

            TransferView.ViewState.LOADING -> {
                TransferView_Loading()
            }

            TransferView.ViewState.ACCOUNTS -> {
                TransferView_Accounts(
                    transferViewModel = transferViewModel,
                    selectedTabIndex = selectedTabIndex,
                    externalBankAccount = externalBankAccount,
                    amountMutableState = amountMutableState,
                    showDialog = showDialog
                )
            }
        }

        // -- Dialog
        if (showDialog.value) {
            TransferView_Modal(
                transferViewModel = transferViewModel,
                externalBankAccount = externalBankAccount,
                showDialog = showDialog,
                selectedTabIndex = selectedTabIndex
            )
        }
    }
}