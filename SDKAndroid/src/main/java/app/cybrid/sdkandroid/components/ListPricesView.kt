package app.cybrid.sdkandroid.components

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.cybrid_api_bank.client.models.SymbolPriceBankModel
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.listprices.view.ListPricesViewModel
import app.cybrid.sdkandroid.core.BigDecimalPipe
import app.cybrid.sdkandroid.ui.Theme.robotoFont
import app.cybrid.sdkandroid.util.Logger
import app.cybrid.sdkandroid.util.LoggerEvents

open class ListPricesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : AbstractComposeView(context, attrs, defStyle) {

    /**
     * Public values for time interval refresh and list type
     * **/
    var updateInterval = 5000L
    var type:ListPricesViewType = ListPricesViewType.Normal
    var onClick:(AssetBankModel, AssetBankModel) -> Unit = { asset, pairAsset -> }

    private var _viewModel: ListPricesViewModel? = null
    private var _handler:Handler? = null
    private var _runnable:Runnable? = null

    // -- Custom Styles and values
    var customStyles = ListPricesViewCustomStyles()

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
        _viewModel?.getListPrices()

        _handler = Handler(Looper.getMainLooper())
        _runnable = Runnable { this.refreshPrices() }
        _handler?.postDelayed(_runnable!!, updateInterval)
    }

    private fun refreshPrices() {

        Logger.log(LoggerEvents.DATA_REFRESHED, "ListPricesView Component data")
        _viewModel?.getListPrices()
        _viewModel.let { it?.getListPrices() }
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
                type = type,
                viewModel = _viewModel,
                context = context,
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
 * ListPricesView Type Enum
 * **/
enum class ListPricesViewType { Normal, Assets }

/**
 * ListPricesView Custom Styles
 * **/
data class ListPricesViewCustomStyles(

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
 * ListPricesView Composable Functions
 * **/
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CryptoList(
    cryptoList: List<SymbolPriceBankModel>,
    type: ListPricesViewType,
    viewModel: ListPricesViewModel? = null,
    context: Context? = null,
    customStyles: ListPricesViewCustomStyles,
    onClick: (AssetBankModel, AssetBankModel) -> Unit) {

    var selectedIndex by remember { mutableStateOf(-1) }
    val textState = remember { mutableStateOf(TextFieldValue("")) }
    val topPadding = if (!customStyles.searchBar) { 0.dp } else { 20.dp }

    Column {

        if (customStyles.searchBar) { SearchView(state = textState) }
        LazyColumn(modifier =
        Modifier
            .padding(top = topPadding)
            .padding(horizontal = 3.5.dp)) {

            val filtered = if (textState.value.text.isNotEmpty()) {
                ArrayList(cryptoList.filter {

                    val asset = viewModel?.findAsset(viewModel.getSymbol(it.symbol!!))
                    asset?.name?.lowercase()?.contains(textState.value.text.lowercase())
                        ?: it.symbol?.lowercase()!!.contains(textState.value.text.lowercase())
                })
            } else { cryptoList }

            stickyHeader {
                CryptoAssetHeaderItem(customStyles)
            }
            itemsIndexed(items =  filtered) { index, item ->

                if (type == ListPricesViewType.Assets) {
                    CryptoAssetItem(
                        crypto = item,
                        vm = viewModel!!,
                        index = index,
                        selectedIndex = selectedIndex,
                        context = context,
                        customStyles = customStyles,
                        onClick = onClick
                    )
                } else {
                    CryptoItem(
                        crypto = item,
                        index = index,
                        selectedIndex = selectedIndex
                    ) {
                        selectedIndex = it
                    }
                }
            }
        }
    }
}

@Composable
fun SearchView(state: MutableState<TextFieldValue>) {

    TextField(
        value = state.value,
        onValueChange = { value ->
            state.value = value
        },
        placeholder = { Text(stringResource(id = R.string.list_prices_asset_component_search)) },
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .height(50.dp)
            .fillMaxWidth()
            .shadow(5.dp),
        shape = RoundedCornerShape(4.dp),
        textStyle = TextStyle(
            fontFamily = robotoFont,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        ),
        trailingIcon = {

            val iconSize = 20.dp
            if (state.value != TextFieldValue("")) {
                IconButton(
                    onClick = {
                        state.value = TextFieldValue("")
                    }
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(0.dp)
                            .size(iconSize)
                    )
                }
            } else {

                Icon(
                    Icons.Default.Search,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(0.dp)
                        .size(iconSize)
                )
            }
        },
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.Black,
            cursorColor = colorResource(id = R.color.primary_color),
            leadingIconColor = Color.Black,
            trailingIconColor = Color.Black,
            backgroundColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun CryptoItem(crypto: SymbolPriceBankModel,
               index:Int, selectedIndex:Int,
               onClick: (Int) -> Unit) {

    val backgroundColor = if (index == selectedIndex) MaterialTheme.colors.primary else MaterialTheme.colors.background
    Card(
        modifier = Modifier
            .padding(15.dp, 10.dp)
            .fillMaxWidth()
            .clickable { onClick(index) },
        shape = RoundedCornerShape(8.dp), elevation = 4.dp
    ) {
        Surface(color = backgroundColor) {

            Row(
                Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxSize()
            ) {

                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(vertical = 10.dp, horizontal = 20.dp)
                        .fillMaxHeight()
                        .weight(0.5f)
                ) {
                    Text(
                        text = crypto.symbol!!,
                        style = MaterialTheme.typography.subtitle1,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    Text(
                        text = "Buy price: ${crypto.buyPrice}",
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    Text(
                        text = "Sell price: ${crypto.sellPrice}",
                        style = MaterialTheme.typography.body1
                    )
                }
            }
        }
    }
}

@Composable
fun CryptoAssetItem(crypto: SymbolPriceBankModel,
                    vm:ListPricesViewModel,
                    index:Int, selectedIndex:Int,
                    context: Context? = null,
                    customStyles: ListPricesViewCustomStyles,
                    onClick: (AssetBankModel, AssetBankModel) -> Unit) {

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
                BigDecimalPipe.transform(it, pairAsset)
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
fun CryptoAssetHeaderItem(customStyles: ListPricesViewCustomStyles) {

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
 * ListPricesView Composable Preview Functions
 * **/
@Preview(showBackground = true)
@Composable
fun SearchViewPreview() {

    val textState = remember { mutableStateOf(TextFieldValue("")) }
    SearchView(textState)
}

@Preview(showBackground = true)
@Composable
fun cryptoListPreview() {

    CryptoList(
        cryptoList = listOf(),
        type = ListPricesViewType.Assets,
        customStyles = ListPricesViewCustomStyles(),
        onClick = {it, it2 ->})
}

/**
 * ListPricesView Helper fucntions
 * **/
fun getImage(context:Context, name: String): Int {

    return context.resources.getIdentifier(name, "drawable", context.packageName)
}