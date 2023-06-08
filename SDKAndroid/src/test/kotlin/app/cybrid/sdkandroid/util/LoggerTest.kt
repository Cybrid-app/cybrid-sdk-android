package app.cybrid.sdkandroid.util

import android.util.Log
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.listener.CybridSDKEvents
import org.junit.Assert
import org.junit.Test

class LoggerTest {

    @Test
    fun testLogger() {

        val logger = Logger
        Assert.assertNotNull(logger)
    }

    @Test
    fun test_EventCallback() {

        // -- Given
        val levelToLog = Log.ERROR
        val messageToLog = "Error."
        val logger = Logger
        Cybrid.instance.listener = object : CybridSDKEvents {
            override fun onTokenExpired() {}
            override fun onEvent(level: Int, message: String) {

                Assert.assertEquals(level, levelToLog)
                Assert.assertEquals(message, "Error: Error.")
            }
        }

        // -- When
        logger.log(LoggerEvents.ERROR, messageToLog)
    }

    @Test
    fun test_EventCallback_2() {

        // -- Given
        val levelToLog = Log.ERROR
        val logger = Logger
        Cybrid.instance.listener = object : CybridSDKEvents {
            override fun onTokenExpired() {}
            override fun onEvent(level: Int, message: String) {

                Assert.assertEquals(level, levelToLog)
                Assert.assertEquals(message, "Error")
            }
        }

        // -- When
        logger.log(LoggerEvents.ERROR)
    }
}