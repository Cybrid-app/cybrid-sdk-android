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
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.accounts.compose.AccountsView_List
import app.cybrid.sdkandroid.components.accounts.compose.AccountsView_List_Empty
import app.cybrid.sdkandroid.components.accounts.compose.AccountsView_Loading
import app.cybrid.sdkandroid.components.accounts.view.AccountsViewModel
import app.cybrid.sdkandroid.components.composeViews.AccountsView_Trades
import app.cybrid.sdkandroid.components.listprices.view.ListPricesViewModel
import app.cybrid.sdkandroid.core.Constants
import app.cybrid.sdkandroid.util.Polling

class AccountsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0):
Component(context, attrs, defStyle) {

    enum class AccountsViewState { LOADING, CONTENT, EMPTY, TRADES }

    private var _listPricesViewModel: ListPricesViewModel? = null
    private var _accountsViewModel: AccountsViewModel? = null
    private var pricesPolling: Polling? = null

    var currentState = mutableStateOf(AccountsViewState.LOADING)

    init {

        LayoutInflater.from(context).inflate(R.layout.accounts_component, this, true)
        this.composeView = findViewById(R.id.composeContent)
    }

    fun setViewModels(
        listPricesViewModel: ListPricesViewModel,
        accountsViewModel: AccountsViewModel
    ) {

        this._listPricesViewModel = listPricesViewModel
        this._accountsViewModel = accountsViewModel
        this.setupCompose()

        //this._listPricesViewModel?.getPricesList()
        this._accountsViewModel?.getAccountsList()

        //this.pricesPolling = Polling { this._listPricesViewModel?.getPricesList() }
    }

    private fun setupCompose() {

        this.composeView?.let { compose ->
            compose.setContent {
                AccountsView(
                    currentState = this.currentState,
                    listPricesViewModel = this._listPricesViewModel,
                    accountsViewModel = this._accountsViewModel
                )
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
    currentState: MutableState<AccountsView.AccountsViewState>,
    listPricesViewModel: ListPricesViewModel?,
    accountsViewModel: AccountsViewModel?
) {

    // -- Vars
    val currentRememberState: MutableState<AccountsView.AccountsViewState> = remember { currentState }

    if (accountsViewModel?.accountsResponse?.isNotEmpty()!!
        && listPricesViewModel?.prices?.isNotEmpty()!!
        && listPricesViewModel.assets.isNotEmpty()) {

        if (accountsViewModel.trades.isEmpty()) {
            currentRememberState.value = AccountsView.AccountsViewState.CONTENT
        } else {
            currentRememberState.value = AccountsView.AccountsViewState.TRADES
        }
    }

    // -- Content
    Surface(
        modifier = Modifier
            .testTag(Constants.AccountsViewTestTags.Surface.id)
    ) {
        
        BackHandler(enabled = currentState.value == AccountsView.AccountsViewState.TRADES) {

            if (currentState.value == AccountsView.AccountsViewState.TRADES) {

                accountsViewModel.cleanTrades()
                currentRememberState.value = AccountsView.AccountsViewState.CONTENT
            }
        }

        when(currentRememberState.value) {

            AccountsView.AccountsViewState.LOADING -> {
                AccountsView_Loading()
            }

            AccountsView.AccountsViewState.CONTENT -> {
                AccountsView_List(
                    listPricesViewModel = listPricesViewModel,
                    accountsViewModel = accountsViewModel,
                    currentRememberState = currentRememberState
                )
            }

            AccountsView.AccountsViewState.EMPTY -> {
                AccountsView_List_Empty()
            }

            AccountsView.AccountsViewState.TRADES -> {

                AccountsView_Trades(
                    listPricesViewModel = listPricesViewModel,
                    accountsViewModel = accountsViewModel
                )
            }
        }
    }
}