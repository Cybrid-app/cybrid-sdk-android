package app.cybrid.sdkandroid.flow

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.*
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
import app.cybrid.cybrid_api_bank.client.models.PostQuoteBankModel
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.ListPricesView
import app.cybrid.sdkandroid.components.ListPricesViewType
import app.cybrid.sdkandroid.components.getImage
import app.cybrid.sdkandroid.components.listprices.view.ListPricesViewModel
import app.cybrid.sdkandroid.components.quote.view.QuoteConfirmationModal
import app.cybrid.sdkandroid.components.quote.view.QuoteViewModel
import app.cybrid.sdkandroid.core.AssetPipe
import app.cybrid.sdkandroid.core.BigDecimal
import app.cybrid.sdkandroid.core.BigDecimalPipe
import app.cybrid.sdkandroid.ui.Theme.robotoFont
import app.cybrid.sdkandroid.util.Logger
import app.cybrid.sdkandroid.util.LoggerEvents
import java.math.BigDecimal as JavaBigDecimal

class TradeFlow @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(context, attrs, defStyle) {

    var updateInterval = 5000L
    var listPricesView: ListPricesView? = null
    var quoteViewModel: QuoteViewModel? = null

    private var _handler: Handler? = null
    private var _runnable: Runnable? = null

    private var listPricesViewModel: ListPricesViewModel? = null
    private var composeContent: ComposeView? = null
    private var postQuoteBankModel: PostQuoteBankModel? = null

    init {

        LayoutInflater.from(context).inflate(R.layout.trade_flow, this, true)

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
                    val selectedTabIndex = remember { mutableStateOf(0) }

                    // -- Currency input
                    val currencyState = remember { mutableStateOf(asset) }

                    // -- Currency Input --> DropDown
                    val expandedCurrencyInput = remember { mutableStateOf(false) }
                    val currencyInputWidth = remember { mutableStateOf(Size.Zero) }

                    // -- Amount Input
                    val typeOfAmountState = remember { mutableStateOf(AssetBankModel.Type.fiat) }
                    val amountState = remember { mutableStateOf("") }
                    val amountAsset = remember { mutableStateOf(
                        if (typeOfAmountState.value == AssetBankModel.Type.fiat)
                            pairAsset else currencyState.value
                    )}

                    PreQuoteHeaderTabs(
                        selectedTabIndex = selectedTabIndex,
                        tabs = tabs
                    )

                    PreQuoteCurrencyInput(
                        currencyState = currencyState,
                        expandedCurrencyInput = expandedCurrencyInput,
                        currencyInputWidth = currencyInputWidth
                    )

                    PreQuoteCurrencyDropDown(
                        currencyState = currencyState,
                        expandedCurrencyInput = expandedCurrencyInput,
                        currencyInputWidth = currencyInputWidth,
                        cryptoList = cryptoList
                    )

                    if (!expandedCurrencyInput.value) {

                        PreQuoteAmountInput(
                            amountState = amountState,
                            amountAsset = amountAsset,
                            typeOfAmountState = typeOfAmountState
                        )

                        if (amountState.value != "") {

                            PreQuoteCurrencyValueResult(
                                currencyState = currencyState,
                                amountState = amountState,
                                amountAsset = amountAsset,
                                pairAsset = pairAsset,
                                typeOfAmountState = typeOfAmountState
                            )

                            PreQuoteActionButton(
                                currencyState = currencyState,
                                amountState = amountState,
                                pairAsset = pairAsset,
                                typeOfAmountState = typeOfAmountState,
                                selectedTabIndex = selectedTabIndex
                            )
                        }
                    }
                }
            }
        }
    }

    private fun refreshQuoteModel() {

        Logger.log(LoggerEvents.DATA_REFRESHED, "TradeFlow: Quote Component Data")
        if (this.quoteViewModel != null && this.postQuoteBankModel != null) {

            quoteViewModel?.getQuote(this.postQuoteBankModel!!)
            _handler.let {
                _runnable.let { _it ->
                    it?.postDelayed(_it!!, updateInterval)
                }
            }
        }
    }

    @Composable
    private fun PreQuoteHeaderTabs(
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
    private fun PreQuoteCurrencyInput(
        currencyState: MutableState<AssetBankModel>,
        expandedCurrencyInput: MutableState<Boolean>,
        currencyInputWidth: MutableState<Size>) {

        val icon = if (expandedCurrencyInput.value) {
            Icons.Filled.ArrowDropUp
        } else {
            Icons.Filled.ArrowDropDown
        }

        // -- Content
        Text(
            modifier = Modifier
                .padding(top = 27.dp)
                .padding(horizontal = 1.dp),
            text = stringResource(
                id = R.string.trade_flow_pre_quote_currency_input_label),
            fontFamily = robotoFont,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
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
                    .padding(start = 16.dp)
                    .size(32.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = currencyState.value.name,
                modifier = Modifier
                    .padding(start = 10.dp),
                fontFamily = robotoFont,
                fontWeight = FontWeight.Normal,
                fontSize = 16.5.sp,
                color = Color.Black
            )
            Text(
                text = currencyState.value.code,
                modifier = Modifier
                    .padding(start = 5.5.dp),
                fontFamily = robotoFont,
                fontWeight = FontWeight.Normal,
                fontSize = 16.5.sp,
                color = colorResource(id = R.color.list_prices_asset_component_code_color)
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                icon,
                contentDescription = "",
                modifier = Modifier
                    .size(30.dp)
                    .padding(end = 5.dp, top = 5.dp)
                    .clickable { expandedCurrencyInput.value = !expandedCurrencyInput.value }
            )
        }
    }

    @Composable
    private fun PreQuoteCurrencyDropDown(
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
    fun PreQuoteAmountInput(
        amountState: MutableState<String>,
        amountAsset: MutableState<AssetBankModel>,
        typeOfAmountState: MutableState<AssetBankModel.Type>
    ) {

        // -- Focus Manger
        val focusManager = LocalFocusManager.current

        // -- Content
        Text(
            modifier = Modifier
                .padding(top = 29.dp)
                .padding(horizontal = 1.dp),
            text = stringResource(id = R.string.trade_flow_text_field_amount_placeholder),
            fontFamily = robotoFont,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            color = colorResource(id = R.color.pre_quote_input_label)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 8.dp)
                .padding(horizontal = 2.dp)
                .height(56.dp)
                .fillMaxWidth()
                .background(Color.White)
                .border(
                    border = BorderStroke(
                        1.15.dp,
                        colorResource(id = R.color.custom_input_color_border)
                    ),
                    shape = RoundedCornerShape(4.dp)
                )
                .clickable {}
        ) {

            Text(
                modifier = Modifier
                    .padding(start = 18.dp),
                text = amountAsset.value.code,
                fontFamily = robotoFont,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = colorResource(id = R.color.list_prices_asset_component_code_color)
            )
            Box(
                modifier = Modifier
                    .padding(start = 15.dp)
                    .width(1.dp)
                    .height(22.dp)
                    .background(
                        color = colorResource(id = R.color.pre_quote_value_input_separator)
                    )
            )
            TextField(
                value = amountState.value.filter { it.isDigit() || it == '.' },
                onValueChange = { value ->
                    amountState.value = value.filter { it.isDigit() || it == '.' }
                },
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.trade_flow_text_field_amount_placeholder),
                        color = colorResource(id = R.color.black)
                    )
                },
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus(true) }
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier
                    .padding(start = 0.dp, end = 0.dp)
                    .weight(0.88f)
                    .testTag("PreQuoteAmountInputTextFieldTag"),
                    //.fillMaxWidth(),
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
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
            Icon(
                Icons.Filled.SwapVert,
                contentDescription = "",
                tint = colorResource(id = R.color.primary_color),
                modifier = Modifier

                    .size(24.dp)
                    .padding(end = 14.dp)
                    .weight(0.12f)
                    .clickable {
                        if (typeOfAmountState.value == AssetBankModel.Type.fiat) {
                            typeOfAmountState.value = AssetBankModel.Type.crypto
                        } else {
                            typeOfAmountState.value = AssetBankModel.Type.fiat
                        }
                    }
            )
        }
    }

    @Composable
    private fun PreQuoteCurrencyValueResult(
        currencyState: MutableState<AssetBankModel>,
        amountState: MutableState<String>,
        amountAsset: MutableState<AssetBankModel>,
        pairAsset: AssetBankModel,
        typeOfAmountState: MutableState<AssetBankModel.Type>
    ) {

        val symbol = "${currencyState.value.code}-${pairAsset.code}"
        var stateInt = amountState.value
        if (stateInt.isNotEmpty() && stateInt[0] == '.') {
            stateInt = "0$stateInt"
        }

        val buyPrice = listPricesViewModel?.getBuyPrice(symbol)
        val buyPriceDecimal = BigDecimal(buyPrice?.buyPrice ?: JavaBigDecimal(0))
        var amount = "0"
        var codeAssetToUse:AssetBankModel? = null

        when(typeOfAmountState.value) {

            AssetBankModel.Type.crypto -> {

                amountAsset.value = currencyState.value
                codeAssetToUse = pairAsset
                val value = BigDecimal(stateInt).times(buyPriceDecimal)
                amount =  BigDecimalPipe.transform(value, pairAsset)!!
            }

            AssetBankModel.Type.fiat -> {

                amountAsset.value = pairAsset
                codeAssetToUse = currencyState.value
                val baseValue = AssetPipe.transform(
                    stateInt,
                    pairAsset,
                    "base"
                )
                val value = baseValue.divL(buyPriceDecimal)
                amount = value.toPlainString()
            }

            else -> {}
        }

        val amountStyled = buildAnnotatedString {
            append(amount)
            withStyle(style = SpanStyle(
                color = colorResource(id = R.color.list_prices_asset_component_code_color))) {
                append(" ${codeAssetToUse?.code}")
            }
        }

        // -- Content
        Row(
            modifier = Modifier
                .padding(top = 11.dp)
                .padding(horizontal = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (typeOfAmountState.value == AssetBankModel.Type.crypto) {
                Image(
                    painter = painterResource(id = R.drawable.ic_usd),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(top = 0.dp, end = 8.dp)
                        .width(28.dp)
                        .height(16.34.dp)
                )
            }
            Text(
                text = amountStyled,
                fontFamily = robotoFont,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            )
        }
    }

    @Composable
    private fun PreQuoteActionButton(
        currencyState: MutableState<AssetBankModel>,
        amountState: MutableState<String>,
        pairAsset: AssetBankModel,
        typeOfAmountState: MutableState<AssetBankModel.Type>,
        selectedTabIndex: MutableState<Int>
    ) {

        // -- Side logic
        val side = remember { mutableStateOf(PostQuoteBankModel.Side.buy) }
        var textButton = ""
        when(selectedTabIndex.value) {

            0 ->  {
                side.value = PostQuoteBankModel.Side.buy
                textButton = stringResource(id = R.string.trade_flow_buy_action_button)
            }
            1 -> {
                PostQuoteBankModel.Side.sell
                textButton = stringResource(id = R.string.trade_flow_sell_action_button)
            }
        }

        // -- Show Dialog
        val showDialog = remember { mutableStateOf(false) }
        if (showDialog.value) {
            QuoteConfirmationModal(
                viewModel = this.quoteViewModel!!,
                asset = currencyState,
                pairAsset = pairAsset,
                showDialog = showDialog,
                selectedTabIndex = selectedTabIndex,
                updateInterval = updateInterval
            )
        }

        // -- Content
        Row(
            modifier = Modifier
                .padding(top = 30.dp, end = 2.dp)
        ) {

            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    getQuote(
                        showDialog = showDialog,
                        side = side,
                        currencyState = currencyState,
                        amountState = amountState,
                        pairAsset = pairAsset,
                        typeOfAmountState = typeOfAmountState
                    )
                },
                modifier = Modifier
                    .width(120.dp)
                    .height(44.dp),
                shape = RoundedCornerShape(4.dp),
                elevation = ButtonDefaults.elevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 4.dp,
                    disabledElevation = 0.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(id = R.color.primary_color),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = textButton,
                    color = Color.White,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                )
            }
        }
    }

    private fun getQuote(
        showDialog: MutableState<Boolean>,
        side: MutableState<PostQuoteBankModel.Side>,
        currencyState: MutableState<AssetBankModel>,
        amountState: MutableState<String>,
        pairAsset: AssetBankModel,
        typeOfAmountState: MutableState<AssetBankModel.Type>,
    ) {

        showDialog.value = true

        // -- Set runnable for Quote Model
        _handler = Handler(Looper.getMainLooper())
        _runnable = Runnable { this.refreshQuoteModel() }
        _handler?.postDelayed(_runnable!!, updateInterval)

        // -- PostQuoteBankModel
        this.postQuoteBankModel = quoteViewModel?.getQuoteObject(
            amount = BigDecimal(amountState.value),
            input = typeOfAmountState.value,
            side = side.value,
            asset = currencyState.value,
            pairAsset = pairAsset
        )
        quoteViewModel?.getQuote(this.postQuoteBankModel!!)
    }

    private fun getImageID(name: String) : Int {
        return getImage(context, "ic_${name}")
    }
}