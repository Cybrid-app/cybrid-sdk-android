package app.cybrid.demoapp.ui.tradeComponent

import android.content.res.Resources
import androidx.compose.ui.test.*
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import app.cybrid.demoapp.R
import app.cybrid.demoapp.ui.login.LoginActivity
import app.cybrid.demoapp.ui.util.waitUntilViewIsDisplayed
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

class TradeFlowActivityTest {

    /*@JvmField
    @Rule
    val rule = ActivityTestRule(LoginActivity::class.java)*/

    @get : Rule
    var mActivityRule = ActivityScenarioRule(LoginActivity::class.java)

    @JvmField
    @Rule
    var compose = createEmptyComposeRule()

    private lateinit var resources: Resources

    @Before
    fun setup() {

        resources = InstrumentationRegistry.getInstrumentation().context.resources
    }

    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalTestApi::class)
    fun test_flow() = runTest {

        waitUntilViewIsDisplayed(withId(R.id.demo))
        onView(withId(R.id.demo))
            .check(matches(isDisplayed())).perform(click())

        waitUntilViewIsDisplayed(withId(R.id.list))
        onView(withId(R.id.list)).check(matches(isDisplayed()))
        onView(withText("Trade Component")).check(matches(isDisplayed())).perform(
            click()
        )

        compose.waitForIdle()
        compose.waitUntilAtLeastOneExists(hasTestTag("ListPricesView"), 10_000L)
        compose.onRoot().printToLog("Cybrid_E2E")

        compose.onNodeWithTag("ListPricesView").assertIsDisplayed()
        compose.onNodeWithText("Search").assertIsDisplayed()
        compose.onNodeWithText("Bitcoin").assertExists()
        compose.onNodeWithText("Ethereum").assertExists()
        compose.onNodeWithText("Bitcoin").performClick()

        // -- Buy
        compose.waitUntilExactlyOneExists(hasText("Crypto Currency"), 5000L)
        compose.onRoot().printToLog("Cybrid_E2E")
        compose.onNodeWithText("Crypto Currency").assertExists()
        compose.onNodeWithText("Bitcoin").assertExists()

        val amountInput = compose.onNodeWithTag("PreQuoteAmountInputTextFieldTag")
        amountInput.assertExists()
        amountInput.performTextInput("100")

        val buyButton = compose.onNodeWithText("Buy")
        buyButton.assertExists()
        buyButton.performClick()

        compose.onAllNodes(isRoot())[1].printToLog("Cybrid_E2E")
        //compose.onRoot(useUnmergedTree = false).onChildAt(1).printToLog("Cybrid_E2E")
    }
}