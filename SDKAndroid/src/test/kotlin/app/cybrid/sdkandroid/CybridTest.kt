package app.cybrid.sdkandroid

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cybrid.cybrid_api_bank.client.models.BankBankModel
import app.cybrid.cybrid_api_bank.client.models.CustomerBankModel
import app.cybrid.sdkandroid.core.CybridEnvironment
import app.cybrid.sdkandroid.core.SDKConfig
import app.cybrid.sdkandroid.listener.CybridSDKEvents
import app.cybrid.sdkandroid.mock.AssetsApiMock
import app.cybrid.sdkandroid.tools.MainDispatcherRule
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.spyk
import io.mockk.unmockkAll
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
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

    @Before
    fun setup() {

        MockKAnnotations.init(this, relaxed = true)
    }

    @After
    fun teardown() {

        Cybrid.resetInstance()
        unmockkAll()
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
        Assert.assertTrue(cybrid.invalidToken)
        Assert.assertEquals(cybrid.imagesUrl, "https://images.cybrid.xyz/sdk/assets/png/color/")
        Assert.assertEquals(cybrid.imagesSize, "@2x.png")
        Assert.assertEquals(cybrid.accountsRefreshObservable.value, false)
        Assert.assertNull(cybrid.customer)
        Assert.assertNull(cybrid.bank)
        Assert.assertTrue(cybrid.assets.isEmpty())
    }

    @Test
    fun test_setup() {

        // -- Given
        val customer = getCustomer()
        val bank = getBank()
        val listener = getListener()
        val sdkConfig = getSDKConfig(customer, bank, listener)
        val cybrid = Cybrid.getInstance()

        // -- When
        Assert.assertEquals(cybrid.bearer, "")
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
    }

    @Test
    fun test_setup_multi() {

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

        // -- When
        val customer2 = CustomerBankModel(guid = "000999")
        val sdkConfig2 = getSDKConfig(customer2, bank, listener)
        cybrid.setup(sdkConfig2) {}

        Assert.assertNotEquals(cybrid.customer?.guid, "000999")
    }

    @Test
    fun test_setup_Bearer_Empty() {

        // -- Given
        val customer = getCustomer()
        val bank = getBank()
        val listener = getListener()
        val sdkConfig = SDKConfig(
            environment = CybridEnvironment.STAGING,
            bearer = "",
            customerGuid = "1234",
            customer = customer,
            bank = bank,
            logTag = "test-tag",
            listener = listener
        )
        val cybrid = Cybrid.getInstance()

        // -- When
        Assert.assertEquals(cybrid.bearer, "")
        cybrid.setup(sdkConfig) {}

        // -- Then
        Assert.assertTrue(cybrid.configured)
        Assert.assertEquals(cybrid.bearer, "")
        Assert.assertEquals(cybrid.logTag, "test-tag")
        Assert.assertEquals(cybrid.customerGuid, "1234")
        Assert.assertEquals(cybrid.environment, CybridEnvironment.STAGING)
        Assert.assertEquals(cybrid.listener, listener)
        Assert.assertTrue(cybrid.invalidToken)
        Assert.assertEquals(cybrid.imagesUrl, "https://images.cybrid.xyz/sdk/assets/png/color/")
        Assert.assertEquals(cybrid.imagesSize, "@2x.png")
        Assert.assertEquals(cybrid.accountsRefreshObservable.value, false)
        Assert.assertEquals(cybrid.customer, customer)
        Assert.assertEquals(cybrid.bank, bank)
        Assert.assertTrue(cybrid.assets.isEmpty())
    }

    @Test
    fun test_getInstance() {

        Assert.assertNotNull(Cybrid.getInstance())
        Cybrid.resetInstance()
        Assert.assertNotNull(Cybrid.getInstance())
    }

    @Test
    fun test_resetInstance() {

        val cybridInstance1 = Cybrid.getInstance()
        Cybrid.resetInstance()
        val cybridInstance2 = Cybrid.getInstance()

        Assert.assertNotEquals(cybridInstance1, cybridInstance2)
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
        Assert.assertTrue(cybrid.invalidToken)
        cybrid.setup(sdkConfig) {}
        val oldBearer = cybrid.bearer
        cybrid.setBearer("new-bearer")

        // -- Then
        Assert.assertFalse(cybrid.invalidToken)
        Assert.assertEquals(cybrid.bearer, "new-bearer")
        Assert.assertNotEquals(cybrid.bearer, oldBearer)

        // -- Empty
        cybrid.setBearer("")
        Assert.assertTrue(cybrid.invalidToken)
        cybrid.setBearer("new-bearer")
        Assert.assertFalse(cybrid.invalidToken)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_autoLoad() = runTest {

        // -- Given
        var testNumber = 1
        val customer = getCustomer()
        val bank = getBank()
        val listener = getListener()
        val sdkConfig = getSDKConfig(customer, bank, listener)
        val completionLatch = CompletableDeferred<Unit>()
        val completion: () -> Unit = {
            completionLatch.complete(Unit)
            testNumber++
        }

        val cybrid = spyk(Cybrid.getInstance())
        val mockAssetsApi = AssetsApiMock.mockListAssets()

        // -- When
        cybrid.setup(sdkConfig, completion)
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
        val cybrid = spyk(Cybrid.getInstance())
        val mockAssetsApi = AssetsApiMock.mockListAssets()

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

    @Test
    fun test_fetchAssets_Data_Null() = runBlocking {

        // -- Given
        val cybrid = spyk(Cybrid.getInstance())
        val mockAssetsApi = AssetsApiMock.mockListAssets(AssetsApiMock.getAssetsListBankModelMock_DataNull())

        // -- When
        val completionLatch = CompletableDeferred<Unit>()
        cybrid.fetchAssets { completionLatch.complete(Unit) }
        completionLatch.await()

        // -- Then
        coVerify { mockAssetsApi.listAssets(BigDecimal(0), BigDecimal(50)) }
        Assert.assertNotNull(cybrid)
        Assert.assertTrue(cybrid.assets.isEmpty())
    }

    @Test
    fun testListener() = runBlocking {

        // -- Given
        var tokenExpiredCall = false
        var eventLevel = -98765
        var eventMessage = "no_message_here"

        val completionLatch = CompletableDeferred<Unit>()
        val sdkConfig = SDKConfig(
            listener = object : CybridSDKEvents {
                override fun onTokenExpired() {
                    tokenExpiredCall = true
                }
                override fun onEvent(level: Int, message: String) {
                    eventLevel = level
                    eventMessage = message
                }
            }
        )
        val cybrid = spyk(Cybrid.getInstance())
        AssetsApiMock.mockListAssets()

        // -- When
        Assert.assertFalse(tokenExpiredCall)
        Assert.assertEquals(eventLevel, -98765)
        Assert.assertEquals(eventMessage, "no_message_here")

        cybrid.setup(sdkConfig) { completionLatch.complete(Unit) }
        completionLatch.await()

        cybrid.listener?.onTokenExpired()
        cybrid.listener?.onEvent(1, "Success")

        // -- Then
        Assert.assertNotNull(cybrid.listener)
        Assert.assertEquals(eventLevel, 1)
        Assert.assertEquals(eventMessage, "Success")
    }
}