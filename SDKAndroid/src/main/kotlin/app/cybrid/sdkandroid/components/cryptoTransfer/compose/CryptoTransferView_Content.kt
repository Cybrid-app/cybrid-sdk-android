package app.cybrid.sdkandroid.components.cryptoTransfer.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import app.cybrid.cybrid_api_bank.client.models.AccountBankModel
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.cybrid_api_bank.client.models.ExternalWalletBankModel
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.cryptoTransfer.view.CryptoTransferViewModel
import app.cybrid.sdkandroid.ui.Theme.robotoFont
import app.cybrid.sdkandroid.ui.lib.RoundedAccountLabelSelector
import app.cybrid.sdkandroid.ui.lib.RoundedWalletSelector
import app.cybrid.sdkandroid.ui.lib.RoundedWalletSelectorLabelSelector

@Composable
fun CryptoTransferView_Content(cryptoTransferViewModel: CryptoTransferViewModel) {

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {

        // -- Vars
        val accounts = cryptoTransferViewModel.accounts
            .filter { it.type == AccountBankModel.Type.trading }
            .sortedBy { it.asset }
        val accountExpandedMutableState = remember { mutableStateOf(false) }
        val selectedAccountMutableState: MutableState<AccountBankModel?> = remember {
            if (accounts.isEmpty()) mutableStateOf(null)
            else mutableStateOf(accounts[0])
        }

        val wallets = cryptoTransferViewModel.wallets
            .sortedBy { it.asset }
        val walletExpandedMutableState = remember { mutableStateOf(false) }
        val selectedWalletMutableState: MutableState<ExternalWalletBankModel?> = remember {
            if (wallets.isEmpty()) mutableStateOf(null)
            else mutableStateOf(wallets[0])
        }

        // -- Refs
        val (title, accountsSelector, walletSelector) = createRefs()

        // -- Content
        // -- Title
        Text(
            modifier = Modifier
                .constrainAs(title) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(parent.top, margin = 15.dp)
                },
            text = "Crypto Transfer",
            style = TextStyle(
                fontSize = 26.sp,
                lineHeight = 32.sp,
                fontFamily = FontFamily(Font(R.font.roboto_regular)),
                fontWeight = FontWeight(800),
                color = Color.Black,
                textAlign = TextAlign.Left,
            )
        )

        // -- RoundedAccountsSelector
        RoundedAccountLabelSelector(
            modifier = Modifier
                .constrainAs(accountsSelector) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(title.bottom, margin = 40.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                    width = Dimension.fillToConstraints
                },
            selectExpandedMutableState = accountExpandedMutableState,
            selectedAccountMutableState = selectedAccountMutableState,
            titleText = "From Account",
            items = accounts
        )

        // -- Rounded Wallet Selector
        RoundedWalletSelectorLabelSelector(
            modifier = Modifier
                .constrainAs(walletSelector) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(accountsSelector.bottom, margin = 20.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                    width = Dimension.fillToConstraints
                },
            selectExpandedMutableState = walletExpandedMutableState,
            selectedWalletMutableState = selectedWalletMutableState,
            titleText = "To Wallet",
            items = wallets
        )

        // -- Amount Input
        Text(
            text = "Amount",
            modifier = Modifier
                .constrainAs(title) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(walletSelector.top, margin = 20.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                    width = Dimension.fillToConstraints
                },
            style = TextStyle(
                fontSize = 15.5.sp,
                lineHeight = 22.sp,
                fontFamily = FontFamily(Font(R.font.inter_regular)),
                fontWeight = FontWeight(400),
                color = colorResource(id = R.color.external_wallets_view_add_wallet_input_title_color),
                textAlign = TextAlign.Left
            )
        )
    }
}

@Composable
fun CryptoTransferView_Content__AmountInput(
    amountState: MutableState<String>,
    amountAsset: MutableState<AssetBankModel?>,
    typeOfAmountState: MutableState<AssetBankModel.Type>
) {

    // -- Focus Manger
    val focusManager = LocalFocusManager.current

    // -- Content
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
            text = amountAsset.value?.code ?: "",
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