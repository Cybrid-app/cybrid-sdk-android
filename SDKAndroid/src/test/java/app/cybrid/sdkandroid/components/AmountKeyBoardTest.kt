package app.cybrid.sdkandroid.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog

@RunWith(RobolectricTestRunner::class)
@Config(instrumentedPackages = ["androidx.loader.content"])
class AmountKeyBoardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    @Throws(Exception::class)
    fun setUp() {
        ShadowLog.stream = System.out
    }

    @Test
    fun `Setup Component`() {

        // -- Given
        val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "0")

        // -- When
        composeTestRule.setContent {
            AmountKeyboard()
        }

        // -- Then
        for (key in keys) {
            composeTestRule.onNodeWithText(key).assertIsDisplayed()
        }
    }

    @Test
    fun `Component Click`() {

        // -- Given
        val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "0")
        var amountSet = ""
        var checkSet = ""

        // -- When
        composeTestRule.setContent {
            AmountKeyboard(onAmountChanged = { amount -> amountSet = amount })
        }

        // -- Then
        for (key in keys) {

            composeTestRule.onNodeWithText(key).performClick()
            checkSet += key
            Assert.assertEquals(checkSet, amountSet)
        }
    }
}