package app.cybrid.sdkandroid.components.transfer.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewModelScope
import app.cybrid.cybrid_api_bank.client.models.ExternalBankAccountBankModel
import app.cybrid.cybrid_api_bank.client.models.PostQuoteBankModel
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.transfer.view.TransferViewModel
import app.cybrid.sdkandroid.core.Constants
import app.cybrid.sdkandroid.ui.Theme.interFont
import app.cybrid.sdkandroid.ui.Theme.robotoFont
import kotlinx.coroutines.launch

@Composable
fun TransferView_Accounts(
    transferViewModel: TransferViewModel?,
    selectedTabIndex: MutableState<Int>,
    externalBankAccount: MutableState<ExternalBankAccountBankModel?>,
    amountMutableState: MutableState<String>,
    showDialog: MutableState<Boolean>,
) {

    // -- Vars
    transferViewModel?.calculateFiatBalance()

    // -- Tabs
    val tabsTitles = stringArrayResource(id = R.array.transfer_view_component_account_tabs)

    // -- Select
    val selectExpanded = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .background(Color.Transparent)
            .testTag(Constants.TransferView.AccountsView.id)
    ) {
        ConstraintLayout(
            Modifier.fillMaxSize()
        ) {

            val (warning, balance, tabs, select, input, button) = createRefs()

            // -- Warning
            var topConstraint: Dp = 10.dp
            if (transferViewModel?.uiWarning?.value == true)  {
                topConstraint = 50.dp
                TransferView_Warning(
                    modifier = Modifier.constrainAs(warning) {
                        start.linkTo(parent.start, margin = 0.dp)
                        top.linkTo(parent.top, margin = 0.dp)
                        end.linkTo(parent.end, margin = 0.dp)
                    }
                )
            }

            // -- Compose Content
            TransferView_Accounts_Balance(
                transferViewModel = transferViewModel,
                modifier = Modifier.constrainAs(balance) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(parent.top, margin = topConstraint)
                    end.linkTo(parent.end, margin = 0.dp)
                }
            )

            TransferView_Accounts_Tabs(
                selectedTabIndex = selectedTabIndex,
                tabs = tabsTitles,
                modifier = Modifier.constrainAs(tabs) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(balance.bottom, margin = 30.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                }
            )

            TransferView_Accounts_Select(
                selectedTabIndex = selectedTabIndex,
                selectExpanded = selectExpanded,
                externalBankAccount = externalBankAccount,
                transferViewModel = transferViewModel,
                modifier = Modifier
                    .constrainAs(select) {
                        start.linkTo(parent.start, margin = 0.dp)
                        top.linkTo(tabs.bottom, margin = 30.dp)
                        end.linkTo(parent.end, margin = 0.dp)
                    }
                    .fillMaxWidth()
            )

            if (!selectExpanded.value) {
                TransferView_Accounts_Input(
                    transferViewModel = transferViewModel,
                    amountMutableState = amountMutableState,
                    modifier = Modifier
                        .constrainAs(input) {
                            start.linkTo(parent.start, margin = 0.dp)
                            top.linkTo(select.bottom, margin = 30.dp)
                            end.linkTo(parent.end, margin = 0.dp)
                        }
                        .fillMaxWidth()
                )
            }

            if (amountMutableState.value != "" &&
                externalBankAccount.value?.state != "refreshRequired") {

                TransferView_Accounts_Button(
                    transferViewModel = transferViewModel,
                    showDialog = showDialog,
                    selectedTabIndex = selectedTabIndex,
                    amountMutableState = amountMutableState,
                    modifier = Modifier
                        .constrainAs(button) {
                            start.linkTo(parent.start, margin = 0.dp)
                            bottom.linkTo(parent.bottom, margin = 2.dp)
                            end.linkTo(parent.end, margin = 0.dp)
                        }
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun TransferView_Accounts_Balance(
    transferViewModel: TransferViewModel?,
    modifier: Modifier
) {

    // -- Vars
    val balanceFormatted = buildAnnotatedString {
        append(transferViewModel?.fiatBalance?.value!!)
        withStyle(style = SpanStyle(
            color = colorResource(id = R.color.list_prices_asset_component_code_color),
            fontFamily = robotoFont,
            fontWeight = FontWeight.Normal,
            fontSize = 17.sp
        )
        ) {
            append(" ${transferViewModel.currentFiatCurrency}")
        }
    }

    // -- Content
    Surface(
        modifier = modifier
            .fillMaxWidth()
    ) {

        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = stringResource(id = R.string.transfer_view_component_balance_title),
                modifier = Modifier,
                fontFamily = interFont,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = colorResource(id = R.color.transfer_view_balance_title_color)
            )

            Text(
                text = balanceFormatted,
                modifier = Modifier.
                    padding(top = 1.dp),
                fontFamily = robotoFont,
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                lineHeight = 32.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
fun TransferView_Accounts_Tabs(
    selectedTabIndex: MutableState<Int>,
    tabs: Array<String>,
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
                        fontFamily = interFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            )
        }
    }
}

@Composable
fun TransferView_Accounts_Select(
    selectedTabIndex: MutableState<Int>,
    selectExpanded: MutableState<Boolean>,
    externalBankAccount: MutableState<ExternalBankAccountBankModel?>,
    transferViewModel: TransferViewModel?,
    modifier: Modifier
) {

    // -- Vars
    val titleText = if (selectedTabIndex.value == 0) {
        stringResource(id = R.string.transfer_view_component_accounts_select_title_from)
    } else {
        stringResource(id = R.string.transfer_view_component_accounts_select_title_to)
    }
    val inputWidth = remember { mutableStateOf(Size.Zero) }

    // -- Content
    Column(modifier = modifier) {

        Text(
            modifier = Modifier,
            text = titleText,
            textAlign = TextAlign.Left,
            fontFamily = interFont,
            fontWeight = FontWeight.Normal,
            color = colorResource(id = R.color.transfer_view_accounts_select_title_color),
            fontSize = 13.sp
        )

        TransferView_Accounts_Select__Input(
            externalBankAccount = externalBankAccount,
            selectExpanded = selectExpanded,
            inputWidth = inputWidth
        )

        TransferView_Accounts_Select__DropDown(
            externalBankAccount = externalBankAccount,
            selectExpanded = selectExpanded,
            inputWidth = inputWidth,
            externalBankAccountList = transferViewModel?.externalBankAccounts ?: listOf()
        )
    }
}

@Composable
fun TransferView_Accounts_Select__Input(
    externalBankAccount: MutableState<ExternalBankAccountBankModel?>,
    selectExpanded: MutableState<Boolean>,
    inputWidth: MutableState<Size>) {

    // -- Vars
    val icon = if (selectExpanded.value) { Icons.Filled.ArrowDropUp } else { Icons.Filled.ArrowDropDown }
    val accountMask = externalBankAccount.value?.plaidAccountMask ?: ""
    val accountName = externalBankAccount.value?.plaidAccountName ?: ""
    val accountID = externalBankAccount.value?.plaidInstitutionId ?: ""
    val accountNameToDisplay = "$accountID - $accountName ($accountMask)"
    val resource = if (externalBankAccount.value?.state == "refreshRequired") {
        painterResource(id = R.drawable.kyc_error)
    } else {
        painterResource(id = R.drawable.test_bank)
    }

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
                painter = resource,
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
                fontFamily = interFont,
                fontWeight = FontWeight.Normal,
                fontSize = 17.sp,
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
fun TransferView_Accounts_Select__DropDown(
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
            val resource = if (account.state == "refreshRequired") {
                painterResource(id = R.drawable.kyc_error)
            } else {
                painterResource(id = R.drawable.test_bank)
            }

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
                        painter = resource,
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
                        fontSize = 17.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun TransferView_Accounts_Input(
    transferViewModel: TransferViewModel?,
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
            color = colorResource(id = R.color.transfer_view_accounts_input_title_color)
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
                text = transferViewModel?.currentFiatCurrency ?: "",
                fontFamily = interFont,
                fontWeight = FontWeight.Normal,
                fontSize = 17.sp,
                color = colorResource(id = R.color.transfer_view_accounts_input_asset_code_color)
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
                    .weight(0.88f),
                //.fillMaxWidth(),
                textStyle = TextStyle(
                    fontFamily = interFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = 17.sp
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
fun TransferView_Accounts_Button(
    transferViewModel: TransferViewModel?,
    showDialog: MutableState<Boolean>,
    selectedTabIndex: MutableState<Int>,
    amountMutableState: MutableState<String>,
    modifier: Modifier
) {

    val quoteSide = if (selectedTabIndex.value == 0) {
        "deposit"
    } else {
        "withdrawal"
    }

    val amount = transferViewModel!!.transformAmountInBaseBigDecimal(amountMutableState.value)

    Column(modifier = modifier) {

        Button(
            onClick = {

                transferViewModel.viewModelScope.launch {
                    transferViewModel.createQuote(side = quoteSide, amount = amount)
                }
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
                text = stringResource(id = R.string.transfer_view_component_accounts_continue_button),
                color = Color.White,
                fontFamily = interFont,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
            )
        }
    }
}