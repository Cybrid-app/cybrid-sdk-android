package app.cybrid.sdkandroid.util

import app.cybrid.cybrid_api_bank.client.apis.PricesApi
import app.cybrid.cybrid_api_bank.client.infrastructure.ApiClient
import app.cybrid.sdkandroid.AppModule
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.tools.TestConstants
import app.cybrid.sdkandroid.tools.ErrorMockInterceptor
import app.cybrid.sdkandroid.tools.TestEmptyService
import app.cybrid.sdkandroid.tools.MockInterceptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Test
import org.junit.Assert
import org.junit.Before
import java.net.HttpURLConnection

class DataAccessStrategyTest {

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

    private fun prepareClient(code:Int, error:Boolean = false): ApiClient {

        val interceptor = if (error) ErrorMockInterceptor(code) else MockInterceptor(code)
        val clientBuilder = OkHttpClient()
            .newBuilder().addInterceptor(interceptor)
        return ApiClient(okHttpClientBuilder = clientBuilder)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun get400ErrorServerTest() = runBlocking {

        Cybrid.instance.setBearer("Bearer")
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

        val expectedCode = HttpURLConnection.HTTP_UNAUTHORIZED
        Cybrid.instance.setBearer(TestConstants.expiredToken)

        val pricesService = AppModule.getClient().createService(PricesApi::class.java)
        val result = getResult { pricesService.listPrices() }

        Assert.assertNotNull(result)
        Assert.assertEquals(result.code, expectedCode)
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
        Cybrid.instance.setBearer(TestConstants.expiredToken)
        Cybrid.instance.listener = null

        val pricesService = AppModule.getClient().createService(PricesApi::class.java)
        val result = getResult { pricesService.listPrices() }

        Assert.assertNotNull(result)
        Assert.assertEquals(result.code, expectedCode)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun callNullTest() = runBlocking {

        Cybrid.instance.setBearer(TestConstants.expiredToken)
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
}