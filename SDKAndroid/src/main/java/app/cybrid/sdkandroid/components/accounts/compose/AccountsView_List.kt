package app.cybrid.sdkandroid.components.accounts.compose

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewModelScope
import app.cybrid.cybrid_api_bank.client.models.AccountBankModel
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.AccountsViewStyles
import app.cybrid.sdkandroid.components.accounts.entity.AccountAssetPriceModel
import app.cybrid.sdkandroid.components.accounts.view.AccountsViewModel
import app.cybrid.sdkandroid.components.activity.TransferActivity
import app.cybrid.sdkandroid.core.BigDecimal
import app.cybrid.sdkandroid.core.BigDecimalPipe
import app.cybrid.sdkandroid.core.Constants
import app.cybrid.sdkandroid.ui.Theme.interFont
import app.cybrid.sdkandroid.ui.Theme.robotoFont
import app.cybrid.sdkandroid.util.getImageUrl
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AccountsView_List(
    accountsViewModel: AccountsViewModel
) {

    Column(
        modifier = Modifier
            .background(Color.Transparent)
            .testTag(Constants.AccountsViewTestTags.List.id)
    ) {
        ConstraintLayout(
            Modifier.fillMaxSize()
        ) {

            val (balance, list, button) = createRefs()
            val context = LocalContext.current

            AccountsView_Balance(
                accountsViewModel = accountsViewModel,
                modifier = Modifier.constrainAs(balance) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(parent.top, margin = 0.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                }
            )

            LazyColumn(
                modifier = Modifier.constrainAs(list) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(balance.bottom, margin = 0.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                }
            ) {
                stickyHeader {
                    AccountsView_List_Item_Header(
                        accountsViewModel = accountsViewModel
                    )
                }
                itemsIndexed(items = accountsViewModel.accountsAssetPrice) { _, item ->

                    if (item.accountType == AccountBankModel.Type.trading) {
                        AccountsView_List_Trading_Item(
                            balance = item,
                            accountsViewModel = accountsViewModel
                        )
                    } else {
                        AccountsView_List_Fiat_Item(
                            balance = item,
                            accountsViewModel = accountsViewModel
                        )
                    }
                }
            }

            Button(
                onClick = {
                    context.startActivity(Intent(context, TransferActivity::class.java))
                },
                modifier = Modifier
                    .constrainAs(button) {
                        start.linkTo(parent.start, margin = 10.dp)
                        end.linkTo(parent.end, margin = 10.dp)
                        bottom.linkTo(parent.bottom, margin = 35.dp)
                        width = Dimension.fillToConstraints
                        height = Dimension.value(50.dp)
                    }
                    .testTag(Constants.AccountsViewTestTags.TransferFunds.id),
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
                    text = "Transfer Funds",
                    color = Color.White,
                    fontFamily = interFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    letterSpacing = (-0.4).sp
                )
            }
        }
    }
}

@Composable
fun AccountsView_List_Item_Header(
    styles: AccountsViewStyles = AccountsViewStyles(),
    accountsViewModel: AccountsViewModel
) {

    val priceColor = if (styles.headerTextColor != Color(R.color.list_prices_asset_component_header_color)) {
        styles.headerTextColor
    } else {
        Color.Black
    }

    Surface(color = Color.White) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {

            Column {
                Text(
                    text = stringResource(id = R.string.accounts_view_list_header_asset),
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = styles.headerTextSize,
                    lineHeight = 20.sp,
                    color = priceColor
                )
                Text(
                    text = stringResource(id = R.string.accounts_view_list_header_asset_sub),
                    modifier = Modifier,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = styles.itemsCodeTextSize,
                    lineHeight = 20.sp,
                    color = colorResource(id = R.color.accounts_view_balance_title)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.accounts_view_list_header_balance),
                    modifier = Modifier.align(Alignment.End),
                    textAlign = TextAlign.End,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = styles.headerTextSize,
                    color = priceColor
                )
                Text(
                    text = accountsViewModel.currentFiatCurrency,
                    modifier = Modifier.align(Alignment.End),
                    textAlign = TextAlign.End,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = styles.itemsCodeTextSize,
                    color = styles.itemsCodeTextColor
                )
            }
        }
    }
}

@Composable
fun AccountsView_List_Trading_Item(balance: AccountAssetPriceModel,
    accountsViewModel: AccountsViewModel,
    customStyles: AccountsViewStyles = AccountsViewStyles()
) {

    // -- Vars
    val cryptoCode = balance.accountAssetCode
    val imagePainter = rememberAsyncImagePainter(getImageUrl(cryptoCode.lowercase()))
    val cryptoName = balance.assetName
    val assetNameCode = buildAnnotatedString {
        append(cryptoName)
        withStyle(style = SpanStyle(
            color = colorResource(id = R.color.list_prices_asset_component_code_color),
            fontFamily = robotoFont,
            fontWeight = FontWeight.Normal
        )
        ) {
            append(" $cryptoCode")
        }
    }

    // -- Content
    Surface(color = Color.Transparent) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 0.dp)
                .height(66.dp)
                .clickable {
                    accountsViewModel.viewModelScope.launch {
                        accountsViewModel.getTradesList(balance)
                    }
                },
        ) {

            Image(
                painter = imagePainter,
                contentDescription = "{$cryptoName}",
                modifier = Modifier
                    .padding(horizontal = 0.dp)
                    .padding(0.dp)
                    .size(22.dp),
                contentScale = ContentScale.Fit
            )
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = assetNameCode,
                    modifier = Modifier,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    lineHeight = 20.sp,
                    color = customStyles.itemsTextColor
                )
                Text(
                    text = balance.buyPriceFormatted,
                    modifier = Modifier,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.5.sp,
                    lineHeight = 20.sp,
                    color = customStyles.itemsCodeTextColor
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = balance.accountBalanceFormattedString,
                    modifier = Modifier.align(Alignment.End),
                    textAlign = TextAlign.End,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    color = customStyles.itemsTextColor
                )
                Text(
                    text = balance.accountBalanceInFiatFormatted,
                    modifier = Modifier.align(Alignment.End),
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    color = customStyles.itemsCodeTextColor
                )
            }

        }
    }
}

@Composable
fun AccountsView_List_Fiat_Item(balance: AccountAssetPriceModel,
    accountsViewModel: AccountsViewModel,
    customStyles: AccountsViewStyles = AccountsViewStyles()
) {

    // -- Vars
    val fiatCode = balance.accountAssetCode
    val imagePainter = rememberAsyncImagePainter(getImageUrl(fiatCode.lowercase()))
    val fiatName = balance.assetName
    val assetNameCode = buildAnnotatedString {
        append(fiatName)
        withStyle(style = SpanStyle(
            color = colorResource(id = R.color.list_prices_asset_component_code_color),
            fontFamily = robotoFont,
            fontWeight = FontWeight.Normal
        )
        ) {
            append(" $fiatCode")
        }
    }

    val accountBalance = BigDecimalPipe.transform(balance.accountAvailable, balance.pairAsset)
    val accountPendingBalance = balance.accountBalance - balance.accountAvailable.toJavaBigDecimal()
    var accountPendingBalanceString = BigDecimalPipe.transform(BigDecimal(accountPendingBalance), balance.pairAsset)
    accountPendingBalanceString = "$accountPendingBalanceString ${stringResource(id = R.string.accounts_view_pending_deposit_label)}"

    // -- Content
    Surface(color = Color.Transparent) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 0.dp)
                .height(66.dp)
                .clickable {
                    accountsViewModel.viewModelScope.launch {
                        accountsViewModel.getTransfersList(balance)
                    }
                },
        ) {

            Image(
                painter = imagePainter,
                contentDescription = "{$fiatName}",
                modifier = Modifier
                    .padding(horizontal = 0.dp)
                    .padding(0.dp)
                    .size(22.dp),
                contentScale = ContentScale.Fit
            )
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = assetNameCode,
                    modifier = Modifier,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    lineHeight = 20.sp,
                    color = customStyles.itemsTextColor
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = accountBalance,
                    modifier = Modifier.align(Alignment.End),
                    textAlign = TextAlign.End,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    color = customStyles.itemsTextColor
                )
                Text(
                    text = accountPendingBalanceString,
                    modifier = Modifier.align(Alignment.End),
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = colorResource(id = R.color.accounts_pending_deposit_color)
                )
            }

        }
    }
}