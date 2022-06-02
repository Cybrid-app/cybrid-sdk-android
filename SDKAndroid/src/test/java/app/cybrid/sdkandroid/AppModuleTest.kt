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
}