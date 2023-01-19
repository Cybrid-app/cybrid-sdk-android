package app.cybrid.sdkandroid.components.trade.view

import app.cybrid.cybrid_api_bank.client.infrastructure.ApiClient
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.components.TradeView
import app.cybrid.sdkandroid.components.listprices.view.ListPricesViewModel
import app.cybrid.sdkandroid.tools.JSONMock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class TradeViewModelTestError {

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

    private fun createViewModel(): TradeViewModel {

        Cybrid.instance.invalidToken = false
        val tradeViewModel = TradeViewModel()
        tradeViewModel.listPricesViewModel = ListPricesViewModel()
        return tradeViewModel
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_getPricesList_Empty() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.ERROR)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)
        viewModel.listPricesViewModel?.setDataProvider(dataProvider)

        // -- When
        viewModel.getPricesList()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(viewModel.listPricesViewModel)
        Assert.assertNotNull(viewModel.listPricesViewModel?.assets)
        Assert.assertTrue(viewModel.listPricesViewModel?.assets?.isEmpty() == true)
        Assert.assertNotNull(viewModel.listPricesViewModel?.prices)
        Assert.assertTrue(viewModel.listPricesViewModel?.prices?.isEmpty() == true)

        Assert.assertEquals(viewModel.uiState.value, TradeView.ViewState.LOADING)
        Assert.assertNull(viewModel.listPricesPolling)
    }
}