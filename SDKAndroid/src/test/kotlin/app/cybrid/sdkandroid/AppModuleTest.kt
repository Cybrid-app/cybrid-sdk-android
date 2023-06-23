package app.cybrid.sdkandroid

import app.cybrid.sdkandroid.core.CybridEnvironment
import org.junit.Assert.*
import org.junit.Test

class AppModuleTest {

    @Test
    fun testAppModule() {

        val appModule = AppModule
        assertNotNull(appModule)
    }

    @Test
    fun testAppModuleClient() {

        // -- Given
        val appModule = AppModule
        val client = appModule.getClient()

        // -- When

        // -- Then
        // -- Client works
        assertNotNull(client)
    }

    @Test
    fun test_getApiUrl() {

        // -- Staging
        Cybrid.getInstance().environment = CybridEnvironment.STAGING
        val stagingURL = AppModule.getApiUrl()
        assertTrue(stagingURL.contains("staging"))

        // -- Staging
        Cybrid.getInstance().environment = CybridEnvironment.SANDBOX
        val sandboxURL = AppModule.getApiUrl()
        assertTrue(sandboxURL.contains("sandbox"))

        // -- Production
        Cybrid.getInstance().environment = CybridEnvironment.PRODUCTION
        val productionURL = AppModule.getApiUrl()
        assertTrue(productionURL.contains("production"))
    }
}