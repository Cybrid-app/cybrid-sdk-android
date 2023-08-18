package app.cybrid.sdkandroid.components.bankAccounts.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import app.cybrid.sdkandroid.components.bankAccounts.view.BankAccountsViewModel
import app.cybrid.sdkandroid.core.Constants
import app.cybrid.sdkandroid.ui.Theme.robotoFont
import kotlinx.coroutines.launch

@Composable
fun BankAccountsView_Done(bankAccountsViewModel: BankAccountsViewModel?) {

    // -- Content
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .testTag(Constants.BankAccountsView.DoneView.id)
    ) {

        val (text, continueButton) = createRefs()

        Row(
            modifier = Modifier.constrainAs(text) {
                start.linkTo(parent.start, margin = 0.dp)
                top.linkTo(parent.top, margin = 0.dp)
                end.linkTo(parent.end, margin = 0.dp)
                bottom.linkTo(parent.bottom, margin = 0.dp)
                centerVerticallyTo(parent)
            }
        ) {
            Image(
                painter = painterResource(id = R.drawable.kyc_verified),
                contentDescription = "",
                modifier = Modifier
                    .padding(top = 0.dp)
                    .padding(0.dp)
                    .size(26.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = stringResource(id = R.string.bank_accounts_view_done_text),
                modifier = Modifier
                    .padding(start = 10.dp),
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                    fontWeight = FontWeight(700),
                    color = colorResource(id = R.color.black)
                )
            )
        }
        // -- Continue Button
        Button(
            onClick = {
                bankAccountsViewModel?.viewModelScope?.launch {
                    bankAccountsViewModel.fetchExternalBankAccounts()
                }
            },
            modifier = Modifier
                .constrainAs(continueButton) {
                    start.linkTo(parent.start, margin = 5.dp)
                    end.linkTo(parent.end, margin = 5.dp)
                    bottom.linkTo(parent.bottom, margin = 7.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.value(48.dp)
                },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = colorResource(id = R.color.primary_color),
                contentColor = Color.White
            )
        ) {
            Text(
                text = stringResource(id = R.string.bank_accounts_view_done_button),
                style = androidx.compose.ui.text.TextStyle(
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