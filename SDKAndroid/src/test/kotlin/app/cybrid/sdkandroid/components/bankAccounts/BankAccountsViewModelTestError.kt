package app.cybrid.sdkandroid.components.bankAccounts

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cybrid.cybrid_api_bank.client.infrastructure.ApiClient
import app.cybrid.cybrid_api_bank.client.models.ExternalBankAccountBankModel
import app.cybrid.cybrid_api_bank.client.models.PatchExternalBankAccountBankModel
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.components.BankAccountsView
import app.cybrid.sdkandroid.components.bankAccounts.view.BankAccountsViewModel
import app.cybrid.sdkandroid.tools.JSONMock
import app.cybrid.sdkandroid.tools.MainDispatcherRule
import app.cybrid.sdkandroid.util.Polling
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.OkHttpClient
import org.junit.*

class BankAccountsViewModelTestError {

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

        Cybrid.getInstance().invalidToken = false
        return BankAccountsViewModel()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_fetchExternalBankAccounts_Error() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.ERROR)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        Assert.assertEquals(viewModel.uiState.value, BankAccountsView.State.LOADING)
        Assert.assertEquals(viewModel.buttonAddAccountsState.value, BankAccountsView.AddAccountButtonState.LOADING)

        // -- When
        viewModel.fetchExternalBankAccounts()

        // -- Then
        Assert.assertNull(viewModel.accounts)
        Assert.assertEquals(viewModel.uiState.value, BankAccountsView.State.LOADING)
        Assert.assertEquals(viewModel.buttonAddAccountsState.value, BankAccountsView.AddAccountButtonState.LOADING)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_createWorkflow_Error() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.ERROR)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        viewModel.createWorkflow()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNull(viewModel.workflowJob)
        Assert.assertEquals(viewModel.uiState.value, BankAccountsView.State.ERROR)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_fetchWorkflow_Error() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.ERROR)
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
    fun test_createExternalBankAccount_Error() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.ERROR)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        viewModel.createExternalBankAccount("", null)

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertEquals(viewModel.uiState.value, BankAccountsView.State.ERROR)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_fetchExternalBankAccount_Error() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.ERROR)
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
    fun test_getCustomer_Error() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.ERROR)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        val customer = viewModel.getCustomer()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNull(customer)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_getBank_Error() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.ERROR)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        val bank = viewModel.getBank("1234")

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNull(bank)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_assetIsSupported_Error() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.ERROR)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        val supported = viewModel.assetIsSupported("USD")

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertFalse(supported)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_disconnectExternalBankAccount_Error() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.ERROR)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)
        viewModel.externalAccountJob = Polling {}

        // -- When
        viewModel.showExternalBankAccountDetail(ExternalBankAccountBankModel(guid = "1234"))
        viewModel.disconnectExternalBankAccount()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertEquals(viewModel.uiState.value, BankAccountsView.State.LOADING)
        Assert.assertEquals(viewModel.buttonAddAccountsState.value, BankAccountsView.AddAccountButtonState.LOADING)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_createUpdateWorkflow_Error() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.ERROR)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        viewModel.createUpdateWorkflow(ExternalBankAccountBankModel(guid = "1234"))

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNull(viewModel.workflowJob)
        Assert.assertEquals(viewModel.uiState.value, BankAccountsView.State.ERROR)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_fetchUpdateWorkflow_Error() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.ERROR)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)
        viewModel.workflowUpdateJob = Polling {}

        // -- When
        viewModel.fetchUpdateWorkflow("1234")

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(viewModel.workflowUpdateJob)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_updateExternalBankAccount_Error() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.ERROR)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)
        viewModel.workflowUpdateJob = Polling {}

        // -- When
        viewModel.showExternalBankAccountDetail(ExternalBankAccountBankModel(guid = "1234"))
        viewModel.updateExternalBankAccount(state = PatchExternalBankAccountBankModel.State.refreshRequired)

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertEquals(viewModel.uiState.value, BankAccountsView.State.ERROR)
        Assert.assertNotNull(viewModel.currentAccount)
        Assert.assertTrue(viewModel.showAccountDetailModal.value)
    }
}