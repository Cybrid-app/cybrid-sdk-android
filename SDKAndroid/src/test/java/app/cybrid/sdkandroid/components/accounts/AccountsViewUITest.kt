package app.cybrid.sdkandroid.components.accounts

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import app.cybrid.sdkandroid.components.AccountsView
import app.cybrid.sdkandroid.components.AccountsViewLoading
import app.cybrid.sdkandroid.components.accounts.view.AccountsViewModel
import app.cybrid.sdkandroid.components.listprices.view.ListPricesViewModel
import app.cybrid.sdkandroid.core.Constants
import app.cybrid.sdkandroid.tools.TestConstants
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.spyk
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
class AccountsViewUITest {

    private lateinit var listPricesViewModel: ListPricesViewModel
    private lateinit var accountsViewModel: AccountsViewModel

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    @Throws(Exception::class)
    fun setUp() {

        MockKAnnotations.init(this, relaxed = true)
        ShadowLog.stream = System.out

        listPricesViewModel = spyk(ListPricesViewModel())
        accountsViewModel = spyk(AccountsViewModel())

        every { listPricesViewModel.prices } returns TestConstants.prices
        every { listPricesViewModel.assets } returns TestConstants.assets
        every { accountsViewModel.accountsResponse } returns TestConstants.accounts
        every { accountsViewModel.trades } returns TestConstants.trades
    }

    @Test
    fun `AccountsView Loader Test`() {

        composeTestRule.setContent {
            AccountsViewLoading()
        }

        composeTestRule.onNodeWithTag(Constants.AccountsViewTestTags.Loading.id).assertIsDisplayed()
        composeTestRule.onNodeWithText("Loading").assertIsDisplayed()
    }

    @Test
    fun `AccountsView List Prices Test`() {

        composeTestRule.setContent {
            AccountsView(
                currentState = mutableStateOf(AccountsView.AccountsViewState.CONTENT),
                listPricesViewModel = listPricesViewModel,
                accountsViewModel = accountsViewModel)
        }

        //composeTestRule.onNodeWithTag(Constants.AccountsViewTestTags.List.id).assertIsDisplayed()
        composeTestRule.onNodeWithText("Bitcoin_")
    }
}