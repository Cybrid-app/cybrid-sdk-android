package app.cybrid.sdkandroid.components

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.cybrid_api_bank.client.models.SymbolPriceBankModel
import app.cybrid.sdkandroid.R
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

    var currentState = mutableStateOf(AccountsView.AccountsViewState.LOADING)

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

@Composable
fun AccountsView(
    currentState: MutableState<AccountsView.AccountsViewState>,
    listPricesViewModel: ListPricesViewModel?,
    accountsViewModel: AccountsViewModel?
) {

    // -- Vars
    val currentRememberState: MutableState<AccountsView.AccountsViewState> = remember { currentState }
    if (accountsViewModel?.accounts?.isNotEmpty()!!
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

@Composable
fun AccountsCryptoItem(crypto: SymbolPriceBankModel,
                    vm: ListPricesViewModel,
                    index: Int, selectedIndex: Int,
                    context: Context? = null,
                    customStyles: ListPricesViewCustomStyles,
                    onClick: (asset: AssetBankModel,
                              pairAsset: AssetBankModel
                    ) -> Unit) {

    val backgroundColor = if (index == selectedIndex) MaterialTheme.colors.primary else Color.Transparent
    if (crypto.symbol != null) {

        val loadingErrorVal = "-1"
        val asset = vm.findAsset(vm.getSymbol(crypto.symbol!!))
        val pairAsset = vm.findAsset(vm.getPair(crypto.symbol!!))
        val imageName = vm.getSymbol(crypto.symbol!!).lowercase()
        val imageID = getImage(context!!, "ic_${imageName}")

        val name = asset?.name ?: ""
        val valueString = crypto.buyPrice?.let {
            if (pairAsset != null) {
                BigDecimalPipe.transform(it.toBigDecimal(), pairAsset)
            } else { loadingErrorVal }
        } ?: loadingErrorVal
        val value = buildAnnotatedString {
            append(valueString)
            withStyle(style = SpanStyle(color = customStyles.itemsCodeTextColor)) {
                append(" (${pairAsset?.code ?: ""})")
            }
        }
        if (valueString != loadingErrorVal) {
            Surface(color = backgroundColor) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(vertical = 0.dp)
                        .height(56.dp)
                        .clickable { onClick(asset!!, pairAsset!!) },
                ) {

                    Image(
                        painter = painterResource(id = imageID),
                        contentDescription = "{$name}",
                        modifier = Modifier
                            .padding(horizontal = 0.dp)
                            .padding(0.dp)
                            .size(25.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        text = name,
                        modifier = Modifier.padding(start = 16.dp),
                        fontFamily = robotoFont,
                        fontWeight = FontWeight.Normal,
                        fontSize = customStyles.itemsTextSize,
                        color = customStyles.itemsTextColor
                    )
                    Text(
                        text = asset?.code ?: "",
                        modifier = Modifier.padding(start = 5.5.dp),
                        fontFamily = robotoFont,
                        fontWeight = FontWeight.Normal,
                        fontSize = customStyles.itemsCodeTextSize,
                        color = customStyles.itemsCodeTextColor
                    )
                    Text(
                        text = value,
                        modifier = Modifier
                            .padding(end = 0.dp)
                            .weight(1f),
                        textAlign = TextAlign.End,
                        fontFamily = robotoFont,
                        fontWeight = FontWeight.Normal,
                        fontSize = customStyles.itemsTextPriceSize,
                        color = customStyles.itemsTextColor
                    )
                }
            }
        }
    }
}

@Composable
fun AccountsCryptoHeaderItem(customStyles: ListPricesViewCustomStyles) {

    val priceColor = if (customStyles.headerTextColor != Color(R.color.list_prices_asset_component_header_color)) {
        customStyles.headerTextColor
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
                fontSize = customStyles.headerTextSize,
                color = customStyles.headerTextColor
            )
            Text(
                text = stringResource(id = R.string.list_prices_asset_component_header_price),
                modifier = Modifier
                    .padding(end = 0.dp)
                    .weight(1f),
                textAlign = TextAlign.End,
                fontFamily = robotoFont,
                fontWeight = FontWeight.Bold,
                fontSize = customStyles.headerTextSize,
                color = priceColor
            )
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