package app.cybrid.sdkandroid.components.accounts.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.accounts.view.AccountsViewModel
import app.cybrid.sdkandroid.ui.Theme.interFont
import app.cybrid.sdkandroid.ui.Theme.robotoFont

@Composable
fun AccountsView_Balance(
    accountsViewModel: AccountsViewModel?,
    modifier: Modifier
) {

    // -- Vars
    val balanceFormatted = buildAnnotatedString {
        append(accountsViewModel?.totalBalance ?: "")
        withStyle(style = SpanStyle(
            color = colorResource(id = R.color.list_prices_asset_component_code_color),
            fontFamily = robotoFont,
            fontWeight = FontWeight.Normal,
            fontSize = 17.sp
        )
        ) {
            append(" ${accountsViewModel?.currentFiatCurrency}")
        }
    }

    val balanceFiatFormatted = buildAnnotatedString {
        append(accountsViewModel?.totalFiatBalance ?: "")
        withStyle(style = SpanStyle(
            color = colorResource(id = R.color.list_prices_asset_component_code_color),
            fontFamily = robotoFont,
            fontWeight = FontWeight.Normal,
            fontSize = 17.sp
        )
        ) {
            append(" ${accountsViewModel?.currentFiatCurrency}")
        }
    }

    // -- Content
    if (accountsViewModel?.totalBalance != "") {
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
                    text = stringResource(id = R.string.accounts_view_balance_title),
                    modifier = Modifier,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
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

                /*
                Text(
                    text = stringResource(id = R.string.accounts_view_balance_available_title),
                    modifier = Modifier.padding(top = 30.dp),
                    fontFamily = interFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = colorResource(id = R.color.accounts_balance_available_trade_color)
                )

                Text(
                    text = balanceFiatFormatted,
                    modifier = Modifier.
                    padding(top = 1.dp),
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = 23.sp,
                    lineHeight = 32.sp,
                    color = Color.Black
                )

                Text(
                    text = pendingDepositText,
                    modifier = Modifier.padding(top = 1.dp),
                    fontFamily = interFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = colorResource(id = R.color.accounts_pending_deposit_color)
                )
                */
            }
        }
    }
}