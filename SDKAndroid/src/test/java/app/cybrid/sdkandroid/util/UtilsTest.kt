package app.cybrid.sdkandroid.util

import org.junit.Assert
import org.junit.Test

class UtilsTest {

    @Test
    fun isSuccessfulTest() {

        // -- Given
        val code0 = 199
        val code1 = 200
        val code2 = 299
        val code3 = 300
        val code4 = 301

        // -- When
        val result0 = isSuccessful(code0)
        val result1 = isSuccessful(code1)
        val result2 = isSuccessful(code2)
        val result3 = isSuccessful(code3)
        val result4 = isSuccessful(code4)

        // -- Then
        Assert.assertFalse(result0)
        Assert.assertTrue(result1)
        Assert.assertTrue(result2)
        Assert.assertFalse(result3)
        Assert.assertFalse(result4)
    }
}