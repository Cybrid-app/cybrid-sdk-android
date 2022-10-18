package app.cybrid.sdkandroid.components

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.core.Constants
import app.cybrid.sdkandroid.ui.Theme.robotoFont
import com.withpersona.sdk2.inquiry.Environment
import com.withpersona.sdk2.inquiry.Inquiry
import com.withpersona.sdk2.inquiry.InquiryResponse

class KYCView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0):
    Component(context, attrs, defStyle) {

    enum class KYCViewState { LOADING, REQUIRED, VERIFIED, ERROR, REVIEWING, GLOBAL_ERROR }

    private var currentState = mutableStateOf(KYCViewState.LOADING)

    init {

        LayoutInflater.from(context).inflate(R.layout.kyc_component, this, true)
        this.composeView = findViewById(R.id.composeContent)
        this.initComposeView()
    }

    private fun initComposeView() {

        this.composeView?.let { compose ->
            compose.setContent {
                KYCView(
                    currentState = this.currentState
                )
            }
        }
    }

    companion object {

        fun openPersona(getInquiryResult: ManagedActivityResultLauncher<Inquiry, InquiryResponse>) {

            val TEMPLATE_ID = "itmpl_ArgEXWw8ZYtYLvfC26tr9zmY"
            val inquiry = Inquiry.fromTemplate(TEMPLATE_ID)
                .environment(Environment.SANDBOX)
                .build()
            getInquiryResult.launch(inquiry)
        }
    }
}

/**
 * Composable Functions for Accounts
 * **/

@Composable
fun KYCView(
    currentState: MutableState<KYCView.KYCViewState>
) {

    Handler().postDelayed({
        currentState.value = KYCView.KYCViewState.REQUIRED
    }, 4000)

    // -- Content
    Surface(
        modifier = Modifier
            .testTag(Constants.AccountsViewTestTags.Surface.id)
    ) {

        when(currentState.value) {

            KYCView.KYCViewState.LOADING -> {
                KYCView_Loading()
            }

            KYCView.KYCViewState.REQUIRED -> {
                KYCView_Required()
            }

            else -> {}
        }
    }
}

@Composable
fun KYCView_Loading() {

    Box(
        modifier = Modifier
            .height(120.dp)
            .testTag(Constants.AccountsViewTestTags.Loading.id)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.kyc_view_loading_text),
                fontFamily = robotoFont,
                fontWeight = FontWeight.Normal,
                fontSize = 17.sp,
                color = colorResource(id = R.color.primary_color)
            )
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .testTag(Constants.QuoteConfirmation.LoadingIndicator.id),
                color = colorResource(id = R.color.primary_color)
            )
        }
    }
}

@Composable
fun KYCView_Required() {

    // -- Vars
    val getInquiryResult = rememberLauncherForActivityResult(Inquiry.Contract()) {}

    // -- Content
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .testTag(Constants.AccountsViewTestTags.Loading.id)
    ) {

        val (text, buttons) = createRefs()

        Text(
            text = stringResource(id = R.string.kyc_view_required_text),
            Modifier.constrainAs(text) {
                start.linkTo(parent.start, margin = 0.dp)
                top.linkTo(parent.top, margin = 0.dp)
                end.linkTo(parent.end, margin = 0.dp)
                bottom.linkTo(parent.bottom, margin = 0.dp)
            },
            textAlign = TextAlign.Center,
            fontFamily = robotoFont,
            fontWeight = FontWeight.Medium,
            fontSize = 19.sp,
            color = colorResource(id = R.color.black)
        )
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

            // -- Cancel Button
            Button(
                onClick = {},
                modifier = Modifier
                    .constrainAs(cancelButton) {
                        start.linkTo(parent.start, margin = 10.dp)
                        end.linkTo(beginButton.start, margin = 10.dp)
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
                    backgroundColor = colorResource(id = R.color.accent_blue),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(id = R.string.kyc_view_required_cancel_button),
                    color = Color.White,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
            }
            // -- Continue Button
            Button(
                onClick = {
                    KYCView.openPersona(getInquiryResult)
                },
                modifier = Modifier
                    .constrainAs(beginButton) {
                        start.linkTo(cancelButton.end, margin = 10.dp)
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
                    backgroundColor = colorResource(id = R.color.accent_blue),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(id = R.string.kyc_view_required_begin_button),
                    color = Color.White,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                )
            }
        }
    }
}