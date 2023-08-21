package app.cybrid.sdkandroid.components.trade.view

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cybrid.cybrid_api_bank.client.infrastructure.ApiClient
import app.cybrid.cybrid_api_bank.client.models.PostQuoteBankModel
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.components.TradeView
import app.cybrid.sdkandroid.components.listprices.view.ListPricesViewModel
import app.cybrid.sdkandroid.tools.JSONMock
import app.cybrid.sdkandroid.tools.MainDispatcherRule
import app.cybrid.sdkandroid.tools.TestConstants
import app.cybrid.sdkandroid.util.Polling
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.OkHttpClient
import org.junit.*

class TradeViewModelTest {

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

    private fun createViewModel(): TradeViewModel {

        Cybrid.invalidToken = false
        val tradeViewModel = TradeViewModel()
        tradeViewModel.listPricesViewModel = ListPricesViewModel()
        return tradeViewModel
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_init() = runTest {

        // -- Given
        val viewModel = createViewModel()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(viewModel.customerGuid)
        Assert.assertNotNull(viewModel.listPricesViewModel)
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
        Assert.assertNotNull(viewModel.customerGuid)
        Assert.assertNotNull(viewModel.listPricesViewModel)
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
        viewModel.getPricesList()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(viewModel.listPricesViewModel)
        Assert.assertNotNull(viewModel.listPricesViewModel?.assets)
        Assert.assertTrue(viewModel.listPricesViewModel?.assets?.isNotEmpty() == true)
        Assert.assertNotNull(viewModel.listPricesViewModel?.prices)
        Assert.assertTrue(viewModel.listPricesViewModel?.prices?.isNotEmpty() == true)

        Assert.assertEquals(viewModel.uiState.value, TradeView.ViewState.LIST_PRICES)
        Assert.assertNotNull(viewModel.listPricesPolling)
    }

    @Test
    fun test_handlePricesOnClick() {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)
        viewModel.listPricesViewModel?.setDataProvider(dataProvider)

        val btc = TestConstants.BTC_ASSET
        val usd = TestConstants.USD_ASSET

        // -- When
        viewModel.handlePricesOnClick(btc, usd)

        // -- Then
        Assert.assertEquals(viewModel.currentAsset.value, btc)
        Assert.assertEquals(viewModel.currentPairAsset.value, usd)
        Assert.assertEquals(viewModel.uiState.value, TradeView.ViewState.QUOTE_CONTENT)

    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_createQuote() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)
        viewModel.listPricesViewModel?.setDataProvider(dataProvider)
        viewModel.postQuoteBankModel = PostQuoteBankModel(side = PostQuoteBankModel.Side.sell)

        // -- When
        viewModel.createQuote()

        // -- Then
        Assert.assertNotNull(viewModel.quoteBankModel)
        Assert.assertNotNull(viewModel.quoteBankModel.guid)
        Assert.assertEquals(viewModel.uiModalState.value, TradeView.QuoteModalViewState.CONTENT)
        Assert.assertNotNull(viewModel.quotePolling)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_createTrade() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)
        viewModel.listPricesViewModel?.setDataProvider(dataProvider)
        viewModel.postQuoteBankModel = PostQuoteBankModel(side = PostQuoteBankModel.Side.sell)
        viewModel.quotePolling = Polling {}

        // -- When
        viewModel.createTrade()

        // -- Then
        Assert.assertNotNull(viewModel.tradeBankModel)
        Assert.assertEquals(viewModel.uiModalState.value, TradeView.QuoteModalViewState.DONE)
        Assert.assertNotNull(viewModel.quoteBankModel)
    }

    @Test
    fun test_modalBeDismissed() {

        // -- Given
        val viewModel = createViewModel()

        viewModel.showModalDialog.value = true

        // -- When
        viewModel.modalBeDismissed()

        // -- Then
        Assert.assertEquals(viewModel.showModalDialog.value, false)
        Assert.assertEquals(viewModel.uiModalState.value, TradeView.QuoteModalViewState.LOADING)
        Assert.assertNull(viewModel.quotePolling)
        Assert.assertNull(viewModel.quoteBankModel.guid)
        Assert.assertNull(viewModel.tradeBankModel.guid)
    }
}