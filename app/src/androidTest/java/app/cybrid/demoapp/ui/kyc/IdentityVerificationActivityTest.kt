package app.cybrid.demoapp.ui.kyc

import android.content.res.Resources
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import app.cybrid.demoapp.R
import app.cybrid.demoapp.ui.login.LoginActivity
import app.cybrid.demoapp.ui.util.waitUntilExists
import app.cybrid.demoapp.ui.util.waitUntilViewIsDisplayed
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// TODO: Refactor test class to use new Cybrid instance
//@RunWith(AndroidJUnit4::class)
class IdentityVerificationActivityTest {

    @JvmField
    @Rule
    val rule = ActivityTestRule(LoginActivity::class.java)

    @JvmField
    @Rule
    var compose = createEmptyComposeRule()

    private lateinit var resources: Resources

    //@Before
    fun setup() {

        resources = InstrumentationRegistry.getInstrumentation().context.resources
    }
    
    // TODO: Refactor test case with new structure of multi-tokens
    //@Ignore("Disabled until KYC Bearer Token in ready")
    //@Test
    fun test_makeTheFlow() {

        waitUntilViewIsDisplayed(withId(R.id.demo))
        onView(withId(R.id.demo))
            .check(ViewAssertions.matches(isDisplayed())).perform(ViewActions.click())

        waitUntilViewIsDisplayed(withId(R.id.list))
        onView(withId(R.id.list)).check(ViewAssertions.matches(isDisplayed()))
        onView(withText("KYC Component")).check(ViewAssertions.matches(isDisplayed())).perform(
            ViewActions.click()
        )

        compose.waitForIdle()

        val loadingText = "Checking Identity..."
        compose.waitUntilExists(hasText(loadingText))
        compose.onNodeWithText(loadingText).assertIsDisplayed()

        val requiredText = "Begin identity verification."
        compose.waitUntilExists(hasText(requiredText), 20_000L)
        compose.onNodeWithText(requiredText).assertIsDisplayed()
        compose.onNodeWithText("Begin").performClick()
    }
}