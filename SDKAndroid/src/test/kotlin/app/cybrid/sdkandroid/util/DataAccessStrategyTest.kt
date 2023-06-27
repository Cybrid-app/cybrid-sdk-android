package app.cybrid.sdkandroid.util

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cybrid.cybrid_api_bank.client.apis.AssetsApi
import app.cybrid.cybrid_api_bank.client.apis.PricesApi
import app.cybrid.cybrid_api_bank.client.infrastructure.ApiClient
import app.cybrid.sdkandroid.AppModule
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.core.SDKConfig
import app.cybrid.sdkandroid.listener.CybridSDKEvents
import app.cybrid.sdkandroid.mocks.Mocks
import app.cybrid.sdkandroid.tools.*
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.OkHttpClient
import org.junit.*
import java.net.HttpURLConnection

class DataAccessStrategyTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private fun prepareClient(code:Int, error:Boolean = false): ApiClient {

        val interceptor = if (error) ErrorMockInterceptor(code) else MockInterceptor(code)
        val clientBuilder = OkHttpClient()
            .newBuilder().addInterceptor(interceptor)
        return ApiClient(okHttpClientBuilder = clientBuilder)
    }

    private fun prepareSDK(bearer: String, listener: CybridSDKEvents? = null): Cybrid {

        val mockAssetsApi = mockk<AssetsApi>()
        val cybrid = Cybrid.getInstance()
        val sdkConfig = SDKConfig(
            bearer = bearer,
            listener = listener
        )

        coEvery { mockAssetsApi.listAssets(page = any(), perPage = any()) } returns Mocks.getAssetsListBankModelMock()
        cybrid.assetsApi = mockAssetsApi

        cybrid.setup(sdkConfig) {}
        return cybrid
    }

    @Before
    fun setup() {}

    @After
    fun teardown() {
        Cybrid.resetInstance()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun get400ErrorServerTest() = runBlocking {

        Cybrid.getInstance().setBearer("Bearer")
        val pricesService = AppModule.getClient().createService(PricesApi::class.java)
        val result = getResult { pricesService.listPrices() }

        Assert.assertNotNull(result)
        Assert.assertEquals(result.code, 401)
        Assert.assertNull(result.data)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun get200SuccessServerTest() = runBlocking {

        val expectedCode = 200
        val apiClient = prepareClient(expectedCode)
        val pricesService = apiClient.createService(PricesApi::class.java)
        val result = getResult { pricesService.listPrices() }

        Assert.assertNotNull(result)
        Assert.assertEquals(result.code, expectedCode)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getUnauthorizedServerTest() = runBlocking {

        var onTokenExpiredCalled = false
        val expectedCode = HttpURLConnection.HTTP_UNAUTHORIZED
        val cybrid = prepareSDK(
            bearer = TestConstants.expiredToken,
            listener = object : CybridSDKEvents {

                override fun onTokenExpired() {
                    onTokenExpiredCalled = true
                }

                override fun onEvent(level: Int, message: String) {}
            }
        )

        val pricesService = AppModule.getClient().createService(PricesApi::class.java)
        val result = getResult { pricesService.listPrices() }

        Assert.assertNotNull(result)
        Assert.assertEquals(result.code, expectedCode)
        Assert.assertTrue(onTokenExpiredCalled)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getForbiddenServerTest() = runBlocking {

        val expectedCode = HttpURLConnection.HTTP_FORBIDDEN
        val apiClient = prepareClient(expectedCode, error = true)
        val pricesService = apiClient.createService(PricesApi::class.java)
        val result = getResult { pricesService.listPrices() }

        Assert.assertNotNull(result)
        Assert.assertEquals(result.code, expectedCode)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun unauthorizedAndNullListenerTest() = runBlocking {

        val expectedCode = HttpURLConnection.HTTP_UNAUTHORIZED
        Cybrid.getInstance().setBearer(TestConstants.expiredToken)
        val cybrid = prepareSDK(
            bearer = TestConstants.expiredToken,
            listener = null
        )

        val pricesService = AppModule.getClient().createService(PricesApi::class.java)
        val result = getResult { pricesService.listPrices() }

        Assert.assertNotNull(result)
        Assert.assertEquals(result.code, expectedCode)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun callNullTest() = runBlocking {

        Cybrid.getInstance().setBearer(TestConstants.expiredToken)
        val nullService = AppModule.getClient().createService(TestEmptyService::class.java)
        val result = getResult { nullService.getNothing() }

        Assert.assertNotNull(result)
        Assert.assertEquals(result.code, 500)
        Assert.assertNotNull(result.message)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_call_invoke() = runBlocking {

        val expectedCode = 200
        val apiClient = prepareClient(expectedCode)
        val pricesService = apiClient.createService(PricesApi::class.java)
        val result = getResult { pricesService.listPrices() }

        val result2 = pricesService.listPrices()
        val resourceToCheck = Resource.success(result2.body(), result2.code())

        Assert.assertNotNull(result)
        Assert.assertEquals(result.code, expectedCode)
        Assert.assertEquals(result, resourceToCheck)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_getResult_Error() = runBlocking {

        val expectedCode = HttpURLConnection.HTTP_INTERNAL_ERROR
        val apiClient = prepareClient(expectedCode)
        Cybrid.getInstance().setBearer(TestConstants.expiredToken)

        val pricesService = apiClient.createService(PricesApi::class.java)
        val result = getResult { pricesService.listPrices() }

        Assert.assertNotNull(result)
        Assert.assertEquals(result.code, expectedCode)
    }
}