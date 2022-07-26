package app.cybrid.sdkandroid.components

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.cybrid_api_bank.client.models.SymbolPriceBankModel
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.accounts.entity.AccountAssetPriceModel
import app.cybrid.sdkandroid.components.accounts.view.AccountsViewModel
import app.cybrid.sdkandroid.components.listprices.view.ListPricesViewModel
import app.cybrid.sdkandroid.core.BigDecimalPipe
import app.cybrid.sdkandroid.core.Constants
import app.cybrid.sdkandroid.core.toBigDecimal
import app.cybrid.sdkandroid.ui.Theme.robotoFont

class AccountsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : Component(context, attrs, defStyle) {

    enum class AccountsViewState { LOADING, CONTENT }

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

        this._listPricesViewModel?.getListPrices()
        this._accountsViewModel?.getAccounts()

        this.setupRunnable { this._listPricesViewModel?.getListPrices() }
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
    var itemsCodeTextColor: Color = Color(R.color.list_prices_asset_component_header_color)
)

/**
 * Composable Static Functions
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
        currentRememberState.value = AccountsView.AccountsViewState.CONTENT
    }

    // -- Content
    Surface(
        modifier = Modifier
            .testTag(Constants.AccountsViewTestTags.Surface.id)
    ) {

        when(currentRememberState.value) {

            AccountsView.AccountsViewState.LOADING -> {
                AccountsViewLoading()
            }

            AccountsView.AccountsViewState.CONTENT -> {
                AccountsViewList(
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
    accountsViewModel: AccountsViewModel?
) {

    // -- Mutable Vars
    var selectedIndex by remember { mutableStateOf(-1) }

    // -- Items
    accountsViewModel?.createAccountsFormatted(
        prices = listPricesViewModel?.prices!!,
        assets = listPricesViewModel.assets
    )

    // -- Get Total balance
    accountsViewModel?.getTotalBalance()

    Column(
        modifier = Modifier
            .testTag(Constants.AccountsViewTestTags.List.id)
    ) {
        AccountsBalance(
            accountsViewModel = accountsViewModel
        )
        LazyColumn(
            modifier = Modifier
        ) {
            /*stickyHeader {
                AccountsCryptoHeaderItem()
            }*/
            itemsIndexed(items = accountsViewModel?.accounts ?: listOf()) { index, item ->
                AccountsCryptoItem(
                    balance = item,
                    index = index,
                    selectedIndex = selectedIndex
                )
            }
        }
    }
}

@Composable
fun AccountsBalance(
    accountsViewModel: AccountsViewModel?
) {

    // -- Content
    if (accountsViewModel?.totalBalance != "") {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 20.dp)
        ) {

            Text(
                text = accountsViewModel?.totalBalance ?: "",
                modifier = Modifier,
                textAlign = TextAlign.Center,
                fontFamily = robotoFont,
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
fun AccountsCryptoHeaderItem(
    styles: AccountsViewStyles = AccountsViewStyles()
) {

    val priceColor = if (styles.headerTextColor != Color(R.color.list_prices_asset_component_header_color)) {
        styles.headerTextColor
    } else {
        Color.Black
    }

    Surface(color = Color.Transparent) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {

            Text(
                text = stringResource(id = R.string.list_prices_asset_component_header_currency),
                fontFamily = robotoFont,
                fontWeight = FontWeight.Bold,
                fontSize = styles.headerTextSize,
                color = styles.headerTextColor
            )
            Text(
                text = stringResource(id = R.string.list_prices_asset_component_header_price),
                modifier = Modifier
                    .padding(end = 0.dp)
                    .weight(1f),
                textAlign = TextAlign.End,
                fontFamily = robotoFont,
                fontWeight = FontWeight.Bold,
                fontSize = styles.headerTextSize,
                color = priceColor
            )
        }
    }
}


@Composable
fun AccountsCryptoItem(balance: AccountAssetPriceModel,
                       index: Int, selectedIndex: Int,
                       customStyles: AccountsViewStyles = AccountsViewStyles()
) {

    // -- Vars
    val cryptoCode = balance.accountAssetCode
    val imageID = getImage(LocalContext.current, "ic_${cryptoCode.lowercase()}")
    val cryptoName = balance.assetName

    // -- Content
    Surface(color = Color.Transparent) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 0.dp)
                .height(66.dp),
        ) {

            Image(
                painter = painterResource(id = imageID),
                contentDescription = "{$cryptoName}",
                modifier = Modifier
                    .padding(horizontal = 0.dp)
                    .padding(0.dp)
                    .size(25.dp),
                contentScale = ContentScale.Fit
            )
            Column(
               modifier = Modifier
                    //.weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = cryptoName,
                    modifier = Modifier,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = customStyles.itemsTextSize,
                    color = customStyles.itemsTextColor
                )
                Text(
                    text = balance.buyPriceFormatted,
                    modifier = Modifier,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = customStyles.itemsCodeTextSize,
                    color = customStyles.itemsCodeTextColor
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = balance.accountBalanceFormatted.toPlainString(),
                    modifier = Modifier.align(Alignment.End),
                    textAlign = TextAlign.End,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = customStyles.itemsTextPriceSize,
                    color = customStyles.itemsTextColor
                )
                Text(
                    text = balance.accountBalanceInFiatFormatted,
                    modifier = Modifier.align(Alignment.End),
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = customStyles.itemsCodeTextSize,
                    color = customStyles.itemsCodeTextColor
                )
            }

        }
    }
}

/**
 * Compose Previews
 * **/
@Preview(showBackground = true)
@Composable
fun AccountsViewLoadingPreview() {
    AccountsViewLoading()
}