package app.cybrid.sdkandroid.components.kyc.view

import androidx.compose.runtime.mutableStateOf
import app.cybrid.cybrid_api_bank.client.infrastructure.ApiClient
import app.cybrid.sdkandroid.components.KYCView
import app.cybrid.sdkandroid.tools.JSONMock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class IdentityVerificationViewModelTestError {

    /*private val dispatcher = TestCoroutineDispatcher()

    private fun prepareClient(state: JSONMock.JSONMockState): ApiClient {

        val interceptor = JSONMock(state)
        val clientBuilder = OkHttpClient()
            .newBuilder().addInterceptor(interceptor)
        return ApiClient(okHttpClientBuilder = clientBuilder)
    }

    private fun createViewModel(): IdentityVerificationViewModel {

        val viewModel = IdentityVerificationViewModel()
        viewModel.UIState = mutableStateOf(KYCView.KYCViewState.LOADING)
        return viewModel
    }

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_createCustomerTest_Error() = runBlocking {

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
        Assert.assertEquals(viewModel.UIState?.value, KYCView.KYCViewState.LOADING)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_getIdentityVerificationStatus_Error() = runBlocking {

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
    @Test
    fun test_getLastIdentityVerification_Error() = runBlocking {

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
    fun test_createIdentityVerification_Error() = runBlocking {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.ERROR)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        val identity = viewModel.createIdentityVerification()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNull(identity)
    }*/
}