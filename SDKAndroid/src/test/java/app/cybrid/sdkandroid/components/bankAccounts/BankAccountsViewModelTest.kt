package app.cybrid.sdkandroid.components.bankAccounts

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cybrid.cybrid_api_bank.client.infrastructure.ApiClient
import app.cybrid.cybrid_api_bank.client.models.ExternalBankAccountBankModel
import app.cybrid.cybrid_api_bank.client.models.WorkflowBankModel
import app.cybrid.cybrid_api_bank.client.models.WorkflowWithDetailsBankModel
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.components.BankAccountsView
import app.cybrid.sdkandroid.components.bankAccounts.view.BankAccountsViewModel
import app.cybrid.sdkandroid.tools.JSONMock
import app.cybrid.sdkandroid.tools.MainDispatcherRule
import app.cybrid.sdkandroid.util.Polling
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.OkHttpClient
import org.junit.*

class BankAccountsViewModelTest {

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
        Assert.assertEquals(viewModel.uiState.value, BankAccountsView.State.LOADING)
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
        Assert.assertEquals(viewModel.uiState.value, BankAccountsView.State.LOADING)
        Assert.assertNull(viewModel.latestWorkflow)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_fetchExternalBankAccounts() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        Assert.assertEquals(viewModel.uiState.value, BankAccountsView.State.LOADING)
        Assert.assertEquals(viewModel.buttonAddAccountsState.value, BankAccountsView.AddAccountButtonState.LOADING)

        // -- When
        viewModel.fetchExternalBankAccounts()

        // -- Then
        Assert.assertFalse(viewModel.accounts?.isEmpty() == true)
        Assert.assertEquals(viewModel.uiState.value, BankAccountsView.State.CONTENT)
        Assert.assertEquals(viewModel.buttonAddAccountsState.value, BankAccountsView.AddAccountButtonState.LOADING)
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
        Assert.assertNotNull(viewModel.externalAccountJob)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_fetchExternalBankAccount() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)
        viewModel.externalAccountJob = Polling {}

        // -- When
        viewModel.fetchExternalBankAccount("1234")

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(viewModel.externalAccountJob)
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
    }

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
        Assert.assertEquals(viewModel.uiState.value, BankAccountsView.State.LOADING)
    }

    @Test
    fun test_checkExternalBankAccountStatus() {

        // -- Given
        val account = ExternalBankAccountBankModel(state = ExternalBankAccountBankModel.State.completed)
        val viewModel = createViewModel()
        viewModel.uiState.value = BankAccountsView.State.LOADING
        viewModel.externalAccountJob = Polling {}

        // -- When
        viewModel.checkExternalBankAccountStatus(account)

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNull(viewModel.externalAccountJob)
        Assert.assertEquals(viewModel.uiState.value, BankAccountsView.State.DONE)
    }

    @Test
    fun test_checkExternalBankAccountStatus_ContinuePolling() {

        // -- Given
        val account = ExternalBankAccountBankModel(state = ExternalBankAccountBankModel.State.storing)
        val viewModel = createViewModel()
        viewModel.uiState.value = BankAccountsView.State.LOADING
        viewModel.externalAccountJob = Polling {}

        // -- When
        viewModel.checkExternalBankAccountStatus(account)

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(viewModel.externalAccountJob)
        Assert.assertEquals(viewModel.uiState.value, BankAccountsView.State.LOADING)
    }

    @Test
    fun test_showExternalBankAccountDetail() {

        // -- Given
        val viewModel = createViewModel()

        // -- When
        viewModel.showExternalBankAccountDetail(account = ExternalBankAccountBankModel(guid = "1234"))

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(viewModel.currentAccount)
        Assert.assertEquals(viewModel.currentAccount.guid, "1234")
        Assert.assertEquals(viewModel.accountDetailState.value, BankAccountsView.ModalState.CONTENT)
        Assert.assertTrue(viewModel.showAccountDetailModal.value)
    }

    @Test
    fun test_dismissExternalBankAccountDetail() {

        // -- Given
        val viewModel = createViewModel()

        // -- When
        viewModel.dismissExternalBankAccountDetail()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(viewModel.currentAccount)
        Assert.assertNull(viewModel.currentAccount.guid)
        Assert.assertFalse(viewModel.showAccountDetailModal.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_disconnectExternalBankAccount() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        viewModel.showExternalBankAccountDetail(ExternalBankAccountBankModel(guid = "1234"))
        viewModel.disconnectExternalBankAccount()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(viewModel.currentAccount)
        Assert.assertNull(viewModel.currentAccount.guid)
        Assert.assertFalse(viewModel.showAccountDetailModal.value)
        Assert.assertEquals(viewModel.uiState.value, BankAccountsView.State.CONTENT)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_refreshAccount() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        viewModel.workflowUpdateJob = null
        viewModel.refreshAccount()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(viewModel.workflowUpdateJob)
        Assert.assertEquals(viewModel.accountDetailState.value, BankAccountsView.ModalState.LOADING)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_createUpdateWorkflow() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        val workflow = viewModel.createUpdateWorkflow(ExternalBankAccountBankModel(guid = "1234"))

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(workflow)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_fetchUpdateWorkflow() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)
        viewModel.workflowJob = Polling {}

        // -- When
        viewModel.fetchUpdateWorkflow("1234")

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(viewModel.workflowJob)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_fetchUpdateWorkflow_Incomplete() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.EMPTY)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)
        viewModel.workflowJob = Polling {}

        // -- When
        viewModel.fetchUpdateWorkflow("1234")

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(viewModel.workflowJob)
    }

    @Test
    fun test_checkWorkflowUpdateStatus() {

        // -- Given
        val viewModel = createViewModel()

        // -- Null
        viewModel.workflowUpdateJob = Polling {}
        val workflowWithPlaidTokenNull = WorkflowWithDetailsBankModel(plaidLinkToken = null)
        viewModel.checkWorkflowUpdateStatus(workflowWithPlaidTokenNull)
        Assert.assertNotNull(viewModel.workflowUpdateJob)
        Assert.assertNull(viewModel.latestWorkflowUpdate)

        // -- Empty
        viewModel.workflowUpdateJob = Polling {}
        val workflowWithPlaidTokenEmpty = WorkflowWithDetailsBankModel(plaidLinkToken = "")
        viewModel.checkWorkflowUpdateStatus(workflowWithPlaidTokenEmpty)
        Assert.assertNotNull(viewModel.workflowUpdateJob)
        Assert.assertNull(viewModel.latestWorkflowUpdate)

        // -- With Token
        viewModel.workflowUpdateJob = Polling {}
        val workflowWithPlaidToken = WorkflowWithDetailsBankModel(plaidLinkToken = "1234")
        viewModel.checkWorkflowUpdateStatus(workflowWithPlaidToken)
        Assert.assertNull(viewModel.workflowUpdateJob)
        Assert.assertNotNull(viewModel.latestWorkflowUpdate)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_updateExternalBankAccount() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        viewModel.showExternalBankAccountDetail(ExternalBankAccountBankModel(guid = "1234"))
        viewModel.updateExternalBankAccount()
        Assert.assertEquals(viewModel.uiState.value, BankAccountsView.State.CONTENT)
        Assert.assertNotNull(viewModel.currentAccount)
        Assert.assertNull(viewModel.currentAccount.guid)
        Assert.assertFalse(viewModel.showAccountDetailModal.value)
    }
}