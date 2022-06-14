package app.cybrid.sdkandroid.flow

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.constraintlayout.widget.ConstraintLayout
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.ListPricesView
import app.cybrid.sdkandroid.components.ListPricesViewType
import app.cybrid.sdkandroid.components.listprices.view.ListPricesViewModel
import app.cybrid.sdkandroid.ui.Theme.robotoFont

class TradeFlow @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(context, attrs, defStyle) {

    var listPricesView:ListPricesView? = null
    var composeContent:ComposeView? = null

    init {

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.trade_flow, this, true)

        // -- List
        this.listPricesView = findViewById(R.id.list)

        //this.composeContent = this.findViewById(R.id.composeContent)
        //this.setComposeContent()
    }

    fun setListPricesVideModel(viewModel:ListPricesViewModel) {

        this.listPricesView.let {
            it?.type = ListPricesViewType.Assets
            it?.setViewModel(viewModel)
            it?.onClick = {
                Toast.makeText(context, "LOLLL", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // -- PreQuote
    private fun initComposePreQuoteComponent() {}

    private fun setComposeContent() {

        this.composeContent.let {
            it?.setContent {
                Column {

                    val suggestions = listOf("Item1","Item2","Item3")
                    val state = remember { mutableStateOf(TextFieldValue("")) }
                    val amountHint = buildAnnotatedString {
                        append(stringResource(id = R.string.trade_flow_text_field_amount_placeholder))
                        withStyle(style = SpanStyle(color = colorResource(id = R.color.list_prices_asset_component_code_color))) {
                            append(" USD")
                        }
                    }
                    val amountLabel = buildAnnotatedString {
                        append(stringResource(id = R.string.trade_flow_text_field_amount_placeholder))
                        append(" USD")
                    }

                    // -- DropDown
                    val currencyState = remember { mutableStateOf(TextFieldValue("")) }
                    var expanded = remember { mutableStateOf(false) }
                    var textFieldSize = remember { mutableStateOf(Size.Zero) }
                    val icon = Icons.Filled.ArrowDropDown

                    Text(
                        text = "Buy Bitcoin",
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
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .padding(top = 30.dp)
                            .padding(horizontal = 2.dp)
                            .height(58.dp)
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
                        value = currencyState.value,
                        onValueChange = { value ->
                            currencyState.value = value
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
                        suggestions.forEach { label ->
                            DropdownMenuItem(onClick = { }) {
                                Text(text = label)
                            }
                        }
                    }

                    // --
                    Text(
                        text = "1 BTC = $36,588.23 USD",
                        modifier = Modifier
                            .padding(top = 31.dp)
                    )
                }
            }
        }
    }
}