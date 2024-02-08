package app.cybrid.sdkandroid.core

import app.cybrid.cybrid_api_bank.client.models.BankBankModel
import app.cybrid.cybrid_api_bank.client.models.CustomerBankModel
import app.cybrid.sdkandroid.listener.CybridSDKEvents
import org.junit.Assert
import org.junit.Test
import java.time.OffsetDateTime

class SDKConfigTest {

    @Test
    fun test_init_without_params() {

        // -- Given
        val sdkConfig = SDKConfig()

        // -- Then
        Assert.assertEquals(sdkConfig.environment, CybridEnvironment.SANDBOX)
        Assert.assertEquals(sdkConfig.bearer, "")
        Assert.assertEquals(sdkConfig.customerGuid, "")
        Assert.assertNull(sdkConfig.customer)
        Assert.assertNull(sdkConfig.bank)
        Assert.assertEquals(sdkConfig.logTag, "CybridSDK")
        Assert.assertNull(sdkConfig.listener)
    }

    @Test
    fun test_init() {

        // -- Given
        val bank = BankBankModel(
            guid = "34567890",
            organizationGuid = "133",
            name = "Test",
            type = "sandbox",
            features = listOf(),
            createdAt = OffsetDateTime.now()
        )
        val listener = object : CybridSDKEvents {
            override fun onTokenExpired() {}
            override fun onEvent(level: Int, message: String) {}
        }
        val sdkConfig = SDKConfig(
            environment = CybridEnvironment.STAGING,
            bearer = "TEST-BEARER",
            customerGuid = "123456789",
            customer = CustomerBankModel(guid = "987654321"),
            bank = bank,
            logTag = "cybrid-log-tag",
            listener = listener
        )

        // -- Then
        Assert.assertEquals(sdkConfig.environment, CybridEnvironment.STAGING)
        Assert.assertEquals(sdkConfig.bearer, "TEST-BEARER")
        Assert.assertEquals(sdkConfig.customerGuid, "123456789")
        Assert.assertNotNull(sdkConfig.customer)
        Assert.assertEquals(sdkConfig.customer?.guid, "987654321")
        Assert.assertNotNull(sdkConfig.bank)
        Assert.assertEquals(sdkConfig.bank?.guid, "34567890")
        Assert.assertEquals(sdkConfig.bank?.organizationGuid, "133")
        Assert.assertEquals(sdkConfig.bank?.name, "Test")
        Assert.assertEquals(sdkConfig.bank?.type, "sandbox")
        Assert.assertTrue(sdkConfig.bank?.features?.isEmpty() ?: false)
        Assert.assertEquals(sdkConfig.logTag, "cybrid-log-tag")
    }
}