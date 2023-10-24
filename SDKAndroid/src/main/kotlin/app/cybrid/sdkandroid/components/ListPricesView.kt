package app.cybrid.sdkandroid.components

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.cybrid_api_bank.client.models.SymbolPriceBankModel
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.listprices.view.ListPricesViewModel
import app.cybrid.sdkandroid.components.listprices.view.compose.CryptoList_HeaderItem
import app.cybrid.sdkandroid.components.listprices.view.compose.CryptoList_Item
import app.cybrid.sdkandroid.components.listprices.view.compose.CryptoList_SearchView
import app.cybrid.sdkandroid.util.Logger
import app.cybrid.sdkandroid.util.LoggerEvents
import kotlinx.coroutines.launch

open class ListPricesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : AbstractComposeView(context, attrs, defStyle) {

    var updateInterval = 5000L
    var customStyles = ListPricesViewCustomStyles()
    var onClick: (AssetBankModel, AssetBankModel) -> Unit = { _, _ -> }

    private var _viewModel: ListPricesViewModel? = null
    private var _handler: Handler? = null
    private var _runnable: Runnable? = null

    init {

        Logger.log(LoggerEvents.COMPONENT_INIT, "ListPricesView Component")
        val a = context.obtainStyledAttributes(attrs,
            R.styleable.ListPricesView, 0, 0)

        val headerTextSizeDimension = a.getFloat(R.styleable.ListPricesView_headerTextSize,16.5F)
        val itemsTextSizeDimension = a.getFloat(R.styleable.ListPricesView_itemsTextSize,16F)
        val itemsCodeTextSizeDimension = a.getFloat(R.styleable.ListPricesView_itemsCodeTextSize,14F)
        val itemsTextPriceSizeDimension = a.getFloat(R.styleable.ListPricesView_itemsTextPriceSize,15F)

        customStyles.searchBar = a.getBoolean(R.styleable.ListPricesView_searchBar, true)
        customStyles.headerTextSize = headerTextSizeDimension.sp
        customStyles.headerTextColor = Color(a.getColor(R.styleable.ListPricesView_headerTextColor,
            ContextCompat.getColor(context, R.color.list_prices_asset_component_header_color)))
        customStyles.itemsTextSize = itemsTextSizeDimension.sp
        customStyles.itemsTextColor = Color(a.getColor(R.styleable.ListPricesView_itemsTextColor,
            ContextCompat.getColor(context, R.color.black)))
        customStyles.itemsTextPriceSize = itemsTextPriceSizeDimension.sp
        customStyles.itemsCodeTextSize = itemsCodeTextSizeDimension.sp
        customStyles.itemsCodeTextColor =  Color(a.getColor(R.styleable.ListPricesView_itemsCodeTextColor,
            ContextCompat.getColor(context, R.color.list_prices_asset_component_code_color)))

        a.recycle()
    }

    fun setViewModel(viewModel: ListPricesViewModel) {

        _viewModel = viewModel
        _viewModel?.viewModelScope?.launch {
            _viewModel?.getPricesList()
        }

        _handler = Handler(Looper.getMainLooper())
        _runnable = Runnable { this.refreshPrices() }
        _handler?.postDelayed(_runnable!!, updateInterval)
    }

    private fun refreshPrices() {

        Logger.log(LoggerEvents.DATA_REFRESHED, "ListPricesView Component data")
        _viewModel.let {
            it?.viewModelScope?.launch {
                _viewModel?.getPricesList()
            }
        }
        _handler.let {
            _runnable.let { _it ->
                it?.postDelayed(_it!!, updateInterval)
            }
        }
    }

    @Composable
    override fun Content() {

        _viewModel?.let {
            CryptoList(
                cryptoList = it.prices,
                viewModel = _viewModel,
                customStyles = customStyles,
                onClick = this.onClick
            ).apply {
                setViewCompositionStrategy(
                    ViewCompositionStrategy.DisposeOnLifecycleDestroyed(LocalLifecycleOwner.current)
                )
            } }
    }
}

/**
 * ListPricesView Custom Styles
 * **/
data class ListPricesViewCustomStyles(

    var searchBar: Boolean = true,
    var headerTextSize: TextUnit = 16.5.sp,
    var headerTextColor: Color = Color(R.color.list_prices_asset_component_header_color),
    var itemsTextSize: TextUnit = 17.sp,
    var itemsTextColor: Color = Color.Black,
    var itemsTextPriceSize: TextUnit = 15.sp,
    var itemsCodeTextSize: TextUnit = 14.sp,
    var itemsCodeTextColor: Color = Color(R.color.list_prices_asset_component_header_color)
)

/**
 * ListPricesView Composable Functions
 * **/
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CryptoList(
    cryptoList: List<SymbolPriceBankModel>,
    viewModel: ListPricesViewModel? = null,
    customStyles: ListPricesViewCustomStyles,
    onClick: (asset: AssetBankModel, pairAsset: AssetBankModel) -> Unit) {

    // -- Vars
    val selectedIndex by remember { mutableStateOf(-1) }
    val textState = remember { mutableStateOf(TextFieldValue("")) }
    val topPadding = if (!customStyles.searchBar) { 0.dp } else { 20.dp }

    val filtered = if (textState.value.text.isNotEmpty()) {
        ArrayList(cryptoList.filter {

            val asset = viewModel?.findAsset(viewModel.getSymbol(it.symbol!!))
            asset?.name?.lowercase()?.contains(textState.value.text.lowercase())
                ?: it.symbol?.lowercase()!!.contains(textState.value.text.lowercase())
        })
    } else { cryptoList }

    // -- Content
    Column {

        if (customStyles.searchBar) {
            CryptoList_SearchView(state = textState)
        }
        LazyColumn(modifier = Modifier
            .testTag("ListPricesView")
            .padding(top = topPadding)
            .padding(horizontal = 3.5.dp)) {

            stickyHeader {
                CryptoList_HeaderItem(customStyles)
            }
            itemsIndexed(items =  filtered) { index, item ->

                CryptoList_Item(
                    crypto = item,
                    vm = viewModel!!,
                    index = index,
                    selectedIndex = selectedIndex,
                    customStyles = customStyles,
                    onClick = onClick
                )
            }
        }
    }
}