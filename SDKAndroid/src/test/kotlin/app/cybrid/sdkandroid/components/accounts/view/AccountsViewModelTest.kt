package app.cybrid.sdkandroid.components.accounts.view

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cybrid.cybrid_api_bank.client.infrastructure.ApiClient
import app.cybrid.cybrid_api_bank.client.models.TradeBankModel
import app.cybrid.cybrid_api_bank.client.models.TransferBankModel
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.components.AccountsView
import app.cybrid.sdkandroid.components.listprices.view.ListPricesViewModel
import app.cybrid.sdkandroid.tools.JSONMock
import app.cybrid.sdkandroid.tools.MainDispatcherRule
import app.cybrid.sdkandroid.tools.TestConstants
import app.cybrid.sdkandroid.util.Polling
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.OkHttpClient
import org.junit.*
import java.math.BigDecimal

class AccountsViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private fun prepareClient(state: JSONMock.JSONMockState): ApiClient {

        val interceptor = JSONMock(state)
        val clientBuilder = OkHttpClient()
            .newBuilder().addInterceptor(interceptor)
        return ApiClient(okHttpClientBuilder = clientBuilder)
    }

    private fun createViewModel(): AccountsViewModel {

        Cybrid.invalidToken = false
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

    @Test
    fun test_getTradeAmount_Sell() {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)
        viewModel.listPricesViewModel?.assets = TestConstants.assets

        val trade = TradeBankModel(
            guid = "1234",
            customerGuid = "1234",
            quoteGuid = "1234",
            symbol = "BTC-USD",
            side = TradeBankModel.Side.sell,
            state = TradeBankModel.State.completed,
            receiveAmount = BigDecimal(12345),
            deliverAmount = BigDecimal(67891)
        )

        // -- When
        val value = viewModel.getTradeAmount(trade)

        // -- Then
        Assert.assertEquals(value, "0.00067891")
    }

    @Test
    fun test_getTradeAmount_Buy() {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)
        viewModel.listPricesViewModel?.assets = TestConstants.assets

        val trade = TradeBankModel(
            guid = "1234",
            customerGuid = "1234",
            quoteGuid = "1234",
            symbol = "BTC-USD",
            side = TradeBankModel.Side.buy,
            state = TradeBankModel.State.completed,
            receiveAmount = BigDecimal(12345),
            deliverAmount = BigDecimal(67891)
        )

        // -- When
        val value = viewModel.getTradeAmount(trade)

        // -- Then
        Assert.assertEquals(value, "0.00012345")
    }

    @Test
    fun test_getTradeFiatAmount_Sell() {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)
        viewModel.listPricesViewModel?.assets = TestConstants.assets

        val trade = TradeBankModel(
            guid = "1234",
            customerGuid = "1234",
            quoteGuid = "1234",
            symbol = "BTC-USD",
            side = TradeBankModel.Side.sell,
            state = TradeBankModel.State.completed,
            receiveAmount = BigDecimal(12345),
            deliverAmount = BigDecimal(67891)
        )

        // -- When
        val value = viewModel.getTradeFiatAmount(trade)

        // -- Then
        Assert.assertEquals(value, "$123.45")
    }

    @Test
    fun test_getTradeFiatAmount_Buy() {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)
        viewModel.listPricesViewModel?.assets = TestConstants.assets

        val trade = TradeBankModel(
            guid = "1234",
            customerGuid = "1234",
            quoteGuid = "1234",
            symbol = "BTC-USD",
            side = TradeBankModel.Side.buy,
            state = TradeBankModel.State.completed,
            receiveAmount = BigDecimal(12345),
            deliverAmount = BigDecimal(67891)
        )

        // -- When
        val value = viewModel.getTradeFiatAmount(trade)

        // -- Then
        Assert.assertEquals(value, "$678.91")
    }

    @Test
    fun test_showTradeDetail() {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        val trade = TestConstants.trades[0]

        // -- When
        viewModel.showTradeDetail(trade)

        // -- Then
        Assert.assertEquals(viewModel.currentTrade, trade)
        Assert.assertEquals(viewModel.showTradeDetail.value, true)

    }

    @Test
    fun test_dismissTradeDetail() {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        viewModel.showTradeDetail.value = true
        viewModel.dismissTradeDetail()

        // -- Then
        Assert.assertEquals(viewModel.currentTrade.guid, null)
        Assert.assertEquals(viewModel.showTradeDetail.value, false)

    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_getTransfersList() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        val account = TestConstants.accountsFormatted[0]

        // -- When
        viewModel.uiState.value = AccountsView.ViewState.LOADING
        viewModel.getTransfersList(account)

        // -- Then
        Assert.assertEquals(viewModel.currentAccountSelected, account)
        Assert.assertTrue(viewModel.transfers.isNotEmpty())
        Assert.assertEquals(viewModel.uiState.value, AccountsView.ViewState.TRANSFERS)
    }

    @Test
    fun test_getTransferFiatAmount() {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)
        viewModel.listPricesViewModel?.assets = TestConstants.assets

        val transfer = TransferBankModel(
            guid = "1234",
            transferType = TransferBankModel.TransferType.funding,
            customerGuid = "1234",
            quoteGuid = "1234",
            asset = "USD",
            side = TransferBankModel.Side.deposit,
            state = TransferBankModel.State.completed,
            amount = BigDecimal(123456),
        )

        // -- When
        val value = viewModel.getTransferFiatAmount(transfer)

        // -- Then
        Assert.assertEquals(value, "$1,234.56")
    }
}