package app.cybrid.sdkandroid.components.transfer

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cybrid.cybrid_api_bank.client.infrastructure.ApiClient
import app.cybrid.cybrid_api_bank.client.models.PostQuoteBankModel
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.components.TransferView
import app.cybrid.sdkandroid.components.transfer.view.TransferViewModel
import app.cybrid.sdkandroid.core.BigDecimal
import app.cybrid.sdkandroid.tools.JSONMock
import app.cybrid.sdkandroid.tools.MainDispatcherRule
import app.cybrid.sdkandroid.tools.TestConstants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.OkHttpClient
import org.junit.*

class TransferViewModelTestError {

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

    private fun createViewModel(): TransferViewModel {

        Cybrid.invalidToken = false
        return TransferViewModel()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_fetchAssets_Error() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.ERROR)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        val assets = viewModel.fetchAssets()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNull(assets)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_fetchAccounts_Error() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.ERROR)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        viewModel.fetchAccounts()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNull(viewModel.assets)
        Assert.assertNotNull(viewModel.accounts)
        Assert.assertTrue(viewModel.accounts.isEmpty())
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_fetchExternalAccounts_Error() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.ERROR)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        viewModel.fetchExternalAccounts()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertTrue(viewModel.externalBankAccounts.isEmpty())
        Assert.assertEquals(viewModel.uiState.value, TransferView.ViewState.LOADING)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_calculateFiatBalance_Error() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.ERROR)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        viewModel.fetchAccounts()
        viewModel.calculateFiatBalance()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNull(viewModel.assets)
        Assert.assertNotNull(viewModel.accounts)
        Assert.assertTrue(viewModel.accounts.isEmpty())
        Assert.assertEquals(viewModel.fiatBalance.value, "")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_createQuote_Error() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.ERROR)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        viewModel.createQuote(PostQuoteBankModel.Side.deposit, BigDecimal(0))

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNull(viewModel.currentQuote)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_createTrade_Error() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.ERROR)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        viewModel.createQuote(PostQuoteBankModel.Side.deposit, BigDecimal(0))
        viewModel.createTransfer(TestConstants.externalBankAccount)

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNull(viewModel.currentQuote)
        Assert.assertNull(viewModel.currentTransfer)
    }
}