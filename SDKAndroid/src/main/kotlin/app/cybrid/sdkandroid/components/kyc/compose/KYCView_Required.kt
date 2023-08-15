package app.cybrid.sdkandroid.components.kyc.compose

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.KYCView
import app.cybrid.sdkandroid.components.kyc.view.IdentityVerificationViewModel
import app.cybrid.sdkandroid.core.Constants
import app.cybrid.sdkandroid.ui.Theme.interFont
import app.cybrid.sdkandroid.ui.Theme.robotoFont
import com.withpersona.sdk2.inquiry.Inquiry
import com.withpersona.sdk2.inquiry.InquiryResponse
import java.time.format.TextStyle

@Composable
fun KYCView_Required(
    viewModel: IdentityVerificationViewModel,
    currentState: MutableState<KYCView.KYCViewState>
) {

    // -- Vars
    val context = LocalContext.current
    val getInquiryResult = rememberLauncherForActivityResult(Inquiry.Contract()) { result ->

        when(result) {

            is InquiryResponse.Complete -> {

                currentState.value = KYCView.KYCViewState.LOADING
                viewModel.getIdentityVerificationStatus(viewModel.latestIdentityVerification)
            }

            is InquiryResponse.Cancel -> {}

            is InquiryResponse.Error -> {

                currentState.value = KYCView.KYCViewState.ERROR
            }
        }
    }

    // -- Content
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .testTag(Constants.IdentityVerificationView.RequiredView.id)
    ) {

        val (text, continueButton) = createRefs()

        // -- Text
        Row(
            modifier = Modifier.constrainAs(text) {
                start.linkTo(parent.start, margin = 0.dp)
                end.linkTo(parent.end, margin = 0.dp)
                centerVerticallyTo(parent)
            }
        ) {
            Image(
                painter = painterResource(id = R.drawable.kyc_required),
                contentDescription = "",
                modifier = Modifier
                    .padding(top = 0.dp)
                    .padding(0.dp)
                    .size(26.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = stringResource(id = R.string.kyc_view_required_text),
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
                KYCView.openPersona(
                    identityViewModel = viewModel,
                    getInquiryResult = getInquiryResult)
            },
            modifier = Modifier
                .constrainAs(continueButton) {
                    start.linkTo(parent.start, margin = 1.dp)
                    end.linkTo(parent.end, margin = 1.dp)
                    bottom.linkTo(parent.bottom, margin = 5.dp)
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
                text = stringResource(id = R.string.kyc_view_required_begin_button),
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

@Preview(showBackground = true, widthDp = 200, heightDp = 300)
@Composable
fun KYCView_Required_Preview() {
    KYCView_Required(
        viewModel = IdentityVerificationViewModel(),
        currentState = remember { mutableStateOf(KYCView.KYCViewState.REQUIRED) }
    )
}