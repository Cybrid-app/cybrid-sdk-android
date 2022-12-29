package app.cybrid.sdkandroid.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.constraintlayout.compose.ConstraintLayout
import app.cybrid.cybrid_api_bank.client.models.ExternalBankAccountBankModel
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.bankTransfer.compose.BankTransferView_Loading
import app.cybrid.sdkandroid.components.bankTransfer.modal.BankTransferModal
import app.cybrid.sdkandroid.components.bankTransfer.view.BankTransferViewModel
import app.cybrid.sdkandroid.core.Constants
import app.cybrid.sdkandroid.ui.Theme.robotoFont
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BankTransferView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0):
Component(context, attrs, defStyle) {

    enum class ViewState { LOADING, IN_LIST }

    private var currentState = mutableStateOf(ViewState.LOADING)
    var bankTransferViewModel: BankTransferViewModel? = null

    init {

        LayoutInflater.from(context).inflate(R.layout.bank_transfer_component, this, true)
        this.composeView = findViewById(R.id.composeContent)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun setViewModel(bankTransferViewModel: BankTransferViewModel) {

        this.bankTransferViewModel = bankTransferViewModel
        this.currentState = bankTransferViewModel.uiState
        this.initComposeView()
        GlobalScope.launch {

            bankTransferViewModel.fetchAccounts()
            bankTransferViewModel.fetchExternalAccounts()
        }
    }

    private fun initComposeView() {

        this.composeView?.let { compose ->
            compose.setContent {
                BankTransferView(
                    currentState = currentState,
                    bankTransferViewModel = bankTransferViewModel
                )
            }
        }
    }
}

/**
 * Composable Function for Bank Transfer
 **/

@Composable
fun BankTransferView(
    currentState: MutableState<BankTransferView.ViewState>,
    bankTransferViewModel: BankTransferViewModel?
) {

    // -- Vars
    val showDialog = remember { mutableStateOf(false) }

    // -- Content
    Surface {

        BankTransferView_Loading()

        // -- UIState
        /*when(currentState.value) {

            BankTransferView.ViewState.LOADING -> {
                BankTransferView_Loading()
            }

            BankTransferView.ViewState.IN_LIST -> {
                BankTransferView_List(
                    bankTransferViewModel = bankTransferViewModel
                )
            }
        }*/
    }
}

@Composable
fun BankTransferView_List(
    bankTransferViewModel: BankTransferViewModel?
) {

    // -- Vars
    bankTransferViewModel?.calculateFiatBalance()

    // -- Tabs
    val selectedTabIndex = remember { mutableStateOf(0) }
    val tabsTitles = listOf("Deposit", "Withdraw")

    // -- Select
    val selectExpanded = remember { mutableStateOf(false) }
    val externalBankAccount: MutableState<ExternalBankAccountBankModel?> = remember { mutableStateOf(null) }

    // -- Amount
    val amountMutableState = remember { mutableStateOf("") }

    // -- Dialog
    val showDialog = remember { mutableStateOf(false) }

    // -- Compose
    if (showDialog.value) {
        BankTransferModal(
            bankTransferViewModel = bankTransferViewModel,
            externalBankAccount = externalBankAccount.value,
            showDialog = showDialog,
            selectedTabIndex = selectedTabIndex,
            amountMutableState = amountMutableState
        )
    }

    Column(
        modifier = Modifier
            .background(Color.Transparent)
            .testTag(Constants.AccountsViewTestTags.List.id)
    ) {
        ConstraintLayout(
            Modifier.fillMaxSize()
        ) {

            val (balance, tabs, select, input, button) = createRefs()

            // -- Compose Content
            BankTransferView_List_Balance(
                bankTransferViewModel = bankTransferViewModel,
                modifier = Modifier.constrainAs(balance) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(parent.top, margin = 0.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                }
            )

            BankTransferView_List_Tabs(
                selectedTabIndex = selectedTabIndex,
                tabs = tabsTitles,
                modifier = Modifier.constrainAs(tabs) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(balance.bottom, margin = 32.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                }
            )

            BankTransferView_List_Select(
                selectedTabIndex = selectedTabIndex,
                selectExpanded = selectExpanded,
                externalBankAccount = externalBankAccount,
                bankTransferViewModel = bankTransferViewModel,
                modifier = Modifier
                    .constrainAs(select) {
                        start.linkTo(parent.start, margin = 0.dp)
                        top.linkTo(tabs.bottom, margin = 40.dp)
                        end.linkTo(parent.end, margin = 0.dp)
                    }
                    .fillMaxWidth()
            )

            if (!selectExpanded.value) {
                BankTransferView_List_Input(
                    bankTransferViewModel = bankTransferViewModel,
                    amountMutableState = amountMutableState,
                    modifier = Modifier
                        .constrainAs(input) {
                            start.linkTo(parent.start, margin = 0.dp)
                            top.linkTo(select.bottom, margin = 40.dp)
                            end.linkTo(parent.end, margin = 0.dp)
                        }
                        .fillMaxWidth()
                )
            }

            if (amountMutableState.value != "") {
                BankTransferView_List_Button(
                    showDialog = showDialog,
                    modifier = Modifier
                        .constrainAs(button) {
                            start.linkTo(parent.start, margin = 0.dp)
                            bottom.linkTo(parent.bottom, margin = 20.dp)
                            end.linkTo(parent.end, margin = 0.dp)
                        }
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun BankTransferView_List_Balance(
    bankTransferViewModel: BankTransferViewModel?,
    modifier: Modifier
) {

    // -- Vars
    val balanceFormatted = buildAnnotatedString {
        append(bankTransferViewModel?.fiatBalance ?: "")
        withStyle(style = SpanStyle(
            color = colorResource(id = R.color.list_prices_asset_component_code_color),
            fontFamily = robotoFont,
            fontWeight = FontWeight.Normal,
            fontSize = 17.sp
        )
        ) {
            append(" ${bankTransferViewModel?.currentFiatCurrency}")
        }
    }

    // -- Content
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 25.5.dp)
    ) {

        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Available to Trade",
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

@Composable
fun BankTransferView_List_Tabs(
    selectedTabIndex: MutableState<Int>,
    tabs: List<String>,
    modifier: Modifier) {

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
        },
        modifier = modifier
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
fun BankTransferView_List_Select(
    selectedTabIndex: MutableState<Int>,
    selectExpanded: MutableState<Boolean>,
    externalBankAccount: MutableState<ExternalBankAccountBankModel?>,
    bankTransferViewModel: BankTransferViewModel?,
    modifier: Modifier
) {

    // -- Vars
    val titleText = if (selectedTabIndex.value == 0) {  "From Bank Account" } else { "To Bank Account" }
    val inputWidth = remember { mutableStateOf(Size.Zero) }

    // -- Content
    Column(modifier = modifier) {

        Text(
            modifier = Modifier,
            text = titleText,
            textAlign = TextAlign.Left,
            fontFamily = robotoFont,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp
        )

        BankTransferView_List_Select__Input(
            externalBankAccount = externalBankAccount,
            selectExpanded = selectExpanded,
            inputWidth = inputWidth
        )

        BankTransferView_List_Select__DropDown(
            externalBankAccount = externalBankAccount,
            selectExpanded = selectExpanded,
            inputWidth = inputWidth,
            externalBankAccountList = bankTransferViewModel?.externalBankAccounts ?: listOf()
        )
    }
}

@Composable
fun BankTransferView_List_Select__Input(
    externalBankAccount: MutableState<ExternalBankAccountBankModel?>,
    selectExpanded: MutableState<Boolean>,
    inputWidth: MutableState<Size>) {

    // -- Vars
    val icon = if (selectExpanded.value) { Icons.Filled.ArrowDropUp } else { Icons.Filled.ArrowDropDown }
    val accountMask = externalBankAccount.value?.plaidAccountMask ?: ""
    val accountName = externalBankAccount.value?.plaidAccountName ?: ""
    val accountID = externalBankAccount.value?.plaidInstitutionId ?: ""
    val accountNameToDisplay = "$accountID - $accountName ($accountMask)"

    // -- Content
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = 18.dp)
            .padding(horizontal = 2.dp)
            .height(56.dp)
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                inputWidth.value = coordinates.size.toSize()
            }
            .background(colorResource(id = R.color.select_background))
            .border(
                border = ButtonDefaults.outlinedBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { selectExpanded.value = !selectExpanded.value }
    ) {
        if (externalBankAccount.value != null) {
            Image(
                painter = painterResource(id = R.drawable.test_bank),
                contentDescription = "",
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(32.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = accountNameToDisplay,
                modifier = Modifier
                    .padding(start = 10.dp),
                fontFamily = robotoFont,
                fontWeight = FontWeight.Normal,
                fontSize = 16.5.sp,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        Icon(
            icon,
            contentDescription = "",
            modifier = Modifier
                .size(30.dp)
                .padding(end = 5.dp, top = 5.dp)
                .clickable { selectExpanded.value = !selectExpanded.value }
        )
    }
}

@Composable
fun BankTransferView_List_Select__DropDown(
    externalBankAccount: MutableState<ExternalBankAccountBankModel?>,
    selectExpanded: MutableState<Boolean>,
    inputWidth: MutableState<Size>,
    externalBankAccountList: List<ExternalBankAccountBankModel>
) {

    DropdownMenu(
        expanded = selectExpanded.value,
        onDismissRequest = { selectExpanded.value = false },
        modifier = Modifier
            .width(with(LocalDensity.current) { inputWidth.value.width.toDp() })
            .padding(horizontal = 2.dp)
    ) {
        externalBankAccountList.forEach { account ->

            //val imageID = getImageID(crypto.code.lowercase())
            val accountMask = account.plaidAccountMask
            val accountName = account.plaidAccountName
            val accountID = account.plaidInstitutionId
            val accountNameToDisplay = "$accountID - $accountName ($accountMask)"

            DropdownMenuItem(
                onClick = {

                    externalBankAccount.value = account
                    selectExpanded.value = false
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(vertical = 0.dp)
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.test_bank),
                        contentDescription = "",
                        modifier = Modifier
                            .padding(horizontal = 0.dp)
                            .padding(0.dp)
                            .size(25.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        text = accountNameToDisplay,
                        modifier = Modifier.padding(start = 16.dp),
                        fontFamily = robotoFont,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun BankTransferView_List_Input(
    bankTransferViewModel: BankTransferViewModel?,
    amountMutableState: MutableState<String>,
    modifier: Modifier,
) {

    // -- Focus Manger
    val focusManager = LocalFocusManager.current

    // -- Content
    Column(modifier = modifier) {
        Text(
            modifier = Modifier
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
                .padding(top = 10.dp)
                .padding(horizontal = 2.dp)
                .height(56.dp)
                .fillMaxWidth()
                .background(Color.Transparent)
                .border(
                    border = ButtonDefaults.outlinedBorder,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable {}
        ) {

            Text(
                modifier = Modifier
                    .padding(start = 18.dp),
                text = bankTransferViewModel?.currentFiatCurrency ?: "",
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
                value = amountMutableState.value.filter { it.isDigit() || it == '.' },
                onValueChange = { value ->
                    amountMutableState.value = value.filter { it.isDigit() || it == '.' }
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
        }
    }
}

@Composable
fun BankTransferView_List_Button(
    showDialog: MutableState<Boolean>,
    modifier: Modifier
) {

    Column(modifier = modifier) {

        Button(
            onClick = {
              showDialog.value = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
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
                text = "Continue",
                color = Color.White,
                fontFamily = robotoFont,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
            )
        }
    }
}