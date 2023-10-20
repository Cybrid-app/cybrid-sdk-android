package app.cybrid.sdkandroid.components.cryptoTransfer.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewModelScope
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.cryptoTransfer.view.CryptoTransferViewModel
import app.cybrid.sdkandroid.ui.lib.AmountLabelInput
import app.cybrid.sdkandroid.ui.lib.ContinueButton
import app.cybrid.sdkandroid.ui.lib.RoundedAccountLabelSelector
import app.cybrid.sdkandroid.ui.lib.RoundedWalletSelectorLabelSelector
import app.cybrid.sdkandroid.util.getImageUrl
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch

@Composable
fun CryptoTransferView_Content(cryptoTransferViewModel: CryptoTransferViewModel) {

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {

        // -- Vars
        val accountsExpandedMutableState = remember { mutableStateOf(false) }
        val walletExpandedMutableState = remember { mutableStateOf(false) }

        // -- Refs
        val (title, accountsSelector,
            walletSelector, amountInput,
            preQuote, error, continueButton) = createRefs()

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
            selectExpandedMutableState = accountsExpandedMutableState,
            selectedAccountMutableState = cryptoTransferViewModel.currentAccount,
            titleText = "From Account",
            items = cryptoTransferViewModel.accounts,
            onClick = { account ->
                cryptoTransferViewModel.changeCurrentAccount(account)
            }
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
            selectedWalletMutableState = cryptoTransferViewModel.currentWallet,
            titleText = "To Wallet",
            items = cryptoTransferViewModel.currentWallets,
            onClick = { wallet ->
                cryptoTransferViewModel.currentWallet.value = wallet
            }
        )

        if (cryptoTransferViewModel.currentWallets.value.isNotEmpty()) {

            // -- Amount Input
            AmountLabelInput(
                modifier = Modifier
                    .constrainAs(amountInput) {
                        start.linkTo(parent.start, margin = 0.dp)
                        top.linkTo(walletSelector.bottom, margin = 20.dp)
                        end.linkTo(parent.end, margin = 0.dp)
                        width = Dimension.fillToConstraints
                    },
                amountState = cryptoTransferViewModel.currentAmountInput,
                assetState = cryptoTransferViewModel.currentAsset,
                counterAsset = cryptoTransferViewModel.fiat,
                isAmountInFiat = cryptoTransferViewModel.isAmountInFiat,
                titleText = "Amount"
            )

            // -- PreQuote Value
            CryptoTransferView_Content__PreQuote(
                modifier = Modifier
                    .constrainAs(preQuote) {
                        start.linkTo(parent.start, margin = 0.dp)
                        top.linkTo(amountInput.bottom, margin = 10.dp)
                        end.linkTo(parent.end, margin = 0.dp)
                        width = Dimension.fillToConstraints
                    },
                cryptoTransferViewModel = cryptoTransferViewModel,
                currentAsset = cryptoTransferViewModel.currentAsset,
                currentCounterAsset = cryptoTransferViewModel.fiat,
                isTransferInFiat = cryptoTransferViewModel.isAmountInFiat
            )

            // -- Error
            if (cryptoTransferViewModel.preQuoteValueHasErrorState.value) {
                Text(
                    modifier = Modifier
                        .constrainAs(error) {
                            start.linkTo(parent.start, margin = 0.dp)
                            top.linkTo(preQuote.bottom, margin = 10.dp)
                            end.linkTo(parent.end, margin = 0.dp)
                            width = Dimension.fillToConstraints
                        },
                    text = "Insufficient Funds",
                    style = TextStyle(
                        fontSize = 13.sp,
                        lineHeight = 24.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        fontWeight = FontWeight(400),
                        color = Color(0xFFE91E26),
                        letterSpacing = 0.5.sp,
                    )
                )
            }

            // -- Continue Button
            if (!cryptoTransferViewModel.preQuoteValueHasErrorState.value &&
                cryptoTransferViewModel.currentAmountInput.value.isNotEmpty()) {
                ContinueButton(
                    modifier = Modifier
                        .constrainAs(continueButton) {
                            start.linkTo(parent.start, margin = 0.dp)
                            top.linkTo(preQuote.bottom, margin = 20.dp)
                            end.linkTo(parent.end, margin = 0.dp)
                            width = Dimension.fillToConstraints
                            height = Dimension.value(48.dp)
                        },
                    text = "Continue",
                    onClick = {
                        cryptoTransferViewModel.viewModelScope.let {
                            it.launch {
                                cryptoTransferViewModel.openModal()
                                cryptoTransferViewModel.createQuote(cryptoTransferViewModel.currentAmountInput.value)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CryptoTransferView_Content__PreQuote(
    modifier: Modifier,
    cryptoTransferViewModel: CryptoTransferViewModel,
    currentAsset: MutableState<AssetBankModel?>,
    currentCounterAsset: AssetBankModel?,
    isTransferInFiat: MutableState<Boolean>
) {
    ConstraintLayout(
        modifier = modifier // h:26
    ) {

        // -- Refs
        val (icon, preQuoteLabel, maxButton) = createRefs()

        // -- Content
        // -- Icon Image
        val assetCode = if (isTransferInFiat.value) { currentAsset.value?.code } else { currentCounterAsset?.code }
        val imagePainter = rememberAsyncImagePainter(getImageUrl(assetCode?.lowercase() ?: ""))
        Image(
            painter = imagePainter,
            contentDescription = "assetCodeDesc",
            modifier = Modifier
                .constrainAs(icon) {
                    start.linkTo(parent.start, margin = 0.dp)
                    centerVerticallyTo(parent)
                    width = Dimension.value(28.dp)
                    height = Dimension.value(24.dp)
                }
        )

        // -- Pre-Quote
        cryptoTransferViewModel.calculatePreQuote()
        Text(
            modifier = Modifier
                .constrainAs(preQuoteLabel) {
                    start.linkTo(icon.end, margin = 5.dp)
                    centerVerticallyTo(parent)
                },
            text = cryptoTransferViewModel.preQuoteValueState.value,
            style = TextStyle(
                fontSize = 13.sp,
                lineHeight = 24.sp,
                fontFamily = FontFamily(Font(R.font.inter_regular)),
                fontWeight = FontWeight(400),
                color = Color(0xDE000000),
                letterSpacing = 0.5.sp,
            )
        )

        // -- Max Button
        if (!isTransferInFiat.value) {
            Text(
                modifier = Modifier
                    .constrainAs(maxButton) {
                        end.linkTo(parent.end, margin = 0.5.dp)
                        centerVerticallyTo(parent)
                    }
                    .clickable {
                        cryptoTransferViewModel.maxButtonClickHandler()
                    },
                text = "MAX",
                style = TextStyle(
                    fontSize = 13.sp,
                    lineHeight = 24.sp,
                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                    fontWeight = FontWeight(400),
                    color = colorResource(id = R.color.primary_color),
                    letterSpacing = 0.5.sp,
                )
            )
        }
    }
}