package app.cybrid.sdkandroid.components.cryptoTransfer.modal.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewModelScope
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.cryptoTransfer.view.CryptoTransferViewModel
import app.cybrid.sdkandroid.core.AssetPipe
import app.cybrid.sdkandroid.core.BigDecimalPipe
import app.cybrid.sdkandroid.ui.lib.AccountLabelView
import app.cybrid.sdkandroid.ui.lib.ContinueButton
import app.cybrid.sdkandroid.ui.lib.WalletLabelView
import kotlinx.coroutines.launch
import java.math.BigDecimal

@Composable
fun CryptoTransferModal_Quote(
    cryptoTransferViewModel: CryptoTransferViewModel
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        // -- Refs
        val (title, subTitle, account,
            wallet, amount, fee,
            networkFee, confirmButton) = createRefs()
        val horizontalMargin = 25.dp

        val quoteAssetCode = cryptoTransferViewModel.currentQuote.value?.asset ?: ""
        val quoteAsset = Cybrid.assets.find { it.code == quoteAssetCode }
        val quoteAmount = cryptoTransferViewModel.currentQuote.value?.deliverAmount
        val quoteFee = cryptoTransferViewModel.currentQuote.value?.fee
        val quoteNetworkFee = cryptoTransferViewModel.currentQuote.value?.networkFee
        val quoteNetworkFeeAssetCode = cryptoTransferViewModel.currentQuote.value?.networkFeeAsset
        val quoteNetworkFeeAsset =  Cybrid.assets.find { it.code == quoteNetworkFeeAssetCode }

        // -- Content
        Text(
            modifier = Modifier
                .constrainAs(title) {
                    start.linkTo(parent.start, margin = horizontalMargin)
                    top.linkTo(parent.top, margin = 27.dp)
                    end.linkTo(parent.end, margin = horizontalMargin)
                    width = Dimension.fillToConstraints
                },
            text = "Confirm Withdraw",
            style = TextStyle(
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontFamily = FontFamily(Font(R.font.inter_regular)),
                fontWeight = FontWeight(400),
                color = Color.Black,
                letterSpacing = 0.35.sp,
            )
        )
        Text(
            modifier = Modifier
                .constrainAs(subTitle) {
                    start.linkTo(parent.start, margin = horizontalMargin)
                    top.linkTo(title.bottom, margin = 5.dp)
                    end.linkTo(parent.end, margin = horizontalMargin)
                    width = Dimension.fillToConstraints
                },
            text = "Please confirm the withdrawal details are correct. ",
            style = TextStyle(
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontFamily = FontFamily(Font(R.font.inter_regular)),
                fontWeight = FontWeight(400),
                color = Color(0xFF636366),
            )
        )

        // -- Account
        AccountLabelView(
            modifier = Modifier
                .constrainAs(account) {
                    start.linkTo(parent.start, margin = horizontalMargin)
                    top.linkTo(subTitle.bottom, margin = 25.dp)
                    end.linkTo(parent.end, margin = horizontalMargin)
                    width = Dimension.fillToConstraints
                },
            titleText = "From my account",
            account = cryptoTransferViewModel.currentAccount.value!!
        )

        // -- Wallet
        WalletLabelView(
            modifier = Modifier
                .constrainAs(wallet) {
                    start.linkTo(parent.start, margin = horizontalMargin)
                    top.linkTo(account.bottom, margin = 25.dp)
                    end.linkTo(parent.end, margin = horizontalMargin)
                    width = Dimension.fillToConstraints
                },
            titleText = "To my wallet",
            wallet = cryptoTransferViewModel.currentWallet.value!!
        )

        // -- Amount
        var amountValue = AssetPipe.transform(
            value = quoteAmount ?: java.math.BigDecimal.ZERO,
            asset = quoteAsset!!,
            unit = AssetPipe.AssetPipeTrade
        ).toPlainString()
        amountValue += " $quoteAssetCode"
        CryptoTransferModal_Quote__Item(
            modifier = Modifier
                .constrainAs(amount) {
                    start.linkTo(parent.start, margin = horizontalMargin)
                    top.linkTo(wallet.bottom, margin = 25.dp)
                    end.linkTo(parent.end, margin = horizontalMargin)
                    width = Dimension.fillToConstraints
                },
            titleText = "Amount",
            valueText = amountValue
        )

        // -- Fee
        val feeValue = BigDecimalPipe.transform(
            value = AssetPipe.transform(
                value = quoteFee ?: BigDecimal.ZERO,
                asset = cryptoTransferViewModel.fiat!!,
                unit = AssetPipe.AssetPipeTrade
            ),
            asset = cryptoTransferViewModel.fiat
        ) + " ${cryptoTransferViewModel.fiat.code}"
        CryptoTransferModal_Quote__Item(
            modifier = Modifier
                .constrainAs(fee) {
                    start.linkTo(parent.start, margin = horizontalMargin)
                    top.linkTo(amount.bottom, margin = 25.dp)
                    end.linkTo(parent.end, margin = horizontalMargin)
                    width = Dimension.fillToConstraints
                },
            titleText = "Transaction fee",
            valueText = feeValue
        )

        // -- Network Fee
        var networkFeeValue = AssetPipe.transform(
            value = quoteNetworkFee ?: BigDecimal.ZERO,
            asset = quoteNetworkFeeAsset!!,
            unit = AssetPipe.AssetPipeTrade
        ).toPlainString()
        networkFeeValue += " ${quoteNetworkFeeAsset.code}"
        CryptoTransferModal_Quote__Item(
            modifier = Modifier
                .constrainAs(networkFee) {
                    start.linkTo(parent.start, margin = horizontalMargin)
                    top.linkTo(fee.bottom, margin = 25.dp)
                    end.linkTo(parent.end, margin = horizontalMargin)
                    width = Dimension.fillToConstraints
                },
            titleText = "Network fee",
            valueText = networkFeeValue
        )

        // -- Confirm Button
        ContinueButton(
            modifier = Modifier
                .constrainAs(confirmButton) {
                    start.linkTo(parent.start, margin = horizontalMargin)
                    top.linkTo(networkFee.bottom, margin = 25.dp)
                    end.linkTo(parent.end, margin = horizontalMargin)
                    bottom.linkTo(parent.bottom, margin = 20.dp)
                    height = Dimension.value(48.dp)
                    width = Dimension.fillToConstraints
                },
            text = "Confirm",
        ) {
            cryptoTransferViewModel.viewModelScope.let {
                it.launch { cryptoTransferViewModel.createTransfer() }
            }
        }
    }
}

@Composable
fun CryptoTransferModal_Quote__Item(
    modifier: Modifier,
    titleText: String,
    titleColor: Color = colorResource(id = R.color.external_wallets_view_add_wallet_input_title_color),
    titleSize: TextUnit = 15.5.sp,
    titleWeight: Int = 400,
    valueText: String,
    valueColor: Color = Color.Black,
    valueSize: TextUnit = 16.sp,
    valueWeight: Int = 400,
) {

    Column(
        modifier = modifier
    ) {

        // -- Title
        Text(
            text = titleText,
            style = TextStyle(
                fontSize = titleSize,
                lineHeight = 22.sp,
                fontFamily = FontFamily(Font(R.font.inter_regular)),
                fontWeight = FontWeight(titleWeight),
                color = titleColor,
                letterSpacing = 0.5.sp,
            )
        )
        // -- Value
        Text(
            text = valueText,
            modifier = Modifier.padding(top = 5.dp),
            style = TextStyle(
                fontSize = valueSize,
                lineHeight = 22.sp,
                fontFamily = FontFamily(Font(R.font.inter_regular)),
                fontWeight = FontWeight(valueWeight),
                color = valueColor,
                letterSpacing = 0.4.sp,
            )
        )
    }
}