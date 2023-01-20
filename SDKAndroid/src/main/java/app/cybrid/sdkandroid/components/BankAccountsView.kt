@file:OptIn(DelicateCoroutinesApi::class)

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
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.bankAccounts.compose.BankAccountsView_Done
import app.cybrid.sdkandroid.components.bankAccounts.compose.BankAccountsView_Error
import app.cybrid.sdkandroid.components.bankAccounts.compose.BankAccountsView_Loading
import app.cybrid.sdkandroid.components.bankAccounts.compose.BankAccountsView_Content
import app.cybrid.sdkandroid.components.bankAccounts.view.BankAccountsViewModel
import com.plaid.link.configuration.LinkTokenConfiguration
import com.plaid.link.linkTokenConfiguration
import com.plaid.link.result.LinkResult
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BankAccountsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0):
Component(context, attrs, defStyle) {

    enum class BankAccountsViewState { LOADING, CONTENT, DONE, ERROR }

    private var currentState = mutableStateOf(BankAccountsViewState.LOADING)

    var bankAccountsViewModel: BankAccountsViewModel? = null

    init {

        LayoutInflater.from(context).inflate(R.layout.bank_accounts_component, this, true)
        this.composeView = findViewById(R.id.composeContent)
    }

    fun setViewModel(bankAccountsViewModel: BankAccountsViewModel) {

        this.bankAccountsViewModel = bankAccountsViewModel
        this.currentState = bankAccountsViewModel.uiState
        this.initComposeView()
        GlobalScope.launch {

            bankAccountsViewModel.fetchExternalBankAccounts()
            //bankAccountsViewModel.createWorkflow()
        }
    }

    private fun initComposeView() {

        this.composeView?.let { compose ->
            compose.setContent {
                BankAccountsView(
                    currentState = this.currentState,
                    viewModel = bankAccountsViewModel
                )
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
    currentState: MutableState<BankAccountsView.BankAccountsViewState>,
    viewModel: BankAccountsViewModel?) {

    // -- Content
    Surface {

        when(currentState.value) {

            BankAccountsView.BankAccountsViewState.LOADING -> {
                BankAccountsView_Loading()
            }

            BankAccountsView.BankAccountsViewState.CONTENT -> {
                BankAccountsView_Content(viewModel)
            }

            BankAccountsView.BankAccountsViewState.DONE -> {
                BankAccountsView_Done()
            }

            BankAccountsView.BankAccountsViewState.ERROR -> {
                BankAccountsView_Error()
            }
        }
    }
}