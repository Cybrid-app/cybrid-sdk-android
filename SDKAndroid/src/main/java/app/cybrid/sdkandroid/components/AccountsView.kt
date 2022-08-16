package app.cybrid.sdkandroid.components

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Outbound
import androidx.compose.material.icons.outlined.Outbound
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cybrid.cybrid_api_bank.client.models.TradeBankModel
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.accounts.entity.AccountAssetPriceModel
import app.cybrid.sdkandroid.components.accounts.view.AccountsViewModel
import app.cybrid.sdkandroid.components.composeViews.AccountTradesView
import app.cybrid.sdkandroid.components.listprices.view.ListPricesViewModel
import app.cybrid.sdkandroid.core.Constants
import app.cybrid.sdkandroid.ui.Theme.robotoFont
import app.cybrid.sdkandroid.util.getDateInFormat
import app.cybrid.sdkandroid.util.getSpannableStyle
import java.time.OffsetDateTime

class AccountsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : Component(context, attrs, defStyle) {

    enum class AccountsViewState { LOADING, CONTENT, TRADES }

    private var _listPricesViewModel:ListPricesViewModel? = null
    private var _accountsViewModel:AccountsViewModel? = null

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

        this._listPricesViewModel?.getPricesList()
        this._accountsViewModel?.getAccountsList()

        this.setupRunnable { this._listPricesViewModel?.getPricesList() }
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
                AccountsViewLoading()
            }

            AccountsView.AccountsViewState.CONTENT -> {
                AccountsViewList(
                    listPricesViewModel = listPricesViewModel,
                    accountsViewModel = accountsViewModel,
                    currentRememberState = currentRememberState
                )
            }

            AccountsView.AccountsViewState.TRADES -> {

                AccountTradesView(
                    listPricesViewModel = listPricesViewModel,
                    accountsViewModel = accountsViewModel
                )
            }
        }
    }
}

@Composable
fun AccountsViewLoading() {

    Box(
        modifier = Modifier
            .height(120.dp)
            .testTag(Constants.AccountsViewTestTags.Loading.id)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.accounts_view_loading_text),
                fontFamily = robotoFont,
                fontWeight = FontWeight.Normal,
                fontSize = 17.sp,
                color = colorResource(id = R.color.primary_color)
            )
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .testTag(Constants.QuoteConfirmation.LoadingIndicator.id),
                color = colorResource(id = R.color.primary_color)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AccountsViewList(
    listPricesViewModel: ListPricesViewModel?,
    accountsViewModel: AccountsViewModel?,
    currentRememberState: MutableState<AccountsView.AccountsViewState>
) {

    // -- Mutable Vars
    var selectedIndex by remember { mutableStateOf(-1) }

    // -- Items
    accountsViewModel?.createAccountsFormatted(
        prices = listPricesViewModel?.prices!!,
        assets = listPricesViewModel.assets
    )

    // -- Get Total balance
    accountsViewModel?.getCalculatedBalance()

    Column(
        modifier = Modifier
            .background(Color.Transparent)
            .testTag(Constants.AccountsViewTestTags.List.id)
    ) {
        AccountsBalance(
            accountsViewModel = accountsViewModel
        )
        LazyColumn(
            modifier = Modifier
        ) {
            stickyHeader {
                AccountsCryptoHeaderItem(
                    accountsViewModel = accountsViewModel
                )
            }
            itemsIndexed(items = accountsViewModel?.accounts ?: listOf()) { index, item ->
                AccountsCryptoItem(
                    balance = item,
                    index = index,
                    selectedIndex = selectedIndex,
                    accountsViewModel = accountsViewModel,
                    currentRememberState = currentRememberState
                )
            }
        }
    }
}

@Composable
fun AccountsBalance(
    accountsViewModel: AccountsViewModel?
) {

    // -- Vars
    val balanceFormatted = buildAnnotatedString {
        append(accountsViewModel?.totalBalance ?: "")
        withStyle(style = SpanStyle(
            color = colorResource(id = R.color.list_prices_asset_component_code_color),
            fontFamily = robotoFont,
            fontWeight = FontWeight.Normal,
            fontSize = 17.sp
        )
        ) {
            append(" ${accountsViewModel?.currentFiatCurrency}")
        }
    }

    // -- Content
    if (accountsViewModel?.totalBalance != "") {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 25.5.dp)
        ) {

            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = stringResource(id = R.string.accounts_view_balance_title),
                    modifier = Modifier,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = colorResource(id = R.color.accounts_view_balance_title)
                )

                Text(
                    text = balanceFormatted,
                    modifier = Modifier.
                        padding(top = 1.dp),
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = 23.sp,
                    lineHeight = 32.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun AccountsCryptoHeaderItem(
    styles: AccountsViewStyles = AccountsViewStyles(),
    accountsViewModel: AccountsViewModel?,
) {

    val priceColor = if (styles.headerTextColor != Color(R.color.list_prices_asset_component_header_color)) {
        styles.headerTextColor
    } else {
        Color.Black
    }

    Surface(color = Color.White) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {

            Column {
                Text(
                    text = stringResource(id = R.string.accounts_view_list_header_asset),
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = styles.headerTextSize,
                    lineHeight = 20.sp,
                    color = priceColor
                )
                Text(
                    text = stringResource(id = R.string.accounts_view_list_header_asset_sub),
                    modifier = Modifier,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = styles.itemsCodeTextSize,
                    lineHeight = 20.sp,
                    color = colorResource(id = R.color.accounts_view_balance_title)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.accounts_view_list_header_balance),
                    modifier = Modifier.align(Alignment.End),
                    textAlign = TextAlign.End,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = styles.headerTextSize,
                    color = priceColor
                )
                Text(
                    text = accountsViewModel?.currentFiatCurrency ?: "",
                    modifier = Modifier.align(Alignment.End),
                    textAlign = TextAlign.End,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = styles.itemsCodeTextSize,
                    color = styles.itemsCodeTextColor
                )
            }
        }
    }
}


@Composable
fun AccountsCryptoItem(balance: AccountAssetPriceModel,
    index: Int, selectedIndex: Int,
    accountsViewModel: AccountsViewModel?,
    currentRememberState: MutableState<AccountsView.AccountsViewState>,
    customStyles: AccountsViewStyles = AccountsViewStyles()
) {

    // -- Vars
    val cryptoCode = balance.accountAssetCode
    val imageID = getImage(LocalContext.current, "ic_${cryptoCode.lowercase()}")
    val cryptoName = balance.assetName
    val assetNameCode = buildAnnotatedString {
        append(cryptoName)
        withStyle(style = SpanStyle(
            color = colorResource(id = R.color.list_prices_asset_component_code_color),
            fontFamily = robotoFont,
            fontWeight = FontWeight.Normal
        )
        ) {
            append(" $cryptoCode")
        }
    }

    // -- Content
    Surface(color = Color.Transparent) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 0.dp)
                .height(66.dp)
                .clickable {

                    currentRememberState.value = AccountsView.AccountsViewState.LOADING
                    accountsViewModel?.getTradesList(balance)
                },
        ) {

            Image(
                painter = painterResource(id = imageID),
                contentDescription = "{$cryptoName}",
                modifier = Modifier
                    .padding(horizontal = 0.dp)
                    .padding(0.dp)
                    .size(22.dp),
                contentScale = ContentScale.Fit
            )
            Column(
               modifier = Modifier
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = assetNameCode,
                    modifier = Modifier,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    lineHeight = 20.sp,
                    color = customStyles.itemsTextColor
                )
                Text(
                    text = balance.buyPriceFormatted,
                    modifier = Modifier,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.5.sp,
                    lineHeight = 20.sp,
                    color = customStyles.itemsCodeTextColor
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = balance.accountBalanceFormattedString,
                    modifier = Modifier.align(Alignment.End),
                    textAlign = TextAlign.End,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    color = customStyles.itemsTextColor
                )
                Text(
                    text = balance.accountBalanceInFiatFormatted,
                    modifier = Modifier.align(Alignment.End),
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    color = customStyles.itemsCodeTextColor
                )
            }

        }
    }
}

/**
 * Compose Previews
 * **/
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun AccountsViewLoadingPreview() {
    AccountsViewLoading()
}