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
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AccountsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0):
Component(context, attrs, defStyle) {

    enum class ViewState { LOADING, CONTENT, EMPTY, TRADES, TRANSFER }

    private var currentState = mutableStateOf(ViewState.LOADING)

    private var accountsViewModel: AccountsViewModel? = null

    init {

        LayoutInflater.from(context).inflate(R.layout.accounts_component, this, true)
        this.composeView = findViewById(R.id.composeContent)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun setViewModels(
        listPricesViewModel: ListPricesViewModel,
        accountsViewModel: AccountsViewModel
    ) {

        this.accountsViewModel = accountsViewModel
        this.accountsViewModel?.listPricesViewModel = listPricesViewModel
        this.currentState = accountsViewModel.uiState
        this.initComposeView()

        GlobalScope.launch { accountsViewModel.getAccountsList() }
    }

    private fun initComposeView() {

        this.composeView?.let { compose ->
            compose.setContent {
                AccountsView(
                    currentState = this.currentState,
                    accountsViewModel = this.accountsViewModel
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
    currentState: MutableState<AccountsView.ViewState>,
    accountsViewModel: AccountsViewModel?
) {

    // -- Content
    Surface(
        modifier = Modifier
            .testTag(Constants.AccountsViewTestTags.Surface.id)
    ) {
        
        /*BackHandler(enabled = currentState.value == AccountsView.AccountsViewState.TRADES) {

            if (currentState.value == AccountsView.AccountsViewState.TRADES) {

                accountsViewModel.cleanTrades()
                currentRememberState.value = AccountsView.AccountsViewState.CONTENT
            }
        }*/

        when(currentState.value) {

            AccountsView.ViewState.LOADING -> {
                AccountsView_Loading()
            }

            AccountsView.ViewState.CONTENT -> {
                AccountsView_List(
                    accountsViewModel = accountsViewModel!!
                )
            }

            else -> {}

            /*

            AccountsView.AccountsViewState.EMPTY -> {
                AccountsView_List_Empty()
            }

            AccountsView.AccountsViewState.TRADES -> {

                AccountsView_Trades(
                    listPricesViewModel = listPricesViewModel,
                    accountsViewModel = accountsViewModel
                )
            }*/
        }
    }
}