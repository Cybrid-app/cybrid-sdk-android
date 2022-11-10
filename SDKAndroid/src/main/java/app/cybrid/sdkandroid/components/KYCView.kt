package app.cybrid.sdkandroid.components

import android.content.Context
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
import app.cybrid.sdkandroid.components.kyc.view.IdentityVerificationViewModel
import app.cybrid.sdkandroid.core.Constants
import app.cybrid.sdkandroid.ui.Theme.robotoFont
import com.withpersona.sdk2.inquiry.Inquiry
import com.withpersona.sdk2.inquiry.InquiryResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class KYCView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0):
    Component(context, attrs, defStyle) {

    enum class KYCViewState { LOADING, REQUIRED, VERIFIED, ERROR, REVIEWING }

    private var currentState = mutableStateOf(KYCViewState.LOADING)
    var identityViewModel: IdentityVerificationViewModel? = null

    init {

        LayoutInflater.from(context).inflate(R.layout.kyc_component, this, true)
        this.composeView = findViewById(R.id.composeContent)
    }

    fun setViewModel(identityViewModel: IdentityVerificationViewModel) {

        this.identityViewModel = identityViewModel
        this.identityViewModel?.UIState = this.currentState
        this.initComposeView()

        if (BuildConfig.DEBUG) {
            GlobalScope.launch { identityViewModel.createCustomerTest() }
        } else {
            identityViewModel.getCustomerStatus()
        }
    }

    private fun initComposeView() {

        this.composeView?.let { compose ->
            compose.setContent {
                KYCView(
                    viewModel = this.identityViewModel!!,
                    currentState = this.currentState
                )
            }
        }
    }

    companion object {

        fun openPersona(
            identityViewModel: IdentityVerificationViewModel,
            getInquiryResult: ManagedActivityResultLauncher<Inquiry, InquiryResponse>
        ) {

            val id = identityViewModel.latestIdentityVerification?.personaInquiryId ?: ""
            val inquiry = Inquiry.fromInquiry(id)
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
    viewModel: IdentityVerificationViewModel,
    currentState: MutableState<KYCView.KYCViewState>
) {

    // -- Content
    Surface(
        modifier = Modifier
            .testTag(Constants.IdentityVerificationView.Surface.id)
    ) {

        when(currentState.value) {

            KYCView.KYCViewState.LOADING -> {
                KYCView_Loading()
            }

            KYCView.KYCViewState.REQUIRED -> {
                KYCView_Required(
                    viewModel = viewModel,
                    currentState = currentState
                )
            }

            KYCView.KYCViewState.VERIFIED -> {
                KYCView_Verified()
            }

            KYCView.KYCViewState.ERROR -> {
                KYCView_Error()
            }

            KYCView.KYCViewState.REVIEWING -> {
                KYCView_Reviewing()
            }
        }
    }
}

@Composable
fun KYCView_Loading() {

    Box(
        modifier = Modifier
            .height(120.dp)
            .testTag(Constants.IdentityVerificationView.LoadingView.id)
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
                    .testTag(Constants.IdentityVerificationView.LoadingViewIndicator.id),
                color = colorResource(id = R.color.primary_color)
            )
        }
    }
}

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

            is InquiryResponse.Complete -> {

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

@Composable
fun KYCView_Verified() {

    // -- Content
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .testTag(Constants.IdentityVerificationView.VerifiedView.id)
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
                painter = painterResource(id = R.drawable.kyc_verified),
                contentDescription = "",
                modifier = Modifier
                    .padding(top = 5.dp)
                    .padding(0.dp)
                    .size(26.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = stringResource(id = R.string.kyc_view_verified_text),
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

            // -- Done Button
            Button(
                onClick = {},
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
                    backgroundColor = colorResource(id = R.color.accent_blue),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(id = R.string.kyc_view_required_done_button),
                    color = Color.White,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
            }
        }
    }
}

@Composable
fun KYCView_Error() {

    // -- Content
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .testTag(Constants.IdentityVerificationView.ErrorView.id)
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
                painter = painterResource(id = R.drawable.kyc_error),
                contentDescription = "",
                modifier = Modifier
                    .padding(top = 5.dp)
                    .padding(0.dp)
                    .size(26.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = stringResource(id = R.string.kyc_view_error_text),
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

            // -- Done Button
            Button(
                onClick = {},
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
                    backgroundColor = colorResource(id = R.color.accent_blue),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(id = R.string.kyc_view_required_done_button),
                    color = Color.White,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
            }
        }
    }
}

@Composable
fun KYCView_Reviewing() {

    // -- Content
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .testTag(Constants.IdentityVerificationView.ReviewingView.id)
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
                painter = painterResource(id = R.drawable.kyc_reviewing),
                contentDescription = "",
                modifier = Modifier
                    .padding(top = 5.dp)
                    .padding(0.dp)
                    .size(26.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = stringResource(id = R.string.kyc_view_reviewing_text),
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

            // -- Done Button
            Button(
                onClick = {},
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
                    backgroundColor = colorResource(id = R.color.accent_blue),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(id = R.string.kyc_view_required_done_button),
                    color = Color.White,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
            }
        }
    }
}