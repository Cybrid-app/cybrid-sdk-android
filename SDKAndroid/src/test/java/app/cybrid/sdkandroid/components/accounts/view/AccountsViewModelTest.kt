package app.cybrid.sdkandroid.components.accounts.view

import app.cybrid.cybrid_api_bank.client.infrastructure.ApiClient
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.components.AccountsView
import app.cybrid.sdkandroid.components.listprices.view.ListPricesViewModel
import app.cybrid.sdkandroid.components.transfer.view.TransferViewModel
import app.cybrid.sdkandroid.tools.JSONMock
import app.cybrid.sdkandroid.tools.TestConstants
import app.cybrid.sdkandroid.util.Polling
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.spyk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AccountsViewModelTest {

    @ExperimentalCoroutinesApi
    private val scope = TestScope()

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher(scope.testScheduler))
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun prepareClient(state: JSONMock.JSONMockState): ApiClient {

        val interceptor = JSONMock(state)
        val clientBuilder = OkHttpClient()
            .newBuilder().addInterceptor(interceptor)
        return ApiClient(okHttpClientBuilder = clientBuilder)
    }

    private fun createViewModel(): AccountsViewModel {

        Cybrid.instance.invalidToken = false
        val viewModel = AccountsViewModel()
        viewModel.listPricesViewModel = ListPricesViewModel()
        return viewModel
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_init() = runTest {

        // -- Given
        val viewModel = createViewModel()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertEquals(viewModel.uiState.value, AccountsView.ViewState.LOADING)
        Assert.assertEquals(viewModel.showTradeDetail.value, false)
        Assert.assertNotNull(viewModel.listPricesViewModel)
        Assert.assertNull(viewModel.listPricesPolling)
        Assert.assertNull(viewModel.accountsPolling)
        Assert.assertTrue(viewModel.accounts.isEmpty())
        Assert.assertTrue(viewModel.accountsAssetPrice.isEmpty())
        Assert.assertTrue(viewModel.trades.isEmpty())
        Assert.assertNull(viewModel.currentAccountSelected)
        Assert.assertNotNull(viewModel.currentTrade)
        Assert.assertTrue(viewModel.transfers.isEmpty())
        Assert.assertNotNull(viewModel.currentTransfer)
        Assert.assertEquals(viewModel.totalBalance, "")
        Assert.assertEquals(viewModel.totalBalance, "")
        Assert.assertEquals(viewModel.currentFiatCurrency, "USD")
        Assert.assertNotNull(viewModel.customerGuid)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_init_withDataProvider() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertEquals(viewModel.uiState.value, AccountsView.ViewState.LOADING)
        Assert.assertEquals(viewModel.showTradeDetail.value, false)
        Assert.assertNotNull(viewModel.listPricesViewModel)
        Assert.assertNull(viewModel.listPricesPolling)
        Assert.assertNull(viewModel.accountsPolling)
        Assert.assertTrue(viewModel.accounts.isEmpty())
        Assert.assertTrue(viewModel.accountsAssetPrice.isEmpty())
        Assert.assertTrue(viewModel.trades.isEmpty())
        Assert.assertNull(viewModel.currentAccountSelected)
        Assert.assertNotNull(viewModel.currentTrade)
        Assert.assertTrue(viewModel.transfers.isEmpty())
        Assert.assertNotNull(viewModel.currentTransfer)
        Assert.assertEquals(viewModel.totalBalance, "")
        Assert.assertEquals(viewModel.totalBalance, "")
        Assert.assertEquals(viewModel.currentFiatCurrency, "USD")
        Assert.assertNotNull(viewModel.customerGuid)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_getAccountsList_Loading_True() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        viewModel.uiState.value = AccountsView.ViewState.CONTENT
        viewModel.getAccountsList(true)

        // -- Then
        Assert.assertEquals(viewModel.uiState.value, AccountsView.ViewState.LOADING)
        Assert.assertTrue(viewModel.accounts.isNotEmpty())
        Assert.assertNotNull(viewModel.accountsPolling)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_getAccountsList_Loading_False() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        viewModel.uiState.value = AccountsView.ViewState.CONTENT
        viewModel.getAccountsList(false)

        // -- Then
        Assert.assertEquals(viewModel.uiState.value, AccountsView.ViewState.CONTENT)
        Assert.assertTrue(viewModel.accounts.isNotEmpty())
        Assert.assertNotNull(viewModel.accountsPolling)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_getAccountsList_With_Polling() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)
        val pollingToEval = Polling {}

        // -- When
        viewModel.uiState.value = AccountsView.ViewState.CONTENT
        viewModel.accountsPolling = pollingToEval
        viewModel.getAccountsList(true)

        // -- Then
        Assert.assertEquals(viewModel.uiState.value, AccountsView.ViewState.LOADING)
        Assert.assertTrue(viewModel.accounts.isNotEmpty())
        Assert.assertEquals(viewModel.accountsPolling, pollingToEval)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_getPricesList() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)
        viewModel.listPricesViewModel?.setDataProvider(dataProvider)

        // -- When
        viewModel.uiState.value = AccountsView.ViewState.LOADING
        viewModel.getAccountsList()

        // -- Then
        Assert.assertTrue(viewModel.accountsAssetPrice.isNotEmpty())
        Assert.assertEquals(viewModel.uiState.value, AccountsView.ViewState.CONTENT)
        Assert.assertNotNull(viewModel.listPricesPolling)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_getPricesList_Prev_Polling() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)
        viewModel.listPricesViewModel?.setDataProvider(dataProvider)
         val listPricesPollingToEval = Polling {}

        // -- When
        viewModel.uiState.value = AccountsView.ViewState.LOADING
        viewModel.listPricesPolling = listPricesPollingToEval
        viewModel.getAccountsList()

        // -- Then
        Assert.assertTrue(viewModel.accountsAssetPrice.isNotEmpty())
        Assert.assertEquals(viewModel.uiState.value, AccountsView.ViewState.CONTENT)
        Assert.assertEquals(viewModel.listPricesPolling, listPricesPollingToEval)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_getTradesList() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        val account = TestConstants.accountsFormatted[0]

        // -- When
        viewModel.uiState.value = AccountsView.ViewState.LOADING
        viewModel.getTradesList(account)

        // -- Then
        Assert.assertEquals(viewModel.currentAccountSelected, account)
        Assert.assertTrue(viewModel.trades.isNotEmpty())
        Assert.assertEquals(viewModel.uiState.value, AccountsView.ViewState.TRADES)
    }
}