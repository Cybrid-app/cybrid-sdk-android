package app.cybrid.sdkandroid.components.transfer

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cybrid.cybrid_api_bank.client.infrastructure.ApiClient
import app.cybrid.cybrid_api_bank.client.models.PostQuoteBankModel
import app.cybrid.cybrid_api_bank.client.models.QuoteBankModel
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.components.TransferView
import app.cybrid.sdkandroid.components.transfer.view.TransferViewModel
import app.cybrid.sdkandroid.core.BigDecimal
import app.cybrid.sdkandroid.tools.JSONMock
import app.cybrid.sdkandroid.tools.MainDispatcherRule
import app.cybrid.sdkandroid.tools.TestConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import okhttp3.OkHttpClient
import org.junit.*

class TransferViewModelTest {

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

        Cybrid.getInstance().invalidToken = false
        return TransferViewModel()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_init() = runTest {

        // -- Given
        val viewModel = createViewModel()

        // -- Then
        Assert.assertNotNull(viewModel)
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
        Assert.assertNotNull(viewModel.customerGuid)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_fetchAssets() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        val assets = viewModel.fetchAssets()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(assets)
        Assert.assertTrue((assets?.size ?: 0) > 0)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_fetchAccounts() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        viewModel.fetchAccounts()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(viewModel.assets)
        Assert.assertTrue(viewModel.assets!!.isNotEmpty())
        Assert.assertNotNull(viewModel.accounts)
        Assert.assertTrue(viewModel.accounts.isNotEmpty())
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_fetchExternalAccounts() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        viewModel.fetchExternalAccounts()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertTrue(viewModel.externalBankAccounts.isNotEmpty())
        Assert.assertEquals(viewModel.uiState.value, TransferView.ViewState.ACCOUNTS)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_calculateFiatBalance() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        viewModel.fetchAccounts()
        viewModel.calculateFiatBalance()

        // -- Then
        Assert.assertNotNull(viewModel)
        /*Assert.assertNotNull(viewModel.assets)
        Assert.assertTrue(viewModel.assets!!.isNotEmpty())
        Assert.assertNotNull(viewModel.accounts)
        Assert.assertTrue(viewModel.accounts.isNotEmpty())
        Assert.assertEquals(viewModel.fiatBalance.value, "$0.10")*/
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_createQuote() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        viewModel.createQuote(PostQuoteBankModel.Side.deposit, BigDecimal(0))

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(viewModel.currentQuote)
        Assert.assertEquals(viewModel.modalUiState.value, TransferView.ModalViewState.CONFIRM)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_createTransfer() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        viewModel.createQuote(PostQuoteBankModel.Side.deposit, BigDecimal(0))
        viewModel.createTransfer(TestConstants.externalBankAccount)

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(viewModel.currentTransfer)
        Assert.assertEquals(viewModel.modalUiState.value, TransferView.ModalViewState.DETAILS)
    }

    @Test
    fun test_transformAmountInBaseBigDecimal() {

        // -- Given
        val viewModel = createViewModel()
        viewModel.assets = TestConstants.assets

        // -- When
        val value = viewModel.transformAmountInBaseBigDecimal("100")

        // -- Then
        Assert.assertEquals(value, BigDecimal("10000"))
    }

    @Test
    fun test_transformAmountInBaseBigDecimal_Currency_MXN() {

        // -- Given
        val viewModel = createViewModel()
        viewModel.assets = TestConstants.assets
        viewModel.currentFiatCurrency = "MXN"

        // -- When
        val value = viewModel.transformAmountInBaseBigDecimal("100")

        // -- Then
        Assert.assertEquals(value, BigDecimal("0"))
    }

    @Test
    fun test_transformQuoteAmountInLabelString() {

        // -- Given
        val viewModel = createViewModel()
        viewModel.assets = TestConstants.assets
        val quote = QuoteBankModel(deliverAmount = java.math.BigDecimal("10000"))

        // -- When
        val label = viewModel.transformQuoteAmountInLabelString(quote)

        // -- Then
        Assert.assertEquals(label, "$100.00")
    }

    @Test
    fun test_transformQuoteAmountInLabelString_Currency_MXN() {

        // -- Given
        val viewModel = createViewModel()
        viewModel.assets = TestConstants.assets
        viewModel.currentFiatCurrency = "MXN"
        val quote = QuoteBankModel(deliverAmount = java.math.BigDecimal("10000"))

        // -- When
        val label = viewModel.transformQuoteAmountInLabelString(quote)

        // -- Then
        Assert.assertEquals(label, "0")
    }
}