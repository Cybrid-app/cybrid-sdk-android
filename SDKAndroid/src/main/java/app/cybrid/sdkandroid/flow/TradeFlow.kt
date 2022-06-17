package app.cybrid.sdkandroid.flow

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.constraintlayout.widget.ConstraintLayout
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.ListPricesView
import app.cybrid.sdkandroid.components.ListPricesViewType
import app.cybrid.sdkandroid.components.getImage
import app.cybrid.sdkandroid.components.listprices.view.ListPricesViewModel
import app.cybrid.sdkandroid.core.AssetPipe
import app.cybrid.sdkandroid.core.BigDecimal
import app.cybrid.sdkandroid.core.BigDecimalPipe
import app.cybrid.sdkandroid.ui.Theme.robotoFont

class TradeFlow @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(context, attrs, defStyle) {

    var listPricesView: ListPricesView? = null

    private var listPricesViewModel: ListPricesViewModel? = null
    private var composeContent: ComposeView? = null

    init {

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.trade_flow, this, true)

        // -- List
        this.listPricesView = findViewById(R.id.list)
    }

    fun setListPricesViewModel(viewModel: ListPricesViewModel) {

        this.listPricesViewModel = viewModel
        this.listPricesView.let {
            it?.type = ListPricesViewType.Assets
            it?.setViewModel(viewModel)
            it?.onClick = { asset, pairAsset ->
                initComposePreQuoteComponent(asset, pairAsset)
            }
        }
    }

    // -- PreQuote
    private fun initComposePreQuoteComponent(
        asset: AssetBankModel, pairAsset: AssetBankModel
    ) {
        this.listPricesView?.visibility = GONE
        this.composeContent = this.findViewById(R.id.composeContent)
        this.composeContent?.visibility = VISIBLE
        this.setComposePreQuoteContent(asset, pairAsset)
    }

    private fun setComposePreQuoteContent(
        asset: AssetBankModel, pairAsset: AssetBankModel
    ) {

        this.composeContent.let {
            it?.setContent {
                Column {

                    // -- Crypto List
                    val cryptoList = listPricesViewModel?.getCryptoListAsset() ?: listOf()

                    // -- Tabs
                    val tabs = stringArrayResource(id = R.array.trade_flow_tabs)

                    // -- Currency input
                    val currencyState = remember { mutableStateOf(asset) }

                    // -- Currency Input --> DropDown
                    val expandedCurrencyInput = remember { mutableStateOf(false) }
                    val currencyInputWidth = remember { mutableStateOf(Size.Zero) }
                    val selectedTabIndex = remember { mutableStateOf(0) }

                    // -- Value Input
                    val typeOfValueState = remember { mutableStateOf(AssetBankModel.Type.fiat) }
                    val valueState = remember { mutableStateOf("") }
                    val valueAsset = remember { mutableStateOf(
                        if (typeOfValueState.value == AssetBankModel.Type.fiat)
                            pairAsset else currencyState.value
                    )}

                    CryptoHeaderTabs(
                        selectedTabIndex = selectedTabIndex,
                        tabs = tabs
                    )

                    CryptoCurrencyInput(
                        currencyState = currencyState,
                        expandedCurrencyInput = expandedCurrencyInput,
                        currencyInputWidth = currencyInputWidth
                    )

                    CryptoCurrencyDropDown(
                        currencyState = currencyState,
                        expandedCurrencyInput = expandedCurrencyInput,
                        currencyInputWidth = currencyInputWidth,
                        cryptoList = cryptoList
                    )

                    CryptoValueInput(
                        valueState = valueState,
                        valueAsset = valueAsset
                    )
                }
            }
        }
    }

    @Composable
    private fun CryptoHeaderTabs(
        selectedTabIndex: MutableState<Int>,
        tabs: Array<String>) {

        TabRow(
            selectedTabIndex = selectedTabIndex.value,
            backgroundColor = Color.Transparent,
            indicator = { tabsIndicators ->
                Box(
                    Modifier
                        .tabIndicatorOffset(tabsIndicators[selectedTabIndex.value])
                        .height(2.dp)
                        .border(3.5.dp, colorResource(id = R.color.primary_color))
                )
            }
        ) {
            tabs.forEachIndexed { index, tabItem ->
                Tab(
                    selected = selectedTabIndex.value == index,
                    onClick = {
                        selectedTabIndex.value = index
                    },
                    selectedContentColor = colorResource(id = R.color.primary_color),
                    unselectedContentColor = colorResource(id = R.color.list_prices_asset_component_code_color),
                    text = {
                        Text(
                            text = tabItem,
                            fontFamily = robotoFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                )
            }
        }
    }

    @Composable
    private fun CryptoCurrencyInput(
        currencyState: MutableState<AssetBankModel>,
        expandedCurrencyInput: MutableState<Boolean>,
        currencyInputWidth: MutableState<Size>) {

        Text(
            modifier = Modifier
                .padding(top = 27.dp)
                .padding(horizontal = 1.dp),
            text = "Crypto Currency",
            fontFamily = robotoFont,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp
        )
        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .padding(horizontal = 2.dp)
                .height(56.dp)
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    currencyInputWidth.value = coordinates.size.toSize()
                }
                .background(Color.White)
                .border(
                    border = BorderStroke(
                        1.15.dp,
                        colorResource(id = R.color.custom_input_color_border)
                    ),
                    shape = RoundedCornerShape(4.dp)
                )
                .clickable { expandedCurrencyInput.value = !expandedCurrencyInput.value }
        ) {
            Image(
                painter = painterResource(id = getImageID(currencyState.value.code.lowercase())),
                contentDescription = currencyState.value.name,
                modifier = Modifier
                    .padding(top = 12.dp, start = 16.dp)
                    .size(32.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = currencyState.value.name,
                modifier = Modifier
                    .padding(start = 10.dp, top = 18.dp),
                fontFamily = robotoFont,
                fontWeight = FontWeight.Normal,
                fontSize = 16.5.sp,
                color = Color.Black
            )
            Text(
                text = currencyState.value.code,
                modifier = Modifier
                    .padding(start = 5.5.dp, top = 18.dp),
                fontFamily = robotoFont,
                fontWeight = FontWeight.Normal,
                fontSize = 16.5.sp,
                color = colorResource(id = R.color.list_prices_asset_component_code_color)
            )
            Spacer(modifier = Modifier.weight(0.5f))
            Icon(
                Icons.Filled.ArrowDropDown,
                contentDescription = "",
                modifier = Modifier
                    .width(45.dp)
                    .height(45.dp)
                    .size(30.dp)
                    .padding(top = 19.dp, end = 3.dp)
                    .clickable { /*expanded.value = !expanded.value*/ }
            )
        }
    }

    @Composable
    private fun CryptoCurrencyDropDown(
        currencyState: MutableState<AssetBankModel>,
        expandedCurrencyInput: MutableState<Boolean>,
        currencyInputWidth: MutableState<Size>,
        cryptoList: List<AssetBankModel>
    ) {

        DropdownMenu(
            expanded = expandedCurrencyInput.value,
            onDismissRequest = { expandedCurrencyInput.value = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { currencyInputWidth.value.width.toDp() })
                .padding(horizontal = 2.dp)
        ) {
            cryptoList.forEach { crypto ->

                val imageID = getImageID(crypto.code.lowercase())
                DropdownMenuItem(
                    onClick = {

                        currencyState.value = crypto
                        expandedCurrencyInput.value = false
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(vertical = 0.dp)
                    ) {

                        Image(
                            painter = painterResource(id = imageID),
                            contentDescription = "{$imageID}",
                            modifier = Modifier
                                .padding(horizontal = 0.dp)
                                .padding(0.dp)
                                .size(25.dp),
                            contentScale = ContentScale.Fit
                        )
                        Text(
                            text = crypto.name,
                            modifier = Modifier.padding(start = 16.dp),
                            fontFamily = robotoFont,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Text(
                            text = crypto.code,
                            modifier = Modifier.padding(start = 5.5.dp),
                            fontFamily = robotoFont,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = colorResource(id = R.color.list_prices_asset_component_code_color)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun CryptoValueInput(
        valueState: MutableState<String>,
        valueAsset: MutableState<AssetBankModel>
    ) {

        // -- Focus Manger
        val focusManager = LocalFocusManager.current

        // -- Labels and placeholders
        val amountHint = buildAnnotatedString {
            append(stringResource(id = R.string.trade_flow_text_field_amount_placeholder))
            withStyle(style = SpanStyle(
                color = colorResource(id = R.color.list_prices_asset_component_code_color))) {
                append(" ${valueAsset.value.code}")
            }
        }
        val amountLabel = buildAnnotatedString {
            append(stringResource(id = R.string.trade_flow_text_field_amount_placeholder))
            append(" ${valueAsset.value.code}")
        }

        // -- Content
        Text(
            modifier = Modifier
                .padding(top = 29.dp)
                .padding(horizontal = 1.dp),
            text = amountLabel,
            fontFamily = robotoFont,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp
        )
        OutlinedTextField(
            value = valueState.value,
            onValueChange = { value ->
                valueState.value = value
            },
            placeholder = {
                Text(
                    text = amountHint,
                    color = colorResource(id = R.color.black)
                )
            },
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Next) }
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .padding(top = 8.dp)
                .padding(horizontal = 2.dp)
                .height(56.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(4.dp),
            textStyle = TextStyle(
                fontFamily = robotoFont,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            ),
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.Black,
                cursorColor = colorResource(id = R.color.primary_color),
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = colorResource(id = R.color.list_prices_asset_component_code_color),
                unfocusedIndicatorColor = colorResource(id = R.color.list_prices_asset_component_code_color),
                disabledIndicatorColor = colorResource(id = R.color.list_prices_asset_component_code_color)
            )
        )
    }

    private fun getImageID(name: String) : Int {
        return getImage(context, "ic_${name}")
    }
}