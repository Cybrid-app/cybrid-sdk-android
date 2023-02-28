package app.cybrid.demoapp.ui.tradeComponent

import android.content.res.Resources
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import app.cybrid.demoapp.R
import app.cybrid.demoapp.ui.login.LoginActivity
import app.cybrid.demoapp.ui.util.waitUntilExists
import app.cybrid.demoapp.ui.util.waitUntilViewIsDisplayed
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TradeFlowActivityTest {

    @JvmField
    @Rule
    val rule = ActivityTestRule(LoginActivity::class.java)

    @JvmField
    @Rule
    var compose = createEmptyComposeRule()

    private lateinit var resources: Resources

    @Before
    fun setup() {

        resources = InstrumentationRegistry.getInstrumentation().context.resources
    }

    @Test
    fun checkAppFlow() = runTest {

        waitUntilViewIsDisplayed(withId(R.id.demo))
        onView(withId(R.id.demo))
            .check(matches(isDisplayed())).perform(click())

        waitUntilViewIsDisplayed(withId(R.id.list))
        onView(withId(R.id.list)).check(matches(isDisplayed()))
        onView(withText("Trade Component")).check(matches(isDisplayed())).perform(
            click()
        )

        compose.waitForIdle()

        compose.onRoot().printToLog("CybridTEST")
        compose.waitUntilExists(hasTestTag("ListPricesView"), 10_000L)
        compose.onNodeWithTag("ListPricesView").assertIsDisplayed()
        compose.onNodeWithText("Search").assertIsDisplayed()
        compose.onNodeWithText("Bitcoin").assertExists()
        compose.onNodeWithText("Ethereum").assertExists()

        compose.onNodeWithText("Search").performTextInput("Eth")
        compose.onNodeWithText("Bitcoin").assertDoesNotExist()
        compose.onNodeWithText("Ethereum").assertIsDisplayed()

        Thread.sleep(3500)
        compose.onNodeWithText("Eth").performTextInput("")
        compose.onNodeWithText("Bitcoin").assertIsDisplayed()
        compose.onNodeWithText("Ethereum").assertIsDisplayed()
        compose.onNodeWithText("Bitcoin").performClick()
    }
}