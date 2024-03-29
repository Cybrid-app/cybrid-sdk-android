package app.cybrid.sdkandroid.core

import app.cybrid.sdkandroid.tools.TestConstants
import java.math.BigDecimal as JavaBigDecimal
import org.junit.Assert
import org.junit.Test

class BigDecimalPipeTest {

    @Test
    fun initTest() {

        // -- Given
        val bigDecimalPipe = BigDecimalPipe

        // -- When

        // -- Then
        Assert.assertNotNull(bigDecimalPipe)
    }

    @Test
    fun companionTest() {

        // -- Given
        val bigDecimalCompanion = BigDecimalPipe

        // -- Then
        Assert.assertNotNull(bigDecimalCompanion)
    }

    @Test
    fun transformTest() {

        // -- Given
        val zeroBigDecimal = BigDecimal(0)
        val oneBigDecimal = BigDecimal(1)
        val zeroJavaBigDecimal = JavaBigDecimal.ZERO
        val oneJavaBigDecimal = JavaBigDecimal.ONE

        // -- When
        val transformBTC1 = BigDecimalPipe.transform(0, TestConstants.BTC_ASSET)
        val transformETH1 = BigDecimalPipe.transform(0, TestConstants.ETH_ASSET)
        val transformCAD1 = BigDecimalPipe.transform(0, TestConstants.CAD_ASSET)

        val transformBTC2 = BigDecimalPipe.transform(1, TestConstants.BTC_ASSET)
        val transformETH2 = BigDecimalPipe.transform(1, TestConstants.ETH_ASSET)
        val transformCAD2 = BigDecimalPipe.transform(1, TestConstants.CAD_ASSET)

        val transformBTCStr1 = BigDecimalPipe.transform("0", TestConstants.BTC_ASSET)
        val transformETHStr1 = BigDecimalPipe.transform("0", TestConstants.ETH_ASSET)
        val transformCADStr1 = BigDecimalPipe.transform("0", TestConstants.CAD_ASSET)

        val transformBTCStr2 = BigDecimalPipe.transform("1", TestConstants.BTC_ASSET)
        val transformETHStr2 = BigDecimalPipe.transform("1", TestConstants.ETH_ASSET)
        val transformCADStr2 = BigDecimalPipe.transform("1", TestConstants.CAD_ASSET)

        val transformBTCBD1 = BigDecimalPipe.transform(zeroBigDecimal, TestConstants.BTC_ASSET)
        val transformETHBD1 = BigDecimalPipe.transform(zeroBigDecimal, TestConstants.ETH_ASSET)
        val transformCADBD1 = BigDecimalPipe.transform(zeroBigDecimal, TestConstants.CAD_ASSET)

        val transformBTCBD2 = BigDecimalPipe.transform(oneBigDecimal, TestConstants.BTC_ASSET)
        val transformETHBD2 = BigDecimalPipe.transform(oneBigDecimal, TestConstants.ETH_ASSET)
        val transformCADBD2 = BigDecimalPipe.transform(oneBigDecimal, TestConstants.CAD_ASSET)

        val transformBTCJBD1 = BigDecimalPipe.transform(zeroJavaBigDecimal, TestConstants.BTC_ASSET)
        val transformETHJBD1 = BigDecimalPipe.transform(zeroJavaBigDecimal, TestConstants.ETH_ASSET)
        val transformCADJBD1 = BigDecimalPipe.transform(zeroJavaBigDecimal, TestConstants.CAD_ASSET)

        val transformBTCJBD2 = BigDecimalPipe.transform(oneJavaBigDecimal, TestConstants.BTC_ASSET)
        val transformETHJBD2 = BigDecimalPipe.transform(oneJavaBigDecimal, TestConstants.ETH_ASSET)
        val transformCADJBD2 = BigDecimalPipe.transform(oneJavaBigDecimal, TestConstants.CAD_ASSET)

        // -- Then
        Assert.assertEquals(transformBTC1, "₿0.00000000")
        Assert.assertEquals(transformETH1, "Ξ0.000000000000000000")
        Assert.assertEquals(transformCAD1, "$0.00")

        Assert.assertEquals(transformBTC2, "₿0.00000001")
        Assert.assertEquals(transformETH2, "Ξ0.000000000000000001")
        Assert.assertEquals(transformCAD2, "$0.01")

        Assert.assertEquals(transformBTCStr1, "₿0.00000000")
        Assert.assertEquals(transformETHStr1, "Ξ0.000000000000000000")
        Assert.assertEquals(transformCADStr1, "$0.00")

        Assert.assertEquals(transformBTCStr2, "₿0.00000001")
        Assert.assertEquals(transformETHStr2, "Ξ0.000000000000000001")
        Assert.assertEquals(transformCADStr2, "$0.01")

        Assert.assertEquals(transformBTCBD1, "₿0.00000000")
        Assert.assertEquals(transformETHBD1, "Ξ0.000000000000000000")
        Assert.assertEquals(transformCADBD1, "$0.00")

        Assert.assertEquals(transformBTCBD2, "₿0.00000001")
        Assert.assertEquals(transformETHBD2, "Ξ0.000000000000000001")
        Assert.assertEquals(transformCADBD2, "$0.01")

        Assert.assertEquals(transformBTCJBD1, "₿0.00000000")
        Assert.assertEquals(transformETHJBD1, "Ξ0.000000000000000000")
        Assert.assertEquals(transformCADJBD1, "$0.00")

        Assert.assertEquals(transformBTCJBD2, "₿0.00000001")
        Assert.assertEquals(transformETHJBD2, "Ξ0.000000000000000001")
        Assert.assertEquals(transformCADJBD2, "$0.01")
    }

    @Test
    fun transformEnormousNumbersTest() {

        // -- Given
        val bigPositiveNumber = "9007199254740991"
        val bigNegativeNumber = "-9007199254740991"

        // -- When
        val transformBTC1 = BigDecimalPipe.transform(bigPositiveNumber, TestConstants.BTC_ASSET)
        val transformCAD1 = BigDecimalPipe.transform(bigNegativeNumber, TestConstants.CAD_ASSET)

        // -- Then
        Assert.assertEquals(transformBTC1, "₿90,071,992.54740991")
        Assert.assertEquals(transformCAD1, "-$90,071,992,547,409.91")
    }

    @Test
    fun transformMaximumNumbersTest() {

        // -- Given
        val bigPositiveNumber = "123456789123456789123456789123"
        val shaNumber = "115792089237316195423570985008687907853269984665640564039457584007913129639935"

        // -- When
        val transformBTC = BigDecimalPipe.transform(bigPositiveNumber, TestConstants.BTC_ASSET)
        val transformETH = BigDecimalPipe.transform(bigPositiveNumber, TestConstants.ETH_ASSET)
        val transformCAD = BigDecimalPipe.transform(bigPositiveNumber, TestConstants.CAD_ASSET)

        val shaBTC = BigDecimalPipe.transform(shaNumber, TestConstants.BTC_ASSET)
        val shaETH = BigDecimalPipe.transform(shaNumber, TestConstants.ETH_ASSET)
        val shaCAD = BigDecimalPipe.transform(shaNumber, TestConstants.CAD_ASSET)

        // -- Then
        Assert.assertEquals(transformBTC, "₿1,234,567,891,234,567,891,234.56789123")
        Assert.assertEquals(transformETH, "Ξ123,456,789,123.456789123456789123")
        Assert.assertEquals(transformCAD, "$1,234,567,891,234,567,891,234,567,891.23")

        Assert.assertEquals(shaBTC, "₿1,157,920,892,373,161,954,235,709,850,086,879,078,532,699,846,656,405,640,394,575,840,079,131.29639935")
        Assert.assertEquals(shaETH, "Ξ115,792,089,237,316,195,423,570,985,008,687,907,853,269,984,665,640,564,039,457.584007913129639935")
        Assert.assertEquals(shaCAD, "$1,157,920,892,373,161,954,235,709,850,086,879,078,532,699,846,656,405,640,394,575,840,079,131,296,399.35")
    }

    @Test
    fun minimalDecimals() {

        // -- Given
        val value = 36010
        val value2 = BigDecimal("20128146.67")

        // -- When
        val transformCAD = BigDecimalPipe.transform(value, TestConstants.CAD_ASSET)
        val transformCAD2 = BigDecimalPipe.transform(value2, TestConstants.CAD_ASSET)

        // -- Then
        Assert.assertEquals(transformCAD, "$360.10")
        Assert.assertEquals(transformCAD2, "$201,281.46")
    }

    @Test
    fun test_transformAnyWithDot() {

        // -- Given
        val value = BigDecimal("1234.5")

        // -- When
        val transform = BigDecimalPipe.transformAny(value, TestConstants.USD_ASSET)

        // -- Then
        Assert.assertEquals(transform, "$1,234.50")
    }

    @Test
    fun test_transformAnyWithoutDot() {

        // -- Given
        val value = BigDecimal("1234")

        // -- When
        val transform = BigDecimalPipe.transformAny(value, TestConstants.USD_ASSET)

        // -- Then
        Assert.assertEquals(transform, "$1,234.00")
    }
}