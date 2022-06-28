package app.cybrid.sdkandroid.components.quote.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import app.cybrid.sdkandroid.tools.TestConstants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog

@RunWith(RobolectricTestRunner::class)
@Config(instrumentedPackages = ["androidx.loader.content"])
class QuoteConfirmationModalTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    @Throws(Exception::class)
    fun setUp() {
        ShadowLog.stream = System.out
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `Setup Component`() = runTest {

        // -- Given
        val viewModel = QuoteViewModel()
        val cryptoAsset = mutableStateOf(TestConstants.BTC_ASSET)
        val fiatAsset = TestConstants.CAD_ASSET

        // -- When
        composeTestRule.setContent {
            QuoteConfirmationModal(
                viewModel = viewModel,
                asset = cryptoAsset,
                pairAsset = fiatAsset,
                showDialog = mutableStateOf(true))
        }

        // -- Then
        composeTestRule.onNodeWithText("Quote Pending").assertIsDisplayed()
    }
}