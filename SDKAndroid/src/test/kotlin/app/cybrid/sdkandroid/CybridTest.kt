package app.cybrid.sdkandroid

import app.cybrid.sdkandroid.listener.CybridSDKEvents
import org.junit.Assert
import org.junit.Test

class CybridTest {

    private var cybrid = Cybrid.getInstance()

    @Test
    fun testCybridClass() {

        Assert.assertNotNull(cybrid)
    }

    @Test
    fun testOKHttpClient() {

        // -- Given
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
    fun testSetBearer() {

        // -- Given
        val tokenExpiredPrev = cybrid.invalidToken

        // -- When
        cybrid.setBearer("token")
        cybrid.invalidToken = true

        // -- Then
        Assert.assertFalse(tokenExpiredPrev)
        Assert.assertTrue(cybrid.invalidToken)
    }

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