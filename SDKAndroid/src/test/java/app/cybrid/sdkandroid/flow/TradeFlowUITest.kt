package app.cybrid.sdkandroid.flow

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.tools.TestConstants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog

//@RunWith(RobolectricTestRunner::class)
//@Config(instrumentedPackages = ["androidx.loader.content"])
@RunWith(AndroidJUnit4::class)
class TradeFlowUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    @Throws(Exception::class)
    fun setUp() {

        ShadowLog.stream = System.out
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `Setup TradeFlow Input`() = runTest {

        val tradeFlow = TradeFlow(InstrumentationRegistry.getInstrumentation().context)
        val amountState = mutableStateOf("2")
        val amountAsset = mutableStateOf(TestConstants.BTC_ASSET)
        val typeOfAmountState = mutableStateOf(AssetBankModel.Type.crypto)

        composeTestRule.setContent {
            tradeFlow.PreQuoteAmountInput(
                amountState = amountState,
                amountAsset = amountAsset,
                typeOfAmountState = typeOfAmountState)
        }

        // -- Then
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().context
                .getString(R.string.trade_flow_text_field_amount_placeholder)
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText("BTC").assertIsDisplayed()
        composeTestRule.onNodeWithText("2").assertIsDisplayed()
        composeTestRule.onNodeWithTag("PreQuoteAmountInputTextFieldTag")
            .assertIsDisplayed()
        amountState.value = "2F"
        composeTestRule.onNodeWithText("2F").assertDoesNotExist()
        composeTestRule.onNodeWithText("2").assertIsDisplayed()
    }
}