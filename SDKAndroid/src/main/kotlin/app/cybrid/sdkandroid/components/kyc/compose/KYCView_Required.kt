package app.cybrid.sdkandroid.components.kyc.compose

import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.KYCView
import app.cybrid.sdkandroid.components.kyc.view.IdentityVerificationViewModel
import app.cybrid.sdkandroid.core.Constants
import app.cybrid.sdkandroid.ui.Theme.robotoFont
import com.withpersona.sdk2.inquiry.Inquiry
import com.withpersona.sdk2.inquiry.InquiryResponse

@Composable
fun KYCView_Required(
    viewModel: IdentityVerificationViewModel,
    currentState: MutableState<KYCView.KYCViewState>
) {

    // -- Vars
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
            Image(
                painter = painterResource(id = R.drawable.kyc_required),
                contentDescription = "",
                modifier = Modifier
                    .padding(top = 5.dp)
                    .padding(0.dp)
                    .size(26.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = stringResource(id = R.string.kyc_view_required_text),
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
                    KYCView.openPersona(
                        identityViewModel = viewModel,
                        getInquiryResult = getInquiryResult)
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