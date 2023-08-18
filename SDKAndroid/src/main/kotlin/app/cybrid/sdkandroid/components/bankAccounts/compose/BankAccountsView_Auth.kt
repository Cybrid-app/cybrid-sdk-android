package app.cybrid.sdkandroid.components.bankAccounts.compose

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.BankAccountsView
import app.cybrid.sdkandroid.components.bankAccounts.view.BankAccountsViewModel
import app.cybrid.sdkandroid.core.Constants
import com.plaid.link.OpenPlaidLink
import com.plaid.link.result.LinkExit
import com.plaid.link.result.LinkSuccess
import kotlinx.coroutines.launch

@Composable
fun BankAccountsView_Auth(bankAccountsViewModel: BankAccountsViewModel?) {

    // -- Activity Result for Plaid
    val getPlaidResult = rememberLauncherForActivityResult(OpenPlaidLink()) {
        when (it) {
            is LinkSuccess -> {

                if (it.metadata.accounts.size == 1) {

                    bankAccountsViewModel?.uiState?.value = BankAccountsView.State.LOADING
                    bankAccountsViewModel?.viewModelScope?.launch {
                        bankAccountsViewModel.createExternalBankAccount(
                            publicToken = it.publicToken,
                            account = it.metadata.accounts[0])
                    }
                } else {
                    // -- Log multiple accounts or empty accounts
                    bankAccountsViewModel?.uiState?.value = BankAccountsView.State.ERROR
                }
            }
            is LinkExit -> {}
        }
    }

    // -- Content
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .testTag(Constants.BankAccountsView.AuthView.id)
    ) {

        val (imageTitle, title, warningContainer, message, continueButton) = createRefs()

        // -- Image title
        Image(
            painter = painterResource(id = R.drawable.bank_auth_image),
            contentDescription = "",
            modifier = Modifier
                .constrainAs(imageTitle) {
                    centerHorizontallyTo(parent)
                    top.linkTo(parent.top, margin = 26.dp)
                    width = Dimension.value(183.dp)
                    height = Dimension.value(174.dp)
                },
            contentScale = ContentScale.FillBounds
        )

        // -- Title
        Text(
            modifier = Modifier
                .constrainAs(title) {
                    centerHorizontallyTo(parent)
                    top.linkTo(imageTitle.bottom, margin = 17.dp)
                },
            text = stringResource(id = R.string.bank_accounts_view_auth_title),
            style = TextStyle(
                fontSize = 23.sp,
                lineHeight = 32.sp,
                fontFamily = FontFamily(Font(R.font.roboto_regular)),
                fontWeight = FontWeight(400),
                color = Color.Black,
                textAlign = TextAlign.Center,
            )
        )

        // -- Warning Container
        Box(
            modifier = Modifier
                .constrainAs(warningContainer) {
                    top.linkTo(title.bottom, margin = 18.dp)
                    start.linkTo(parent.start, margin = 23.dp)
                    end.linkTo(parent.end, margin = 23.dp)
                    width = Dimension.fillToConstraints
                }
                .background(
                    colorResource(id = R.color.bank_accounts_view_auth_warning_container),
                    shape = RoundedCornerShape(size = 10.dp)
                )
        ) {
            Text(
                text = stringResource(id = R.string.bank_accounts_view_auth_warning),
                modifier = Modifier.padding(17.dp),
                style = TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                    fontWeight = FontWeight(700),
                    color = Color.Black,
                    textAlign = TextAlign.Justify
                )
            )
        }

        // -- Message
        Text(
            modifier = Modifier
                .constrainAs(message) {
                    top.linkTo(warningContainer.bottom, margin = 17.dp)
                    start.linkTo(parent.start, margin = 26.dp)
                    end.linkTo(parent.end, margin = 26.dp)
                    width = Dimension.fillToConstraints
                },
            text = stringResource(id = R.string.bank_accounts_view_auth_message),
            style = TextStyle(
                fontSize = 18.sp,
                lineHeight = 24.sp,
                fontFamily = FontFamily(Font(R.font.inter_regular)),
                fontWeight = FontWeight(400),
                color = Color.Black,
                textAlign = TextAlign.Justify
            )
        )

        // -- Continue Button
        Button(
            onClick = {
                if (bankAccountsViewModel?.buttonAddAccountsState?.value ==
                    BankAccountsView.AddAccountButtonState.READY) {

                    BankAccountsView.openPlaid(
                        plaidToken = bankAccountsViewModel.latestWorkflow?.plaidLinkToken!!,
                        getPlaidResult = getPlaidResult)
                }
            },
            modifier = Modifier
                .constrainAs(continueButton) {
                    start.linkTo(parent.start, margin = 1.dp)
                    top.linkTo(message.bottom, margin = 20.dp)
                    end.linkTo(parent.end, margin = 1.dp)
                    bottom.linkTo(parent.bottom, margin = 15.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.value(48.dp)
                },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = colorResource(id = R.color.accent_blue),
                contentColor = Color.White
            )
        ) {
            Text(
                text = stringResource(id = R.string.bank_accounts_view_auth_continue_button),
                style = TextStyle(
                    fontSize = 17.sp,
                    lineHeight = 22.sp,
                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                    fontWeight = FontWeight(400),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )
            )
        }
    }
}