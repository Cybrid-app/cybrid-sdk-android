package app.cybrid.sdkandroid.flow

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent.ACTION_DOWN
import android.view.LayoutInflater
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
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

@ExperimentalComposeUiApi
class TradeFlow @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(context, attrs, defStyle) {

    var listPricesView:ListPricesView? = null
    private var listPricesViewModel:ListPricesViewModel? = null
    private var composeContent:ComposeView? = null

    init {

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.trade_flow, this, true)

        // -- List
        this.listPricesView = findViewById(R.id.list)
    }

    fun setListPricesVideModel(viewModel:ListPricesViewModel) {

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
        asset:AssetBankModel, pairAsset:AssetBankModel
    ) {
        this.listPricesView?.visibility = GONE
        this.composeContent = this.findViewById(R.id.composeContent)
        this.composeContent?.visibility = VISIBLE
        this.setComposeContent(asset, pairAsset)
    }

    private fun setComposeContent(
        asset:AssetBankModel, pairAsset:AssetBankModel
    ) {

        this.composeContent.let {
            it?.setContent {
                Column {

                    // -- Focus
                    val focusManager = LocalFocusManager.current

                    val cryptoList = listPricesViewModel?.getCryptoListAsset() ?: listOf()
                    val state = remember { mutableStateOf("") }
                    val amountHint = buildAnnotatedString {
                        append(stringResource(id = R.string.trade_flow_text_field_amount_placeholder))
                        withStyle(style = SpanStyle(color = colorResource(id = R.color.list_prices_asset_component_code_color))) {
                            append(" ${pairAsset.code}")
                        }
                    }
                    val amountLabel = buildAnnotatedString {
                        append(stringResource(id = R.string.trade_flow_text_field_amount_placeholder))
                        append(" ${pairAsset.code}")
                    }

                    // -- DropDown
                    val currencyState = remember { mutableStateOf(asset) }
                    val expanded = remember { mutableStateOf(false) }
                    val textFieldSize = remember { mutableStateOf(Size.Zero) }
                    val icon = Icons.Filled.ArrowDropDown

                    Text(
                        text = "Buy ${asset.name}",
                        textAlign = TextAlign.Left,
                        fontFamily = robotoFont,
                        fontWeight = FontWeight.Medium,
                        fontSize = 23.sp
                    )
                    OutlinedTextField(
                        value = state.value,
                        onValueChange = { value ->
                            state.value = value
                        },
                        label = {
                            Text(
                                text = amountLabel
                            )
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
                            .padding(top = 30.dp)
                            .padding(horizontal = 2.dp)
                            .height(58.dp)
                            .fillMaxWidth()
                            .onPreviewKeyEvent { it ->
                                if (it.key == Key.Tab && it.nativeKeyEvent.action == ACTION_DOWN) {
                                    focusManager.moveFocus(FocusDirection.Down)
                                    true
                                } else {
                                    false
                                }
                            },
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
                    Image(
                        painter = painterResource(id = R.drawable.ic_change),
                        contentDescription = "Change icon to crypto-fiat",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(top = 18.dp)
                            .padding(horizontal = 16.dp)
                            .width(16.dp)
                            .height(21.dp)
                            .align(Alignment.End)
                            .clickable {
                                Toast
                                    .makeText(context, "Swap clicked", Toast.LENGTH_LONG)
                                    .show()
                            }
                    )

                    // -- Spinner
                    OutlinedTextField(
                        value = currencyState.value.name,
                        onValueChange = { value ->
                            //currencyState.value = value
                        },
                        interactionSource = remember { MutableInteractionSource() }
                            .also { interactionSource ->
                                LaunchedEffect(interactionSource) {
                                    interactionSource.interactions.collect {
                                        if (it is PressInteraction.Release) {
                                            expanded.value = !expanded.value
                                        }
                                    }
                                }
                            },
                        label = {
                            Text(
                                text = "Currency"
                            )
                        },
                        modifier = Modifier
                            .padding(top = 14.dp)
                            .padding(horizontal = 2.dp)
                            .height(58.dp)
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                textFieldSize.value = coordinates.size.toSize()
                            },
                        trailingIcon = {

                            Icon(
                                icon,
                                contentDescription = "",
                                modifier = Modifier
                                    .size(25.dp)
                                    .clickable { expanded.value = !expanded.value }
                            )
                        },
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
                            trailingIconColor = colorResource(id = R.color.list_prices_asset_component_code_color),
                            focusedIndicatorColor = colorResource(id = R.color.list_prices_asset_component_code_color),
                            unfocusedIndicatorColor = colorResource(id = R.color.list_prices_asset_component_code_color),
                            disabledIndicatorColor = colorResource(id = R.color.list_prices_asset_component_code_color)
                        )
                    )
                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false },
                        modifier = Modifier
                            .width(with(LocalDensity.current) { textFieldSize.value.width.toDp() })
                            .padding(horizontal = 2.dp)
                    ) {
                        cryptoList.forEach { crypto ->

                            val imageName = crypto.code.lowercase()
                            val imageID = getImage(context, "ic_${imageName}")
                            DropdownMenuItem(
                                onClick = {

                                    currencyState.value = crypto
                                    expanded.value = false
                                }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(vertical = 0.dp)
                                ) {

                                    Image(
                                        painter = painterResource(id = imageID),
                                        contentDescription = "{$imageName}",
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
                                        text = asset.code,
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

                    // --
                    if (!expanded.value && state.value != "") {

                        // -- Get latest price
                        listPricesViewModel?.getListPrices()
                        val symbol = "${currencyState.value.code}-${pairAsset.code}"

                        val stateInt = state.value.toInt()
                        val buyPrice = listPricesViewModel?.getBuyPrice(symbol)
                        val buyPriceDecimal = BigDecimal(buyPrice?.buyPrice ?: 0)

                        val baseValue = AssetPipe.transform(
                            stateInt,
                            pairAsset,
                            "base"
                        )
                        val amount = baseValue.divL(buyPriceDecimal).toPlainString()
                        val amountStyled = buildAnnotatedString {
                            append(amount)
                            withStyle(style = SpanStyle(color = colorResource(id = R.color.list_prices_asset_component_code_color))) {
                                append(" ${currencyState.value.code}")
                            }
                        }

                        Text(
                            text = amountStyled,
                            modifier = Modifier
                                .padding(top = 31.dp)
                                .padding(horizontal = 2.dp)
                        )
                    }
                }
            }
        }
    }
}