package app.cybrid.sdkandroid.components.trade.view

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cybrid.cybrid_api_bank.client.infrastructure.ApiClient
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.components.TradeView
import app.cybrid.sdkandroid.components.listprices.view.ListPricesViewModel
import app.cybrid.sdkandroid.tools.JSONMock
import app.cybrid.sdkandroid.tools.MainDispatcherRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.OkHttpClient
import org.junit.*

class TradeViewModelTestError {

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