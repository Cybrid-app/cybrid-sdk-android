package app.cybrid.demoapp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ActivityScenario
import app.cybrid.demoapp.core.App
import app.cybrid.demoapp.listener.BearerListener
import app.cybrid.demoapp.ui.tradeFlow.TradeFlowActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLog

@RunWith(RobolectricTestRunner::class)
class TradeFlowActivityTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    @Throws(Exception::class)
    fun setUp() {
        ShadowLog.stream = System.out
    }

    @Test
    fun `Check TradeFlow init`() {

        App().getBearer(object : BearerListener {

            override fun onBearerReady() {
                ActivityScenario.launch(TradeFlowActivity::class.java)
                    .use { scenario ->
                        scenario.onActivity { activity: TradeFlowActivity ->

                            composeTestRule.onRoot().printToLog("CybridTEST")
                            composeTestRule.onNodeWithTag("ListPricesView").assertIsDisplayed()
                            composeTestRule.onNodeWithText("Search").assertIsDisplayed()
                            composeTestRule.onNodeWithText("Currency").assertIsDisplayed()
                            composeTestRule.onNodeWithText("Price").assertIsDisplayed()

                            composeTestRule.onNodeWithText("Bitcoin").assertIsDisplayed().performClick()
                        }
                    }
            }

            override fun onBearerError() {}
        })
    }
}