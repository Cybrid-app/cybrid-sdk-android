package app.cybrid.sdkandroid.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.bankAccounts.compose.*
import app.cybrid.sdkandroid.components.bankAccounts.view.BankAccountsViewModel
import com.plaid.link.configuration.LinkTokenConfiguration
import com.plaid.link.linkTokenConfiguration
import com.plaid.link.result.LinkResult
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch

class BankAccountsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0):
Component(context, attrs, defStyle) {

    enum class State { LOADING, CONTENT, DONE, ERROR, AUTH }
    enum class AddAccountButtonState { LOADING, READY }
    enum class ModalState { CONTENT, CONFIRM, LOADING }

    private var currentState = mutableStateOf(State.LOADING)

    var bankAccountsViewModel: BankAccountsViewModel? = null

    init {

        LayoutInflater.from(context).inflate(R.layout.bank_accounts_component, this, true)
        this.composeView = findViewById(R.id.composeContent)
    }

    fun setViewModel(bankAccountsViewModel: BankAccountsViewModel) {

        this.bankAccountsViewModel = bankAccountsViewModel
        this.currentState = bankAccountsViewModel.uiState
        this.initComposeView()

        bankAccountsViewModel.viewModelScope.launch {
            bankAccountsViewModel.fetchExternalBankAccounts()
        }
    }

    private fun initComposeView() {

        this.composeView?.let { compose ->
            compose.setContent {
                if (this.canRenderUI()) {
                    BankAccountsView(
                        currentState = this.currentState,
                        viewModel = bankAccountsViewModel,
                    )
                } else {
                    FrozenCustomerUI()
                }
            }
        }
    }

    companion object {

        fun openPlaid(plaidToken: String, getPlaidResult: ManagedActivityResultLauncher<LinkTokenConfiguration, LinkResult>) {

            val linkTokenConfiguration = linkTokenConfiguration {
                token = plaidToken
            }
            getPlaidResult.launch(linkTokenConfiguration)
        }
    }
}

/**
 * Composable Function for Bank Accounts
 **/

@Composable
fun BankAccountsView(
    currentState: MutableState<BankAccountsView.State>,
    viewModel: BankAccountsViewModel?,
) {

    // -- Content
    Surface {

        when(currentState.value) {

            BankAccountsView.State.LOADING -> {
                BankAccountsView_Loading()
            }

            BankAccountsView.State.CONTENT -> {
                BankAccountsView_Content(viewModel)
            }

            BankAccountsView.State.DONE -> {
                BankAccountsView_Done(viewModel)
            }

            BankAccountsView.State.ERROR -> {
                BankAccountsView_Error()
            }

            BankAccountsView.State.AUTH -> {
                BankAccountsView_Auth(viewModel)
            }
        }

        if (viewModel?.showAccountDetailModal?.value == true) {
            BankAccountsView_Modal(
                bankAccountsViewModel = viewModel
            )
        }
    }
}