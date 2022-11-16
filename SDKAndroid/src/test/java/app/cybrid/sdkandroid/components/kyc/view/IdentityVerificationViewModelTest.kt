package app.cybrid.sdkandroid.components.kyc.view

import app.cybrid.cybrid_api_bank.client.infrastructure.ApiClient
import app.cybrid.sdkandroid.tools.JSONMock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class IdentityVerificationViewModelTest {

    private val dispatcher = TestCoroutineDispatcher()

    private fun prepareClient(): ApiClient {

        val interceptor = JSONMock()
        val clientBuilder = OkHttpClient()
            .newBuilder().addInterceptor(interceptor)
        return ApiClient(okHttpClientBuilder = clientBuilder)
    }

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun test_init() = runBlocking {

        // -- Given
        val viewModel = IdentityVerificationViewModel()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(viewModel.customerGuid)
        Assert.assertNull(viewModel.UIState)
        Assert.assertNull(viewModel.latestIdentityVerification)
    }

    @Test
    fun test_init_withDataProvider() = runBlocking {

        // -- Given
        val dataProvider = prepareClient()
        val viewModel = IdentityVerificationViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(viewModel.customerGuid)
        Assert.assertNull(viewModel.UIState)
        Assert.assertNull(viewModel.latestIdentityVerification)
    }

    @Test
    fun test_createCustomerTest() = runBlocking {

        // -- Given
        val dataProvider = prepareClient()
        val viewModel = IdentityVerificationViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        viewModel.createCustomerTest()

        // -- Then
        Assert.assertTrue(true)
    }
}