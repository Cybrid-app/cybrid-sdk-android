package app.cybrid.sdkandroid.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.activity.compose.BackHandler
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.accounts.compose.*
import app.cybrid.sdkandroid.components.accounts.view.AccountsViewModel
import app.cybrid.sdkandroid.components.listprices.view.ListPricesViewModel
import app.cybrid.sdkandroid.components.transfer.view.TransferViewModel
import app.cybrid.sdkandroid.core.Constants
import kotlinx.coroutines.launch

class AccountsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0):
Component(context, attrs, defStyle) {

    enum class ViewState { LOADING, CONTENT, TRADES, TRANSFERS, TRANSFER_COMPONENT }

    private var currentState = mutableStateOf(ViewState.LOADING)

    private var accountsViewModel: AccountsViewModel? = null
    private var transferViewModel: TransferViewModel? = null

    init {

        LayoutInflater.from(context).inflate(R.layout.accounts_component, this, true)
        this.composeView = findViewById(R.id.composeContent)
    }

    fun setViewModels(
        listPricesViewModel: ListPricesViewModel,
        accountsViewModel: AccountsViewModel,
        transferViewModel: TransferViewModel
    ) {

        this.accountsViewModel = accountsViewModel
        this.accountsViewModel?.listPricesViewModel = listPricesViewModel
        this.transferViewModel = transferViewModel

        this.currentState = accountsViewModel.uiState
        this.initComposeView()

        accountsViewModel.viewModelScope.launch {
            accountsViewModel.getAccountsList()
        }
    }

    private fun initComposeView() {
        this.composeView?.let { compose ->
            compose.setContent {
                if (this.canRenderUI()) {
                    AccountsView(
                        currentState = this.currentState,
                        accountsViewModel = this.accountsViewModel,
                        transferViewModel = this.transferViewModel
                    )
                } else {
                    FrozenCustomerUI()
                }
            }
        }
    }
}

/**
 * ListPricesView Custom Styles
 * **/
data class AccountsViewStyles(

    var searchBar: Boolean = true,
    var headerTextSize: TextUnit = 16.5.sp,
    var headerTextColor: Color = Color(R.color.list_prices_asset_component_header_color),
    var itemsTextSize: TextUnit = 16.sp,
    var itemsTextColor: Color = Color.Black,
    var itemsTextPriceSize: TextUnit = 15.sp,
    var itemsCodeTextSize: TextUnit = 14.sp,
    var itemsCodeTextColor: Color = Color(R.color.accounts_view_balance_title)
)

/**
 * Composable Functions for Accounts
 * **/
@Composable
fun AccountsView(
    currentState: MutableState<AccountsView.ViewState>,
    accountsViewModel: AccountsViewModel?,
    transferViewModel: TransferViewModel?
) {

    // -- Content
    Surface(
        modifier = Modifier
            .testTag(Constants.AccountsViewTestTags.Surface.id)
    ) {
        
        BackHandler(
            enabled = currentState.value == AccountsView.ViewState.TRADES ||
                    currentState.value == AccountsView.ViewState.TRANSFERS
        ) {

            if (currentState.value == AccountsView.ViewState.TRADES ||
                currentState.value == AccountsView.ViewState.TRANSFERS) {
                currentState.value = AccountsView.ViewState.CONTENT
            }
        }

        when(currentState.value) {

            AccountsView.ViewState.LOADING -> {
                AccountsView_Loading()
            }

            AccountsView.ViewState.CONTENT -> {

                if (accountsViewModel?.accountsAssetPrice?.isEmpty() == true) {
                    AccountsView_List_Empty()
                } else {
                    AccountsView_List(
                        accountsViewModel = accountsViewModel!!
                    )
                }
            }

            AccountsView.ViewState.TRADES -> {

                AccountsView_Trades(
                    accountsViewModel = accountsViewModel
                )
            }

            AccountsView.ViewState.TRANSFERS -> {

                AccountsView_Transfers(
                    accountsViewModel = accountsViewModel!!
                )
            }

            AccountsView.ViewState.TRANSFER_COMPONENT -> {

                BankTransferView(
                    currentState = transferViewModel?.uiState!!,
                    transferViewModel = transferViewModel
                )
            }
        }

        if (accountsViewModel?.showTradeDetail?.value == true) {
            AccountsView_Trades_Detail(
                accountsViewModel = accountsViewModel
            )
        }
    }
}