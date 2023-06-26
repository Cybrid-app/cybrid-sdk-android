package app.cybrid.sdkandroid

import android.view.LayoutInflater
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cybrid.cybrid_api_bank.client.apis.AssetsApi
import app.cybrid.cybrid_api_bank.client.infrastructure.ApiClient
import app.cybrid.cybrid_api_bank.client.models.BankBankModel
import app.cybrid.cybrid_api_bank.client.models.CustomerBankModel
import app.cybrid.sdkandroid.core.CybridEnvironment
import app.cybrid.sdkandroid.core.SDKConfig
import app.cybrid.sdkandroid.listener.CybridSDKEvents
import app.cybrid.sdkandroid.mocks.Mocks
import app.cybrid.sdkandroid.tools.MainDispatcherRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import java.time.OffsetDateTime

class CybridTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    //@MockK
    //private lateinit var appModule: AppModule

    //@MockK
    //private lateinit var apiClient: ApiClient

    //@MockK
    //private lateinit var assetsApi: AssetsApi

    @Before
    fun setup() {

        MockKAnnotations.init(this, relaxed = true)
        // every { AppModule } returns appModule
        // every { AppModule.getClient() } returns mockk()
    }

    @After
    fun teardown() {
        Cybrid.resetInstance()
    }

    private fun getCustomer(): CustomerBankModel {
        return CustomerBankModel(bankGuid = "12345")
    }

    private fun getBank(): BankBankModel {
        return BankBankModel(
            guid = "34567890",
            organizationGuid = "133",
            name = "Test",
            type = BankBankModel.Type.sandbox,
            features = listOf(),
            createdAt = OffsetDateTime.now()
        )
    }

    private fun getListener(): CybridSDKEvents {
        return object : CybridSDKEvents {
            override fun onTokenExpired() {}
            override fun onEvent(level: Int, message: String) {}
        }
    }

    private fun getSDKConfig(customer: CustomerBankModel,
                                  bank: BankBankModel,
                                  listener: CybridSDKEvents): SDKConfig {

        return SDKConfig(
            environment = CybridEnvironment.STAGING,
            bearer = "test-bearer",
            customerGuid = "1234",
            customer = customer,
            bank = bank,
            logTag = "test-tag",
            listener = listener
        )
    }

    @Test
    fun test_init() {

        // -- Given
        val cybrid = Cybrid.getInstance()

        // -- Then
        Assert.assertFalse(cybrid.configured)
        Assert.assertEquals(cybrid.bearer, "")
        Assert.assertEquals(cybrid.logTag, "CybridSDK")
        Assert.assertEquals(cybrid.customerGuid, "")
        Assert.assertEquals(cybrid.environment, CybridEnvironment.SANDBOX)
        Assert.assertNull(cybrid.listener)
        Assert.assertFalse(cybrid.invalidToken)
        Assert.assertEquals(cybrid.imagesUrl, "https://images.cybrid.xyz/sdk/assets/png/color/")
        Assert.assertEquals(cybrid.imagesSize, "@2x.png")
        Assert.assertEquals(cybrid.accountsRefreshObservable.value, false)
        Assert.assertNull(cybrid.customer)
        Assert.assertNull(cybrid.bank)
        Assert.assertTrue(cybrid.assets.isEmpty())
        Assert.assertFalse(cybrid.autoLoadComplete)
        Assert.assertNull(cybrid.completion)
    }

    @Test
    fun test_constructor() {

        // -- Given
        val customer = getCustomer()
        val bank = getBank()
        val listener = getListener()
        val sdkConfig = getSDKConfig(customer, bank, listener)
        val cybrid = Cybrid.getInstance()

        // -- When
        cybrid.setup(sdkConfig) {}

        // -- Then
        Assert.assertTrue(cybrid.configured)
        Assert.assertEquals(cybrid.bearer, "test-bearer")
        Assert.assertEquals(cybrid.logTag, "test-tag")
        Assert.assertEquals(cybrid.customerGuid, "1234")
        Assert.assertEquals(cybrid.environment, CybridEnvironment.STAGING)
        Assert.assertEquals(cybrid.listener, listener)
        Assert.assertFalse(cybrid.invalidToken)
        Assert.assertEquals(cybrid.imagesUrl, "https://images.cybrid.xyz/sdk/assets/png/color/")
        Assert.assertEquals(cybrid.imagesSize, "@2x.png")
        Assert.assertEquals(cybrid.accountsRefreshObservable.value, false)
        Assert.assertEquals(cybrid.customer, customer)
        Assert.assertEquals(cybrid.bank, bank)
        Assert.assertTrue(cybrid.assets.isEmpty())
        Assert.assertFalse(cybrid.autoLoadComplete)
        Assert.assertNotNull(cybrid.completion)
    }

    @Test
    fun test_getInstance() {

        Assert.assertNotNull(Cybrid.getInstance())
        Assert.assertNotNull(Cybrid.getInstance().assetsApi)

        Cybrid.resetInstance()
        Assert.assertNotNull(Cybrid.getInstance())
        Assert.assertNotNull(Cybrid.getInstance().assetsApi)
    }

    @Test
    fun test_getOKHttpClient() {

        // -- Given
        val cybrid = Cybrid.getInstance()
        val client = cybrid.getOKHttpClient()

        // -- Then
        val interceptorsNames = listOf("HttpBearerAuth", "HttpLoggingInterceptor")

        // -- Client works
        Assert.assertNotNull(client)

        // -- Client Interceptors works
        val interceptors = client.interceptors()
        for (interceptor in interceptors) {

            interceptorsNames.contains(interceptor.javaClass.simpleName).let {
                Assert.assertTrue(it)
            }
        }
    }

    @Test
    fun test_setBearer() {

        // -- Given
        val customer = getCustomer()
        val bank = getBank()
        val listener = getListener()
        val sdkConfig = getSDKConfig(customer, bank, listener)
        val cybrid = Cybrid.getInstance()


        // -- When
        cybrid.setup(sdkConfig) {}
        val oldBearer = cybrid.bearer
        cybrid.setBearer("new-bearer")

        // -- Then
        Assert.assertFalse(cybrid.invalidToken)
        Assert.assertEquals(cybrid.bearer, "new-bearer")
        Assert.assertNotEquals(cybrid.bearer, oldBearer)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_autoLoad() = runTest {

        // -- Given
        val customer = getCustomer()
        val bank = getBank()
        val listener = getListener()
        val sdkConfig = getSDKConfig(customer, bank, listener)
        val cybrid = Cybrid.getInstance()
        val mockAssetsApi = mockk<AssetsApi>()
        var testNumber = 1

        coEvery { mockAssetsApi.listAssets(page = any(), perPage = any()) } returns Mocks.getAssetsListBankModelMock()
        cybrid.assetsApi = mockAssetsApi

        // -- When
        val completionLatch = CompletableDeferred<Unit>()
        cybrid.setup(sdkConfig) {
            completionLatch.complete(Unit)
            testNumber++
        }
        completionLatch.await()

        // -- Then
        coVerify { mockAssetsApi.listAssets(BigDecimal(0), BigDecimal(50)) }
        Assert.assertNotNull(cybrid)
        Assert.assertFalse(cybrid.assets.isEmpty())
        Assert.assertEquals(cybrid.assets.count(), 6)
        Assert.assertEquals(testNumber, 2)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_fetchAssets() = runTest {

        // -- Given
        val cybrid = Cybrid.getInstance()
        val mockAssetsApi = mockk<AssetsApi>()

        coEvery { mockAssetsApi.listAssets(page = any(), perPage = any()) } returns Mocks.getAssetsListBankModelMock()
        cybrid.assetsApi = mockAssetsApi

        // -- When
        val completionLatch = CompletableDeferred<Unit>()
        cybrid.fetchAssets { completionLatch.complete(Unit) }
        completionLatch.await()

        // -- Then
        coVerify { mockAssetsApi.listAssets(BigDecimal(0), BigDecimal(50)) }
        Assert.assertNotNull(cybrid)
        Assert.assertFalse(cybrid.assets.isEmpty())
        Assert.assertEquals(cybrid.assets.count(), 6)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_fetchAssets_Data_Null() = runTest {

        // -- Given
        val cybrid = Cybrid.getInstance()
        val mockAssetsApi = mockk<AssetsApi>()

        coEvery { mockAssetsApi.listAssets(page = any(), perPage = any()) } returns Mocks.getAssetsListBankModelMock_DataNull()
        cybrid.assetsApi = mockAssetsApi

        // -- When
        val completionLatch = CompletableDeferred<Unit>()
        cybrid.fetchAssets { completionLatch.complete(Unit) }
        completionLatch.await()

        // -- Then
        coVerify { mockAssetsApi.listAssets(BigDecimal(0), BigDecimal(50)) }
        Assert.assertNotNull(cybrid)
        Assert.assertTrue(cybrid.assets.isEmpty())
    }

    /*@Test
    fun testSetBearer() {

        // -- Given
        val tokenExpiredPrev = cybrid.invalidToken

        // -- When
        cybrid.setBearer("token")
        cybrid.invalidToken = true

        // -- Then
        Assert.assertFalse(tokenExpiredPrev)
        Assert.assertTrue(cybrid.invalidToken)
    }*/

    /*@Test
    fun testListener() {

        // -- Given
        val listenerPrev = cybrid.listener

        // -- When
        cybrid.listener = object : CybridSDKEvents {
            override fun onTokenExpired() {}
            override fun onEvent(level: Int, message: String) {}
        }

        // -- Then
        Assert.assertNull(listenerPrev)
        Assert.assertNotNull(cybrid.listener)
    }*/
}