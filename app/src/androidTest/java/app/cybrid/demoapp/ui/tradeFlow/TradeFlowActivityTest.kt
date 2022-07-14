package app.cybrid.demoapp.ui.tradeFlow

import android.content.res.Resources
import androidx.compose.runtime.currentComposer
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import app.cybrid.demoapp.R
import app.cybrid.demoapp.ui.login.LoginActivity
import org.hamcrest.Matchers.not
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
    var compose = createComposeRule()

    private var resources: Resources? = null

    @Before
    fun init() {

        resources = rule.activity.resources
    }

    @Test
    fun checkListComponent() {

        Thread.sleep(5000)
        onView(withId(R.id.list)).check(matches(isDisplayed()))
        onView(withText("TradeFlow")).check(matches(isDisplayed())).perform(click())

        Thread.sleep(5000)
        compose.onNodeWithTag("ListPricesView").assertIsDisplayed()
        compose.onNodeWithText("Search").assertIsDisplayed()
        compose.onNodeWithText("Bitcoin").assertIsDisplayed()
        compose.onNodeWithText("Ethereum").assertIsDisplayed()

        compose.onNodeWithText("Search").performTextInput("Eth")
        compose.onNodeWithText("Bitcoin").assertDoesNotExist()
        compose.onNodeWithText("Ethereum").assertIsDisplayed()
        Thread.sleep(2000)

        compose.onNodeWithText("Eth").performTextInput("")
        compose.onNodeWithText("Bitcoin").assertIsDisplayed()
        compose.onNodeWithText("Ethereum").assertIsDisplayed()
        compose.onNodeWithText("Bitcoin").performClick()

        //compose. onNodeWithText("Bitcoin").assertIsDisplayed()
        Thread.sleep(3000)
        //compose2.onNodeWithTag("QuoteComponent").assertIsDisplayed()

        //compose.onNodeWithTag("QuoteComponent").assertIsDisplayed()
        //compose.waitForIdle()
        //compose.onNodeWithText("Bitcoin").assertIsDisplayed()
        //compose.onNodeWithText("BTC").assertIsDisplayed()
        //.sleep(3000)
    }
}