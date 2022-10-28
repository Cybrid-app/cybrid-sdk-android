package app.cybrid.sdkandroid.components

import android.content.res.Resources
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.kyc.view.IdentityVerificationViewModel
import app.cybrid.sdkandroid.core.Constants
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(instrumentedPackages = ["androidx.loader.content"])
class KYCViewUITest {

    private lateinit var resources: Resources

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {

        resources = InstrumentationRegistry.getInstrumentation().context.resources
    }

    @Test
    fun `KYCView Loader Test`() {

        // -- Given
        val identityVerificationView = IdentityVerificationViewModel()
        val state = mutableStateOf(KYCView.KYCViewState.LOADING)

        // -- When
        composeTestRule.setContent {
            KYCView(
                viewModel = identityVerificationView,
                currentState = state)
        }

        // -- Then
        composeTestRule.onNodeWithTag(Constants.IdentityVerificationView.LoadingView.id).assertIsDisplayed()
        composeTestRule.onNodeWithText(resources.getString(R.string.kyc_view_loading_text)).assertExists()
        composeTestRule.onNodeWithTag(Constants.IdentityVerificationView.LoadingViewIndicator.id).assertIsDisplayed()
    }

    @Test
    fun `KYCView Required Test`() {

        // -- Given
        val identityVerificationView = IdentityVerificationViewModel()
        val state = mutableStateOf(KYCView.KYCViewState.REQUIRED)

        // -- When
        composeTestRule.setContent {
            KYCView(
                viewModel = identityVerificationView,
                currentState = state)
        }

        // -- Then
        composeTestRule.onNodeWithTag(Constants.IdentityVerificationView.RequiredView.id).assertIsDisplayed()
        composeTestRule.onNodeWithText(resources.getString(R.string.kyc_view_required_text)).assertExists()
        composeTestRule.onNodeWithText(resources.getString(R.string.kyc_view_required_cancel_button)).assertExists()
        composeTestRule.onNodeWithText(resources.getString(R.string.kyc_view_required_begin_button)).assertExists()
    }

    @Test
    fun `KYCView Verified Test`() {

        // -- Given
        val identityVerificationView = IdentityVerificationViewModel()
        val state = mutableStateOf(KYCView.KYCViewState.VERIFIED)

        // -- When
        composeTestRule.setContent {
            KYCView(
                viewModel = identityVerificationView,
                currentState = state)
        }

        // -- Then
        composeTestRule.onNodeWithTag(Constants.IdentityVerificationView.VerifiedView.id).assertIsDisplayed()
        composeTestRule.onNodeWithText(resources.getString(R.string.kyc_view_verified_text)).assertExists()
        composeTestRule.onNodeWithText(resources.getString(R.string.kyc_view_required_done_button)).assertExists()
    }

    @Test
    fun `KYCView Error Test`() {

        // -- Given
        val identityVerificationView = IdentityVerificationViewModel()
        val state = mutableStateOf(KYCView.KYCViewState.ERROR)

        // -- When
        composeTestRule.setContent {
            KYCView(
                viewModel = identityVerificationView,
                currentState = state)
        }

        // -- Then
        composeTestRule.onNodeWithTag(Constants.IdentityVerificationView.ErrorView.id).assertIsDisplayed()
        composeTestRule.onNodeWithText(resources.getString(R.string.kyc_view_error_text)).assertExists()
        composeTestRule.onNodeWithText(resources.getString(R.string.kyc_view_required_done_button)).assertExists()
    }

    @Test
    fun `KYCView Reviewing Test`() {

        // -- Given
        val identityVerificationView = IdentityVerificationViewModel()
        val state = mutableStateOf(KYCView.KYCViewState.REVIEWING)

        // -- When
        composeTestRule.setContent {
            KYCView(
                viewModel = identityVerificationView,
                currentState = state)
        }

        // -- Then
        composeTestRule.onNodeWithTag(Constants.IdentityVerificationView.ReviewingView.id).assertIsDisplayed()
        composeTestRule.onNodeWithText(resources.getString(R.string.kyc_view_reviewing_text)).assertExists()
        composeTestRule.onNodeWithText(resources.getString(R.string.kyc_view_required_done_button)).assertExists()
    }
}