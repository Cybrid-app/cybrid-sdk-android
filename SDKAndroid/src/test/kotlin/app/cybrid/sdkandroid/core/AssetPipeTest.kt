package app.cybrid.sdkandroid.core

import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.sdkandroid.tools.TestConstants
import io.mockk.MockKAnnotations
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AssetPipeTest {

    private lateinit var classUnderTest: AssetPipe

    @Before
    fun setUp() {

        MockKAnnotations.init(this, relaxUnitFun = true)
        classUnderTest = AssetPipe
    }

    @Test
    fun initTest() {

        // -- Given
        val assetPipe = AssetPipe

        // -- When

        // -- Then
        Assert.assertNotNull(assetPipe)
        Assert.assertNotNull(classUnderTest)
    }

    @Test
    fun companionTest() {

        // -- Given
        val assetCompanion = AssetPipe

        // -- Then
        Assert.assertNotNull(assetCompanion)
    }

    @Test
    fun transformTest() {

        // -- Given
        val valueString = "5"
        val valueInt = 5
        val valueBigDecimal = BigDecimal(5)
        val asset = TestConstants.CAD_ASSET

        val result1 = BigDecimal(500)
        val result2 = BigDecimal(0.05).setScale(2)
        val result3 = BigDecimal(0)

        // -- When
        val transformCAD1BD = AssetPipe.transform(valueBigDecimal, asset, "base")
        val transformCAD2BD = AssetPipe.transform(valueBigDecimal, asset, "trade")

        val transformCAD1String = AssetPipe.transform(valueString, asset, "base")
        val transformCAD2String = AssetPipe.transform(valueString, asset, "trade")

        val transformCAD1Int = AssetPipe.transform(valueInt, asset, "base")
        val transformCAD2Int = AssetPipe.transform(valueInt, asset, "trade")

        val transformZero = AssetPipe.transform(valueInt, asset, "")

        // -- Then
        Assert.assertEquals(transformCAD1BD, result1)
        Assert.assertEquals(transformCAD2BD, result2)

        Assert.assertEquals(transformCAD1String, result1)
        Assert.assertEquals(transformCAD2String, result2)

        Assert.assertEquals(transformCAD1Int, result1)
        Assert.assertEquals(transformCAD2Int, result2)

        Assert.assertEquals(transformZero, result3)
    }

    @Test
    fun transformDigitsTest() {

        // -- Given
        val valueString = "5"
        val valueInt = 5
        val valueBigDecimal = BigDecimal(5)
        val decimals = BigDecimal(TestConstants.CAD_ASSET.decimals)

        val result1 = BigDecimal(500)
        val result2 = BigDecimal(0.05).setScale(2)
        val result3 = BigDecimal(0)

        // -- When
        val transformCAD1BD = AssetPipe.transform(valueBigDecimal, decimals, "base")
        val transformCAD2BD = AssetPipe.transform(valueBigDecimal, decimals, "trade")

        val transformCAD1String = AssetPipe.transform(valueString, decimals, "base")
        val transformCAD2String = AssetPipe.transform(valueString, decimals, "trade")

        val transformCAD1Int = AssetPipe.transform(valueInt, decimals, "base")
        val transformCAD2Int = AssetPipe.transform(valueInt, decimals, "trade")

        val transformZero = AssetPipe.transform(valueInt, decimals, "")

        // -- Then
        Assert.assertEquals(transformCAD1BD, result1)
        Assert.assertEquals(transformCAD2BD, result2)

        Assert.assertEquals(transformCAD1String, result1)
        Assert.assertEquals(transformCAD2String, result2)

        Assert.assertEquals(transformCAD1Int, result1)
        Assert.assertEquals(transformCAD2Int, result2)

        Assert.assertEquals(transformZero, result3)
    }

    @Test
    fun test_trade() {

        // -- Given
        val btcPrice = BigDecimal(2738635)

        // Case: Trade in crypto
        val oneBTC = BigDecimal(1) // Direct from user_input
        val tradeOfOneBTC = AssetPipe.trade(oneBTC, btcPrice, AssetBankModel.Type.crypto)
        Assert.assertEquals(tradeOfOneBTC, BigDecimal(2738635))

        // Case: Trade in fiat with price in zero
        val oneUSD = BigDecimal(100) // In format of USD (convert from user_input)
        val tradeOfOneUsdWithZeroPrice = AssetPipe.trade(oneUSD, BigDecimal.zero(), AssetBankModel.Type.fiat)
        Assert.assertEquals(tradeOfOneUsdWithZeroPrice, BigDecimal(0))

        // Case: Trade in fiat
        val twoUSD = BigDecimal(200) // In format of USD (convert from user_input)
        val tradeOfTwoUsd = AssetPipe.trade(twoUSD, btcPrice, AssetBankModel.Type.fiat, BigDecimal(8))
        Assert.assertEquals(tradeOfTwoUsd, BigDecimal("0.00007302"))
    }
}