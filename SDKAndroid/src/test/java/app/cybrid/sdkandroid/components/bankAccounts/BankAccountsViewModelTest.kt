package app.cybrid.sdkandroid.components.bankAccounts

import app.cybrid.cybrid_api_bank.client.infrastructure.ApiClient
import app.cybrid.cybrid_api_bank.client.models.WorkflowWithDetailsBankModel
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.components.BankAccountsView
import app.cybrid.sdkandroid.components.bankAccounts.view.BankAccountsViewModel
import app.cybrid.sdkandroid.tools.JSONMock
import app.cybrid.sdkandroid.util.Polling
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class BankAccountsViewModelTest {

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

    private fun createViewModel(): BankAccountsViewModel {

        Cybrid.instance.invalidToken = false
        return BankAccountsViewModel()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_init() = runTest {

        // -- Given
        val viewModel = createViewModel()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(viewModel.customerGuid)
        Assert.assertNotNull(viewModel.uiState)
        Assert.assertEquals(viewModel.uiState.value, BankAccountsView.BankAccountsViewState.LOADING)
        Assert.assertNull(viewModel.latestWorkflow)
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
        Assert.assertNotNull(viewModel.uiState)
        Assert.assertEquals(viewModel.uiState.value, BankAccountsView.BankAccountsViewState.LOADING)
        Assert.assertNull(viewModel.latestWorkflow)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_createWorkflow() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        viewModel.createWorkflow()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(viewModel.workflowJob)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_fetchWorkflow() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)
        viewModel.workflowJob = Polling {}

        // -- When
        viewModel.fetchWorkflow("1234")

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(viewModel.workflowJob)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_fetchWorkflow_Incomplete() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.EMPTY)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)
        viewModel.workflowJob = Polling {}

        // -- When
        viewModel.fetchWorkflow("1234")

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(viewModel.workflowJob)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_createExternalBankAccount() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        viewModel.createExternalBankAccount("", null)

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertEquals(viewModel.uiState.value, BankAccountsView.BankAccountsViewState.DONE)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_getCustomer() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        val customer = viewModel.getCustomer()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(customer)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_getBank() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        val bank = viewModel.getBank("1234")

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(bank)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_assetIsSupported() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        val supported = viewModel.assetIsSupported("USD")

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertTrue(supported)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_assetIsSupported_Null() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        val supported = viewModel.assetIsSupported(null)

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertFalse(supported)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_assetIsSupported_AssetNotSupported() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        val supported = viewModel.assetIsSupported("Cybrid")

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertFalse(supported)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_checkWorkflowStatus() {

        // -- Given
        val workflow = WorkflowWithDetailsBankModel(plaidLinkToken = "1234")
        val viewModel = createViewModel()
        viewModel.workflowJob = Polling {}

        // -- When
        viewModel.checkWorkflowStatus(workflow)

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNull(viewModel.workflowJob)
        Assert.assertEquals(viewModel.latestWorkflow, workflow)
        Assert.assertEquals(viewModel.uiState.value, BankAccountsView.BankAccountsViewState.REQUIRED)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_checkWorkflowStatus_ContinuePolling() {

        // -- Given
        val workflow = WorkflowWithDetailsBankModel(plaidLinkToken = "")
        val viewModel = createViewModel()
        viewModel.workflowJob = Polling {}

        // -- When
        viewModel.checkWorkflowStatus(workflow)

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(viewModel.workflowJob)
        Assert.assertNull(viewModel.latestWorkflow)
        Assert.assertEquals(viewModel.uiState.value, BankAccountsView.BankAccountsViewState.LOADING)
    }
}