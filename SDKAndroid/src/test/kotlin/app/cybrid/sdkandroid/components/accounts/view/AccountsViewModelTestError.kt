package app.cybrid.sdkandroid.components.accounts.view

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cybrid.cybrid_api_bank.client.infrastructure.ApiClient
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.components.AccountsView
import app.cybrid.sdkandroid.components.listprices.view.ListPricesViewModel
import app.cybrid.sdkandroid.tools.JSONMock
import app.cybrid.sdkandroid.tools.MainDispatcherRule
import app.cybrid.sdkandroid.tools.TestConstants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.OkHttpClient
import org.junit.*

class AccountsViewModelTestError {

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
    fun test_getAccountsList_Failed() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.ERROR)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        viewModel.uiState.value = AccountsView.ViewState.CONTENT
        viewModel.getAccountsList(true)

        // -- Then
        Assert.assertEquals(viewModel.uiState.value, AccountsView.ViewState.LOADING)
        Assert.assertTrue(viewModel.accounts.isEmpty())
        Assert.assertNull(viewModel.accountsPolling)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_getTradesList_Failed() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.ERROR)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        val account = TestConstants.accountsFormatted[0]

        // -- When
        viewModel.uiState.value = AccountsView.ViewState.LOADING
        viewModel.getTradesList(account)

        // -- Then
        Assert.assertEquals(viewModel.currentAccountSelected, account)
        Assert.assertTrue(viewModel.trades.isEmpty())
        Assert.assertEquals(viewModel.uiState.value, AccountsView.ViewState.LOADING)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_getTransfersList_Failed() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.ERROR)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        val account = TestConstants.accountsFormatted[0]

        // -- When
        viewModel.uiState.value = AccountsView.ViewState.LOADING
        viewModel.getTransfersList(account)

        // -- Then
        Assert.assertEquals(viewModel.currentAccountSelected, account)
        Assert.assertTrue(viewModel.transfers.isEmpty())
        Assert.assertEquals(viewModel.uiState.value, AccountsView.ViewState.LOADING)
    }
}