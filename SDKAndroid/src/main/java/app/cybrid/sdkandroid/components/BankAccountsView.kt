package app.cybrid.sdkandroid.components

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import app.cybrid.sdkandroid.BuildConfig
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.core.Constants
import app.cybrid.sdkandroid.ui.Theme.robotoFont
import com.plaid.link.OpenPlaidLink
import com.plaid.link.configuration.LinkTokenConfiguration
import com.plaid.link.linkTokenConfiguration
import com.plaid.link.result.LinkExit
import com.plaid.link.result.LinkResult
import com.plaid.link.result.LinkSuccess
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BankAccountsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0):
    Component(context, attrs, defStyle) {

    enum class BankAccountsViewState { LOADING, REQUIRED, VERIFIED, ERROR, REVIEWING }

    private var currentState = mutableStateOf(BankAccountsViewState.LOADING)

    init {

        LayoutInflater.from(context).inflate(R.layout.bankaccounts_component, this, true)
        this.composeView = findViewById(R.id.composeContent)
        this.initComposeView()
    }

    private fun initComposeView() {

        this.composeView?.let { compose ->
            compose.setContent {
                BankAccountsView(
                    currentState = this.currentState
                )
            }
        }

        Handler().postDelayed({
            this.currentState.value = BankAccountsViewState.REQUIRED
        }, 5000L)
    }

    companion object {

        fun openPlaid(getPlaidResult: ManagedActivityResultLauncher<LinkTokenConfiguration, LinkResult>) {

            val linkTokenConfiguration = linkTokenConfiguration {
                token = "link-sandbox-09703628-aa09-462f-bdca-2014b9aa3d29"
            }
            getPlaidResult.launch(linkTokenConfiguration)
        }
    }
}

/**
 * Composable Function for Bank Accounts
 **/

@Composable
fun BankAccountsView(
    currentState: MutableState<BankAccountsView.BankAccountsViewState>) {

    // -- Content
    Surface {

        when(currentState.value) {

            BankAccountsView.BankAccountsViewState.LOADING -> {
                BankAccountsView_Loading()
            }

            BankAccountsView.BankAccountsViewState.REQUIRED -> {
                BankAccountsView_Required()
            }

            else -> {}
        }
    }
}


@Composable
fun BankAccountsView_Loading() {

    Box(
        modifier = Modifier
            .height(120.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Checking Bank Accounts",
                fontFamily = robotoFont,
                fontWeight = FontWeight.Normal,
                fontSize = 17.sp,
                color = colorResource(id = R.color.primary_color)
            )
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(top = 16.dp),
                color = colorResource(id = R.color.primary_color)
            )
        }
    }
}

@Composable
fun BankAccountsView_Required() {

    // -- Activity Result for Plaid
    val getPlaidResult = rememberLauncherForActivityResult(OpenPlaidLink()) {
        when (it) {
            is LinkSuccess -> {}
            is LinkExit -> {}
        }
    }

    // -- Content
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .testTag(Constants.AccountsViewTestTags.List.id)
    ) {

        val (text, buttons) = createRefs()

        Row(
            modifier = Modifier.constrainAs(text) {
                start.linkTo(parent.start, margin = 0.dp)
                top.linkTo(parent.top, margin = 0.dp)
                end.linkTo(parent.end, margin = 0.dp)
                bottom.linkTo(parent.bottom, margin = 0.dp)
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            /*Image(
                painter = null,
                contentDescription = "No banks accounts connected.",
                modifier = Modifier
                    .padding(top = 5.dp)
                    .padding(0.dp)
                    .size(26.dp),
                contentScale = ContentScale.Fit
            )*/
            Text(
                text = "No banks accounts connected.",
                modifier = Modifier
                    .padding(start = 10.dp),
                fontFamily = robotoFont,
                fontWeight = FontWeight.Medium,
                fontSize = 19.sp,
                lineHeight = 32.sp,
                color = colorResource(id = R.color.black)
            )
        }
        // -- Buttons
        ConstraintLayout(
            Modifier.constrainAs(buttons) {
                start.linkTo(parent.start, margin = 10.dp)
                end.linkTo(parent.end, margin = 10.dp)
                bottom.linkTo(parent.bottom, margin = 20.dp)
                width = Dimension.fillToConstraints
                height = Dimension.value(50.dp)
            }
        ) {

            val (cancelButton, beginButton) = createRefs()

            // -- Continue Button
            Button(
                onClick = {
                    BankAccountsView.openPlaid(getPlaidResult = getPlaidResult)
                },
                modifier = Modifier
                    .constrainAs(cancelButton) {
                        start.linkTo(parent.start, margin = 10.dp)
                        end.linkTo(parent.end, margin = 10.dp)
                        top.linkTo(parent.top, margin = 0.dp)
                        bottom.linkTo(parent.bottom, margin = 0.dp)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
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
                Text(
                    text = "Add account",
                    color = Color.White,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
            }
        }
    }
}