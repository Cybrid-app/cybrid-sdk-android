package app.cybrid.sdkandroid

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
        Cybrid.instance.environment = CybridEnv.STAGING
        val stagingURL = AppModule.getApiUrl()
        assertTrue(stagingURL.contains("staging"))

        // -- Staging
        Cybrid.instance.environment = CybridEnv.SANDBOX
        val sandboxURL = AppModule.getApiUrl()
        assertTrue(sandboxURL.contains("sandbox"))

        // -- Production
        Cybrid.instance.environment = CybridEnv.PRODUCTION
        val productionURL = AppModule.getApiUrl()
        assertTrue(productionURL.contains("production"))
    }
}