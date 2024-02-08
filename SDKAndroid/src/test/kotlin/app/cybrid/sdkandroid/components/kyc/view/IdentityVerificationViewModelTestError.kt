package app.cybrid.sdkandroid.components.kyc.view

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.mutableStateOf
import app.cybrid.cybrid_api_bank.client.infrastructure.ApiClient
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.components.KYCView
import app.cybrid.sdkandroid.tools.JSONMock
import app.cybrid.sdkandroid.tools.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import org.junit.*

class IdentityVerificationViewModelTestError {

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

    private fun createViewModel(): IdentityVerificationViewModel {

        Cybrid.invalidToken = false
        val viewModel = IdentityVerificationViewModel()
        viewModel.uiState = mutableStateOf(KYCView.KYCViewState.LOADING)
        return viewModel
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_createCustomerTest_Error() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.ERROR)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)
        val originalCustomerGuid = viewModel.customerGuid

        // -- When
        viewModel.createCustomerTest()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertEquals(viewModel.customerGuid, originalCustomerGuid)
        Assert.assertEquals(viewModel.uiState?.value, KYCView.KYCViewState.LOADING)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_fetchIdentityVerificationWithDetailsStatus_Error() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.ERROR)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        val identityStatus = viewModel.fetchIdentityVerificationWithDetailsStatus("1234")

        // -- Then
        Assert.assertNull(identityStatus)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_getIdentityVerificationStatus_Error() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.ERROR)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        viewModel.getIdentityVerificationStatus()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNull(viewModel.identityJob)
    }

    @ExperimentalCoroutinesApi
    @Ignore("Auth API change")
    @Test
    fun test_getLastIdentityVerification_Error() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.ERROR)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        val identity = viewModel.getLastIdentityVerification()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNull(identity)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_createIdentityVerification_Error() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.ERROR)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        val identity = viewModel.createIdentityVerification()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNull(identity)
    }
}