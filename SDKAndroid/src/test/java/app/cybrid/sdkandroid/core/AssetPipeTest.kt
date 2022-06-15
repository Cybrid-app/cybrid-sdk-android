package app.cybrid.sdkandroid.core

import app.cybrid.sdkandroid.tools.TestConstants
import org.junit.Assert
import org.junit.Test

class AssetPipeTest {

    @Test
    fun initTest() {

        // -- Given
        val assetPipe = AssetPipe()

        // -- When

        // -- Then
        Assert.assertNotNull(assetPipe)
    }

    @Test
    fun companionTest() {

        // -- Given
        val assetCompanion = AssetPipe.Companion

        // -- Then
        Assert.assertNotNull(assetCompanion)
    }

    @Test
    fun transformTest() {

        // -- Given
        val valueString = "5"
        val valueInt = 5
        val asset = TestConstants.CAD_ASSET

        val result1 = BigDecimal(500)
        val result2 = BigDecimal(0.05).setScale(2)

        // -- When
        val transformCAD1String = AssetPipe.transform(valueString, asset, "base")
        val transformCAD2String = AssetPipe.transform(valueString, asset, "trade")
        val transformCAD1Int = AssetPipe.transform(valueInt, asset, "base")
        val transformCAD2Int = AssetPipe.transform(valueInt, asset, "trade")

        // -- Then
        Assert.assertEquals(transformCAD1String, result1)
        Assert.assertEquals(transformCAD2String, result2)
        Assert.assertEquals(transformCAD1Int, result1)
        Assert.assertEquals(transformCAD2Int, result2)
    }
}