package app.cybrid.sdkandroid.components.bankAccounts.compose

import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewModelScope
import app.cybrid.cybrid_api_bank.client.models.ExternalBankAccountBankModel
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.BankAccountsView
import app.cybrid.sdkandroid.components.bankAccounts.view.BankAccountsViewModel
import app.cybrid.sdkandroid.core.Constants
import app.cybrid.sdkandroid.ui.Theme.interFont
import app.cybrid.sdkandroid.ui.Theme.robotoFont
import com.plaid.link.OpenPlaidLink
import com.plaid.link.result.LinkExit
import com.plaid.link.result.LinkSuccess
import kotlinx.coroutines.launch

@Composable
fun BankAccountsView_Content(bankAccountsViewModel: BankAccountsViewModel?) {

    // -- Content
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .testTag(Constants.BankAccountsView.RequiredView.id)
    ) {

        val (title, listContainer, addExternalAccountButton) = createRefs()

        // -- Title Bank Accounts
        Text(
            text = stringResource(id = R.string.bank_accounts_title),
            modifier = Modifier
                .constrainAs(title) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(parent.top, margin = 50.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                },
            style = TextStyle(
                fontSize = 23.sp,
                lineHeight = 32.sp,
                fontFamily = FontFamily(Font(R.font.roboto_regular)),
                fontWeight = FontWeight(400),
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        )

        // -- List Container
        Box(
            modifier = Modifier
                .constrainAs(listContainer) {
                    start.linkTo(parent.start, margin = 15.dp)
                    top.linkTo(title.bottom, margin = 40.dp)
                    end.linkTo(parent.end, margin = 15.dp)
                    bottom.linkTo(addExternalAccountButton.top, margin = 10.dp)
                    height = Dimension.fillToConstraints
                }
        ) {

            if (bankAccountsViewModel?.accounts != null &&
                bankAccountsViewModel.accounts?.isNotEmpty() == true) {

                BankAccountsView_Content_List(
                    bankAccountsViewModel = bankAccountsViewModel
                )

            } else {

                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.bank_accounts_view_required_text),
                        modifier = Modifier
                            .padding(start = 10.dp, top = 40.dp),
                        fontFamily = robotoFont,
                        fontWeight = FontWeight.Normal,
                        fontSize = 19.sp,
                        lineHeight = 32.sp,
                        color = colorResource(id = R.color.black)
                    )
                }
            }
        }

        // -- Add External Bank Account Button
        Button(
            onClick = {
                bankAccountsViewModel?.showAuthScreen()
            },
            modifier = Modifier
                .constrainAs(addExternalAccountButton) {
                    start.linkTo(parent.start, margin = 10.dp)
                    end.linkTo(parent.end, margin = 10.dp)
                    top.linkTo(listContainer.bottom, margin = 0.dp)
                    bottom.linkTo(parent.bottom, margin = 10.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.value(48.dp)
                },
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

            if (bankAccountsViewModel?.buttonAddAccountsState?.value ==
                BankAccountsView.AddAccountButtonState.LOADING) {

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(20.dp)
                            .padding(top = 0.dp),
                        color = colorResource(id = R.color.white)
                    )
                }

            } else {

                Text(
                    text = stringResource(id = R.string.bank_accounts_view_required_button),
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
fun BankAccountsView_Content_List(
    bankAccountsViewModel: BankAccountsViewModel?
) {

    Column(
        modifier = Modifier
            .background(Color.Transparent)
            .testTag(Constants.AccountsViewTestTags.List.id)
            .padding(start = 18.5.dp, end = 18.5.dp)
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            itemsIndexed(items = bankAccountsViewModel?.accounts ?: listOf()) { _, item ->
                BankAccountsView_Content_List_Item(
                    account = item,
                    bankAccountsViewModel = bankAccountsViewModel!!
                )
            }
        }
    }
}

@Composable
fun BankAccountsView_Content_List_Item(
    account: ExternalBankAccountBankModel,
    bankAccountsViewModel: BankAccountsViewModel
) {

    // -- Vars
    val imageName = if (account.state == ExternalBankAccountBankModel.State.refreshRequired) { "kyc_error" } else { "test_bank" }
    val imageID = getTestImage(LocalContext.current, imageName)
    val accountMask = account.plaidAccountMask
    val accountName = account.plaidAccountName
    val accountID = account.plaidInstitutionId
    val name = "$accountID - $accountName"

    val accountNameBuild = buildAnnotatedString {
        append(name)
        withStyle(style = SpanStyle(
            color = colorResource(id = R.color.list_prices_asset_component_code_color),
            fontFamily = robotoFont,
            fontWeight = FontWeight.Normal
        )
        ) {
            append(" ($accountMask)")
        }
    }

    // -- Content
    Surface(
        color = Color.Transparent,
    ) {

        Column(
            modifier = Modifier.clickable {
                bankAccountsViewModel.showExternalBankAccountDetail(account)
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 0.dp)
                    .height(50.dp)
            ) {

                Image(
                    painter = painterResource(id = imageID),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(horizontal = 0.dp)
                        .padding(0.dp)
                        .size(26.dp),
                    contentScale = ContentScale.Fit
                )
                Column(
                    modifier = Modifier
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = accountNameBuild,
                        modifier = Modifier,
                        fontFamily = interFont,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.5.sp,
                        lineHeight = 20.sp,
                        color = Color.Black
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 3.dp)
                    .height((0.55).dp)
                    .background(colorResource(id = R.color.modal_divider))
            ) {}
        }
    }
}

@SuppressLint("DiscouragedApi")
fun getTestImage(context: Context, name: String): Int {
    return context.resources.getIdentifier(name, "drawable", context.packageName)
}