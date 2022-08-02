package app.cybrid.sdkandroid.components.accounts

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
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
    fun setUp() {

        MockKAnnotations.init(this, relaxed = true)
        ShadowLog.stream = System.out

        listPricesViewModel = spyk(ListPricesViewModel())
        accountsViewModel = spyk(AccountsViewModel())

        every { listPricesViewModel.prices } returns TestConstants.prices
        every { listPricesViewModel.assets } returns TestConstants.assets
        every { accountsViewModel.accountsResponse } returns TestConstants.accounts
    }

    @Test
    fun `AccountsView Loader Test`() {

        composeTestRule.setContent {
            AccountsViewLoading()
        }

        composeTestRule.onNodeWithTag(Constants.AccountsViewTestTags.Loading.id).assertIsDisplayed()
        composeTestRule.onNodeWithText("Loading").assertIsDisplayed()
    }
}