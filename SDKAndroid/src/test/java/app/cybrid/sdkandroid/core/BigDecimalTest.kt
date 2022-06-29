package app.cybrid.sdkandroid.core

import org.junit.Assert
import org.junit.Test
import java.text.NumberFormat
import java.util.*
import java.math.BigDecimal as JavaBigDecimal

class BigDecimalTest {

    @Test
    fun newIntBigDecimalTest() {

        val intNumber = 1
        val expected = JavaBigDecimal(intNumber)
        val bigDecimal = BigDecimal(intNumber)
        Assert.assertEquals(bigDecimal.value, expected)
    }

    @Test
    fun newLongBigDecimalTest() {

        val longNumber = 10L
        val expected = JavaBigDecimal(longNumber)
        val bigDecimal = BigDecimal(longNumber);
        Assert.assertEquals(bigDecimal.value, expected)
    }

    @Test
    fun newDoubleBigDecimalTest() {

        val doubleNumber = 100.toDouble()
        val expected = JavaBigDecimal(doubleNumber)
        val bigDecimal = BigDecimal(doubleNumber)
        Assert.assertEquals(bigDecimal.value, expected)
    }

    @Test
    fun newStringBigDecimalTest() {

        val stringNumber = "1000"
        val expected = JavaBigDecimal(stringNumber)
        val bigDecimal = BigDecimal(stringNumber);
        Assert.assertEquals(bigDecimal.value, expected)
    }

    @Test
    fun plusTest() {

        // -- Given
        val bigDecimal1 = BigDecimal(1)
        val bigDecimal2 = BigDecimal(2)
        val expected = BigDecimal(3)

        // -- When
        val plusValue = bigDecimal1.plus(bigDecimal2)

        // -- Then
        Assert.assertEquals(plusValue, expected)
    }

    @Test
    fun minusTest() {

        // -- Given
        val bigDecimal1 = BigDecimal(1)
        val bigDecimal2 = BigDecimal(2)
        val expected = BigDecimal(1)

        // -- When
        val minusValue = bigDecimal2.minus(bigDecimal1)

        // -- Then
        Assert.assertEquals(minusValue, expected)
    }

    @Test
    fun minusNegativeTest() {

        // -- Given
        val bigDecimal1 = BigDecimal(1)
        val bigDecimal2 = BigDecimal(2)
        val expected = BigDecimal(-1)

        // -- When
        val minusValue = bigDecimal1.minus(bigDecimal2)

        // -- Then
        Assert.assertEquals(minusValue, expected)
    }

    @Test
    fun timesTest() {

        // -- Given
        val bigDecimal1 = BigDecimal(1)
        val bigDecimal2 = BigDecimal(2)
        val expected = BigDecimal(2)

        // -- When
        val minusValue = bigDecimal1.times(bigDecimal2)

        // -- Then
        Assert.assertEquals(minusValue, expected)
    }

    @Test
    fun timesNegativeTest() {

        // -- Given
        val bigDecimal1 = BigDecimal(-1)
        val bigDecimal2 = BigDecimal(2)
        val expected = BigDecimal(-2)

        // -- When
        val minusValue = bigDecimal1.times(bigDecimal2)

        // -- Then
        Assert.assertEquals(minusValue, expected)
    }

    @Test
    fun divTest() {

        // -- Given
        val bigDecimal1 = BigDecimal(10)
        val bigDecimal2 = BigDecimal(2)
        val expected = BigDecimal(5)

        // -- When
        val minusValue = bigDecimal1.div(bigDecimal2)

        // -- Then
        Assert.assertEquals(minusValue, expected)
    }

    @Test
    fun divLTest() {

        // -- Given
        val bigDecimal1 = BigDecimal(10)
        val bigDecimal2 = BigDecimal(2)
        val expected = BigDecimal(5)

        // -- When
        val minusValue = bigDecimal1.divL(bigDecimal2)

        // -- Then
        Assert.assertEquals(minusValue, expected)
    }

    @Test
    fun powTest() {

        // -- Given
        val bigDecimal1 = BigDecimal(4)
        val expected = BigDecimal(16)

        // -- When
        val powValue = bigDecimal1.pow(2)

        // -- Then
        Assert.assertEquals(powValue, expected)
    }

    @Test
    fun powBigDecimalTest() {

        // -- Given
        val bigDecimal1 = BigDecimal(4)
        val bigDecimal2 = BigDecimal(2)
        val expected = BigDecimal(16)

        // -- When
        val powValue = bigDecimal1.pow(bigDecimal2)

        // -- Then
        Assert.assertEquals(powValue, expected)
    }

    @Test
    fun toDoubleTest() {

        // -- Given
        val bigDecimal1 = BigDecimal(5)
        val expected:Double = 5.0

        // -- When
        val value = bigDecimal1.toDouble()

        // -- Then
        Assert.assertEquals(value, expected, 0.0)
    }

    @Test
    fun toDoubleFailureTest() {

        // -- Given
        val bigDecimal1 = BigDecimal(1234567891234567891)

        // -- When
        val value = bigDecimal1.toDouble()

        // -- Then
        Assert.assertEquals(value, 1.23456789123456794E18, 0.0)
    }

    @Test
    fun toIntTest() {

        // -- Given
        val bigDecimal1 = BigDecimal(5)
        val expected:Int = 5

        // -- When
        val value = bigDecimal1.toInt()

        // -- Then
        Assert.assertEquals(expected, value)
    }

    @Test
    fun toIntFailureTest() {

        // -- Given
        val bigDecimal1 = BigDecimal(1234567891234567891)

        // -- When
        val value = bigDecimal1.toInt()

        // -- Then
        Assert.assertEquals(value, -1071401261)
    }

    @Test
    fun toJavaBigDecimalTest() {

        // -- Given
        val bigDecimal1 = BigDecimal(5)
        val expected:JavaBigDecimal = JavaBigDecimal(5)

        // -- When
        val value = bigDecimal1.toJavaBigDecimal()

        // -- Then
        Assert.assertEquals(expected, value)
    }

    @Test
    fun compareToEqualTest() {

        // -- Given
        val bigDecimal1 = BigDecimal(5)
        val expected = BigDecimal(5)

        // -- When
        val value = bigDecimal1.compareTo(expected)

        // -- Then
        Assert.assertEquals(value, 0)
    }

    @Test
    fun compareToGreaterTest() {

        // -- Given
        val bigDecimal1 = BigDecimal(6)
        val expected = BigDecimal(5)

        // -- When
        val value = bigDecimal1.compareTo(expected)

        // -- Then
        Assert.assertEquals(value, 1)
    }

    @Test
    fun compareToLessTest() {

        // -- Given
        val bigDecimal1 = BigDecimal(4)
        val expected = BigDecimal(5)

        // -- When
        val value = bigDecimal1.compareTo(expected)

        // -- Then
        Assert.assertEquals(value, -1)
    }

    @Test
    fun equalsBigDecimalTest() {

        // -- Given
        val bigDecimal1 = BigDecimal(5)
        val expected = BigDecimal(5)

        // -- When
        val value = bigDecimal1.equals(expected)

        // -- Then
        Assert.assertTrue(value)
    }

    @Test
    fun equalsFailBigDecimalTest() {

        // -- Given
        val bigDecimal1 = BigDecimal(5)
        val expected = BigDecimal(6)

        // -- When
        val value = bigDecimal1.equals(expected)

        // -- Then
        Assert.assertFalse(value)
    }

    @Test
    fun equalsJavaBigDecimalTest() {

        // -- Given
        val bigDecimal1 = BigDecimal(5)
        val expected = JavaBigDecimal(5)

        // -- When
        val value = bigDecimal1.equals(expected)

        // -- Then
        Assert.assertTrue(value)
    }

    @Test
    fun equalsFailJavaBigDecimalTest() {

        // -- Given
        val bigDecimal1 = BigDecimal(5)
        val expected = JavaBigDecimal(6)

        // -- When
        val value = bigDecimal1.equals(expected)

        // -- Then
        Assert.assertFalse(value)
    }

    @Test
    fun failEqualsTest() {

        // -- Given
        val bigDecimal1 = BigDecimal(5)
        val expected = 3

        // -- When
        val value = bigDecimal1.equals(expected)

        // -- Then
        Assert.assertFalse(value)
    }

    @Test
    fun toStringTest() {

        // -- Given
        val bigDecimal1 = BigDecimal(5)
        val expected = "5"

        // -- When
        val value = bigDecimal1.toString()

        // -- Then
        Assert.assertEquals(value, expected)
    }

    @Test
    fun zeroTest() {

        // -- Given
        val bigDecimal1 = BigDecimal.ZERO
        val expected = BigDecimal(0)

        // -- When
        val value = bigDecimal1.equals(expected)

        // -- Then
        Assert.assertTrue(value)
        Assert.assertEquals(bigDecimal1, expected)
    }

    @Test
    fun scaleTest() {

        // -- Given
        val bigDecimal1 = BigDecimal(10)

        // -- When
        val value = bigDecimal1.setScale(0)

        // -- Then
        Assert.assertEquals(value.value.scale(), 0)
    }
}