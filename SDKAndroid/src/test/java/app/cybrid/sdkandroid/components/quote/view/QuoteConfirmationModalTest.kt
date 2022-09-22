package app.cybrid.sdkandroid.components.quote.view

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.core.Constants
import app.cybrid.sdkandroid.tools.TestConstants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
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
        val selectedTabIndex = mutableStateOf(0)

        // -- When
        composeTestRule.setContent {
            QuoteConfirmationModal(
                viewModel = viewModel,
                asset = cryptoAsset,
                pairAsset = fiatAsset,
                selectedTabIndex = selectedTabIndex,
                showDialog = mutableStateOf(true))
        }

        // -- Then
        composeTestRule.onNodeWithText("Quote Pending").assertIsDisplayed()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `Setup QuoteConfirmationLoading`() = runTest {

        // -- When
        composeTestRule.setContent {
            QuoteConfirmationLoading(R.string.trade_flow_quote_confirmation_modal_title)
        }

        // -- Then
        composeTestRule.onNodeWithText("Order Quote").assertIsDisplayed()
        composeTestRule.onNodeWithTag(Constants.QuoteConfirmation.LoadingIndicator.id).assertIsDisplayed()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `Buy Content Test`() = runTest {

        // --
        val viewModel = QuoteViewModel()
        viewModel.quoteBankModel = TestConstants.buyQuote

        // -- When
        composeTestRule.setContent {
            QuoteConfirmationModal(
                viewModel = viewModel,
                asset = mutableStateOf(TestConstants.BTC_ASSET),
                pairAsset = TestConstants.USD_ASSET,
                showDialog = mutableStateOf(true),
                selectedTabIndex = mutableStateOf(0)
            )
        }

        // -- Then
        composeTestRule.onNodeWithText("Purchase amount", true).assertExists()
        composeTestRule.onNodeWithTag("PurchaseAmountId", true)
            .assertExists()
            .assertTextEquals("$250.00 USD")

        composeTestRule.onNodeWithText("Purchase quantity", true).assertExists()
        composeTestRule.onNodeWithTag("PurchaseQuantityId", true)
            .assertExists()
            .assertTextEquals("0.01321413 BTC")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `Sell Content Test`() = runTest {

        // --
        val viewModel = QuoteViewModel()
        viewModel.quoteBankModel = TestConstants.sellQuote

        // -- When
        composeTestRule.setContent {
            QuoteConfirmationModal(
                viewModel = viewModel,
                asset = mutableStateOf(TestConstants.BTC_ASSET),
                pairAsset = TestConstants.USD_ASSET,
                showDialog = mutableStateOf(true),
                selectedTabIndex = mutableStateOf(1)
            )
        }

        // -- Then
        composeTestRule.onNodeWithText("Sell amount", true).assertExists()
        composeTestRule.onNodeWithTag("PurchaseAmountId", true)
            .assertExists()
            .assertTextEquals("$250.00 USD")

        composeTestRule.onNodeWithText("Sell quantity", true).assertExists()
        composeTestRule.onNodeWithTag("PurchaseQuantityId", true)
            .assertExists()
            .assertTextEquals("0.01321413 BTC")
    }
}