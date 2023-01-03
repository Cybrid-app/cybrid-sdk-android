package app.cybrid.sdkandroid.components.bankTransfer

import app.cybrid.cybrid_api_bank.client.infrastructure.ApiClient
import app.cybrid.cybrid_api_bank.client.models.PostQuoteBankModel
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.components.BankTransferView
import app.cybrid.sdkandroid.components.bankTransfer.view.BankTransferViewModel
import app.cybrid.sdkandroid.core.BigDecimal
import app.cybrid.sdkandroid.tools.JSONMock
import app.cybrid.sdkandroid.tools.TestConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class BankTransferViewModelTest {

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

    private fun createViewModel(): BankTransferViewModel {

        Cybrid.instance.invalidToken = false
        return BankTransferViewModel()
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
        Assert.assertEquals(viewModel.uiState.value, BankTransferView.ViewState.IN_LIST)
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
        Assert.assertNotNull(viewModel.assets)
        Assert.assertTrue(viewModel.assets!!.isNotEmpty())
        Assert.assertNotNull(viewModel.accounts)
        Assert.assertTrue(viewModel.accounts.isNotEmpty())
        Assert.assertEquals(viewModel.fiatBalance, "$0.10")
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
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_createTrade() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        viewModel.createQuote(PostQuoteBankModel.Side.deposit, BigDecimal(0))
        viewModel.createTrade()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(viewModel.currentQuote)
        Assert.assertNotNull(viewModel.currentTrade)
    }
}