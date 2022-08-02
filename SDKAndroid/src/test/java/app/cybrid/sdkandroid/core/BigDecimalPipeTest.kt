package app.cybrid.sdkandroid.core

import app.cybrid.sdkandroid.tools.TestConstants
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

        // -- Then
        Assert.assertEquals(transformBTC1, "₿0.00")
        Assert.assertEquals(transformETH1, "Ξ0.00")
        Assert.assertEquals(transformCAD1, "$0.00")

        Assert.assertEquals(transformBTC2, "₿0.00000001")
        Assert.assertEquals(transformETH2, "Ξ0.000000000000000001")
        Assert.assertEquals(transformCAD2, "$0.01")

        Assert.assertEquals(transformBTCStr1, "₿0.00")
        Assert.assertEquals(transformETHStr1, "Ξ0.00")
        Assert.assertEquals(transformCADStr1, "$0.00")

        Assert.assertEquals(transformBTCStr2, "₿0.00000001")
        Assert.assertEquals(transformETHStr2, "Ξ0.000000000000000001")
        Assert.assertEquals(transformCADStr2, "$0.01")

        Assert.assertEquals(transformBTCBD1, "₿0.00")
        Assert.assertEquals(transformETHBD1, "Ξ0.00")
        Assert.assertEquals(transformCADBD1, "$0.00")

        Assert.assertEquals(transformBTCBD2, "₿0.00000001")
        Assert.assertEquals(transformETHBD2, "Ξ0.000000000000000001")
        Assert.assertEquals(transformCADBD2, "$0.01")
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

        // -- When
        val transformCAD = BigDecimalPipe.transform(value, TestConstants.CAD_ASSET)

        // -- Then
        Assert.assertEquals(transformCAD, "$360.10")
    }
}