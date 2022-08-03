package app.cybrid.sdkandroid.components.accounts.view

import app.cybrid.sdkandroid.components.listprices.view.ListPricesViewModel
import app.cybrid.sdkandroid.tools.TestConstants
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AccountsViewModelTest {

    private lateinit var listPricesViewModel: ListPricesViewModel
    private lateinit var accountsViewModel: AccountsViewModel

    @Before
    fun setUp() {

        MockKAnnotations.init(this, relaxed = true)

        listPricesViewModel = spyk(ListPricesViewModel())
        accountsViewModel = spyk(AccountsViewModel())

        every { listPricesViewModel.prices } returns TestConstants.prices
        every { listPricesViewModel.assets } returns TestConstants.assets
        every { accountsViewModel.accountsResponse } returns TestConstants.accounts
        every { accountsViewModel.trades } returns TestConstants.trades
    }

    @ExperimentalCoroutinesApi
    @Test
    fun initTest() = runBlocking {

        // -- Given
        val viewModel = AccountsViewModel()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(accountsViewModel)
        Assert.assertEquals(accountsViewModel.currentFiatCurrency, "USD")
        Assert.assertNotNull(accountsViewModel.accountsResponse)
        Assert.assertNotNull(accountsViewModel.accounts)
        Assert.assertNotNull(accountsViewModel.trades)
        Assert.assertNotNull(listPricesViewModel)
        Assert.assertNotNull(listPricesViewModel.prices)
    }

    @Test
    fun changeCurrencyTest() {

        // -- Given
        val currencyToEval = "CAD"

        // -- When
        accountsViewModel.currentFiatCurrency = currencyToEval

        // -- Then
        Assert.assertEquals(accountsViewModel.currentFiatCurrency, currencyToEval)
    }

    @Test
    fun createAccountsFormattedTest() {

        accountsViewModel.createAccountsFormatted(listPricesViewModel.prices, listPricesViewModel.assets)
        val accountsFormatted = accountsViewModel.accounts

        Assert.assertEquals(accountsFormatted, TestConstants.accountsFormatted)
    }

    @Test
    fun getTotalBalanceTest() {

        accountsViewModel.createAccountsFormatted(listPricesViewModel.prices, listPricesViewModel.assets)
        accountsViewModel.getCalculatedBalance()

        Assert.assertEquals(accountsViewModel.totalBalance, "$51,687.75")
    }

    @Test
    fun getTradeAmountTest() {

        val trades = TestConstants.trades

        val tradeAmount1 = accountsViewModel.getTradeAmount(
            trade = trades[0],
            assets = listPricesViewModel.assets
        )

        val tradeAmount2 = accountsViewModel.getTradeAmount(
            trade = trades[1],
            assets = listPricesViewModel.assets
        )

        Assert.assertEquals(tradeAmount1, "Ξ0.10")
        Assert.assertEquals(tradeAmount2, "Ξ0.10")
    }
}