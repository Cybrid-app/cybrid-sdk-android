package app.cybrid.sdkandroid.components

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(instrumentedPackages = ["androidx.loader.content"])
class KYCViewUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `AccountsView Loader Test`() {


    }
}