package app.cybrid.sdkandroid.core

import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import java.text.DecimalFormat
import java.text.NumberFormat
import java.math.BigDecimal as JavaBigDecimal

object BigDecimalPipe {

    fun transform(value: BigDecimal, asset:AssetBankModel): String {

        val divisor = BigDecimal(10).pow(asset.decimals.toBigDecimal())
        val baseUnit = value.div(divisor)
        return transformAny(baseUnit, asset)
    }

    fun transform(value: JavaBigDecimal, asset:AssetBankModel): String {

        val divisor = BigDecimal(10).pow(asset.decimals.toBigDecimal())
        val baseUnit = value.toBigDecimal().div(divisor)
        return transformAny(baseUnit, asset)
    }

    fun transform(value: Int, asset:AssetBankModel): String {

        val divisor = BigDecimal(10).pow(asset.decimals.toBigDecimal())
        val baseUnit = BigDecimal(value).div(divisor)
        return transformAny(baseUnit, asset)
    }

    fun transform(value: String, asset:AssetBankModel): String {

        val divisor = BigDecimal(10).pow(asset.decimals.toBigDecimal())
        val baseUnit = BigDecimal(value).div(divisor)
        return transformAny(baseUnit, asset)
    }

    internal fun transformAny(baseUnit: BigDecimal, asset:AssetBankModel) : String {

        val baseUnitString = baseUnit.setScale(asset.decimals.intValueExact()).toPlainString()
        val separator = '.'
        val integer = BigDecimal(baseUnitString.split(".")[0])
        val decimal = baseUnitString.split(".")[1]

        val format: NumberFormat = DecimalFormat("'${asset.symbol}'#,###")
        format.minimumFractionDigits = 0
        val valueFormatted = integer.format(format)
        return valueFormatted + separator + decimal
    }
}