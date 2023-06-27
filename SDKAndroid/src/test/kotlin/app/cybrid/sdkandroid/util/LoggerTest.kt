package app.cybrid.sdkandroid.util

import android.util.Log
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.core.SDKConfig
import app.cybrid.sdkandroid.listener.CybridSDKEvents
import io.mockk.MockKAnnotations
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class LoggerTest {

    @Before
    fun setup() {}

    @After
    fun teardown() {
        Cybrid.reset()
    }

    @Test
    fun testLogger() {

        val logger = Logger
        Assert.assertNotNull(logger)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_EventCallback() = runTest {

        // -- Given
        val levelToLog = Log.ERROR
        val messageToLog = "Error."
        val logger = Logger
        val sdkConfig = SDKConfig(
            listener = object : CybridSDKEvents {
                override fun onTokenExpired() {}
                override fun onEvent(level: Int, message: String) {

                    Assert.assertEquals(level, levelToLog)
                    Assert.assertEquals(message, "Error: Error.")
                }
            }
        )

        // -- When
        Cybrid.setup(sdkConfig) {
            logger.log(LoggerEvents.ERROR, messageToLog)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_EventCallback_2() = runTest {

        // -- Given
        val levelToLog = Log.ERROR
        val logger = Logger
        val sdkConfig = SDKConfig(
            listener = object : CybridSDKEvents {
                override fun onTokenExpired() {}
                override fun onEvent(level: Int, message: String) {

                    Assert.assertEquals(level, levelToLog)
                    Assert.assertEquals(message, "__Error")
                }
            }
        )

        // -- When
        Cybrid.setup(sdkConfig) {
            logger.log(LoggerEvents.ERROR)
        }
    }
}