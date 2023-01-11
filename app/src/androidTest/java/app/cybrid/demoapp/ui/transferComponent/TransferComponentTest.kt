package app.cybrid.demoapp.ui.transferComponent

import android.content.res.Resources
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import app.cybrid.demoapp.R
import app.cybrid.demoapp.ui.login.LoginActivity
import app.cybrid.demoapp.ui.util.waitUntilExists
import app.cybrid.demoapp.ui.util.waitUntilViewIsDisplayed
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TransferComponentTest {

    @JvmField
    @Rule
    val rule = ActivityTestRule(LoginActivity::class.java)

    @JvmField
    @Rule
    var compose = createComposeRule()
    var composeTransfer = createEmptyComposeRule()

    private lateinit var resources: Resources

    @Before
    fun setup() {

        resources = InstrumentationRegistry.getInstrumentation().context.resources
    }

    @Test
    fun test_component() {

        waitUntilViewIsDisplayed(ViewMatchers.withId(R.id.demo))
        Espresso.onView(ViewMatchers.withId(R.id.demo))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed())).perform(ViewActions.click())

        waitUntilViewIsDisplayed(ViewMatchers.withId(R.id.list))
        Espresso.onView(ViewMatchers.withId(R.id.list))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText("Accounts Component"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed())).perform(
                ViewActions.click()
            )

       compose.waitForIdle()

        // Check for the button for Transfer Funds
        val transferFundsId = "AccountsView_TransferFunds_Button"
    }
}