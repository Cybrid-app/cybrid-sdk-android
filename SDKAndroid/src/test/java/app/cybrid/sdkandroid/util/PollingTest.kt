package app.cybrid.sdkandroid.util

import io.mockk.MockKAnnotations
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PollingTest {

    private lateinit var classUnderTest: Polling

    @Before
    fun setup() {

        MockKAnnotations.init(this, relaxUnitFun = true)
        classUnderTest = Polling {}
    }

    @Test
    fun test_init() {

        Assert.assertNotNull(classUnderTest)
        Assert.assertNotNull(classUnderTest.handler)
        Assert.assertNotNull(classUnderTest.runnable)
        Assert.assertNotNull(classUnderTest.runner)
    }

    @Test
    fun test_init_runner() {

        // -- Given
        var num = 1
        val runner: () -> Unit = { num += 1 }
        val polling = Polling(runner = runner)

        // -- Then
        Assert.assertNotNull(polling)
        Assert.assertEquals(polling.runner, runner)

        polling.runnable?.run()
        Assert.assertEquals(num, 2)
    }

    @Test
    fun test_stop() {

        // -- When
        classUnderTest.stop()

        // -- Then
        Assert.assertNull(classUnderTest.runnable)
    }
}