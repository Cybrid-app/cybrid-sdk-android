package app.cybrid.sdkandroid.components

import android.content.res.Resources
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.bankAccounts.view.BankAccountsViewModel
import app.cybrid.sdkandroid.core.Constants
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(instrumentedPackages = ["androidx.loader.content"])
class BankAccountsViewUITest {

    private lateinit var resources: Resources

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {

        resources = InstrumentationRegistry.getInstrumentation().context.resources
    }

    @Test
    fun `BankAccounts Loader Test`() {

        // -- Given
        val bankAccountsViewModel = BankAccountsViewModel()
        val state = mutableStateOf(BankAccountsView.State.LOADING)

        // -- When
        composeTestRule.setContent {
            BankAccountsView(
                viewModel = bankAccountsViewModel,
                currentState = state)
        }

        // -- Then
        composeTestRule.onNodeWithTag(Constants.BankAccountsView.LoadingView.id).assertIsDisplayed()
        composeTestRule.onNodeWithText(resources.getString(R.string.bank_accounts_view_loading_text)).assertExists()
        composeTestRule.onNodeWithTag(Constants.BankAccountsView.LoadingViewIndicator.id).assertIsDisplayed()
    }

    @Test
    fun `BankAccounts Done Test`() {

        // -- Given
        val bankAccountsViewModel = BankAccountsViewModel()
        val state = mutableStateOf(BankAccountsView.State.DONE)

        // -- When
        composeTestRule.setContent {
            BankAccountsView(
                viewModel = bankAccountsViewModel,
                currentState = state)
        }

        // -- Then
        composeTestRule.onNodeWithTag(Constants.BankAccountsView.DoneView.id).assertIsDisplayed()
        composeTestRule.onNodeWithText(resources.getString(R.string.bank_accounts_view_done_text)).assertExists()
        composeTestRule.onNodeWithText(resources.getString(R.string.bank_accounts_view_done_button)).assertExists()
    }

    @Test
    fun `BankAccounts Error Test`() {

        // -- Given
        val bankAccountsViewModel = BankAccountsViewModel()
        val state = mutableStateOf(BankAccountsView.State.ERROR)

        // -- When
        composeTestRule.setContent {
            BankAccountsView(
                viewModel = bankAccountsViewModel,
                currentState = state)
        }

        // -- Then
        composeTestRule.onNodeWithTag(Constants.BankAccountsView.ErrorView.id).assertIsDisplayed()
        composeTestRule.onNodeWithText(resources.getString(R.string.bank_accounts_view_error_text)).assertExists()
        composeTestRule.onNodeWithText(resources.getString(R.string.bank_accounts_view_error_button)).assertExists()
    }
}