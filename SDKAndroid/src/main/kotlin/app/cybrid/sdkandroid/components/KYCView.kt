package app.cybrid.sdkandroid.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.kyc.compose.*
import app.cybrid.sdkandroid.components.kyc.view.IdentityVerificationViewModel
import app.cybrid.sdkandroid.core.Constants
import com.withpersona.sdk2.inquiry.Inquiry
import com.withpersona.sdk2.inquiry.InquiryResponse

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
        this.identityViewModel?.uiState = this.currentState
        this.initComposeView()

        identityViewModel.getCustomerStatus()
    }

    private fun initComposeView() {

        this.composeView?.let { compose ->
            compose.setContent {
                KYCView(
                    viewModel = this.identityViewModel!!,
                    currentState = this.currentState
                )

                if (this.identityViewModel?.viewDismiss?.value == true) {
                    (context as AppCompatActivity).finish()
                }
            }
        }
    }

    companion object {

        fun openPersona(
            identityViewModel: IdentityVerificationViewModel,
            getInquiryResult: ManagedActivityResultLauncher<Inquiry, InquiryResponse>
        ) {

            val id = identityViewModel.latestIdentityVerification?.identityVerificationDetails?.personaInquiryId ?: ""
            val inquiry = Inquiry.fromInquiry(id)
                .build()
            getInquiryResult.launch(inquiry)
        }
    }
}

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
                KYCView_Verified(
                    viewModel = viewModel
                )
            }

            KYCView.KYCViewState.ERROR -> {
                KYCView_Error(
                    viewModel = viewModel
                )
            }

            KYCView.KYCViewState.REVIEWING -> {
                KYCView_Reviewing(
                    viewModel = viewModel
                )
            }
        }
    }
}