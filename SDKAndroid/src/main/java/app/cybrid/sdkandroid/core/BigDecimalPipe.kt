package app.cybrid.sdkandroid.core

import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import java.text.DecimalFormat
import java.text.NumberFormat

object BigDecimalPipe {

    fun transform(value: BigDecimal, asset:AssetBankModel): String? {

        val divisor = BigDecimal(10).pow(asset.decimals.toBigDecimal())
        val baseUnit = value.div(divisor)
        val prefix = if (value == BigDecimal(0)) "${asset.symbol}0" else asset.symbol
        return transformAny(baseUnit, asset, prefix)
    }

    fun transform(value: Int, asset:AssetBankModel): String? {

        val divisor = BigDecimal(10).pow(asset.decimals.toBigDecimal())
        val baseUnit = BigDecimal(value).div(divisor)
        val prefix = if (value == 0) "${asset.symbol}0" else asset.symbol
        return transformAny(baseUnit, asset, prefix)
    }

    fun transform(value: String, asset:AssetBankModel): String? {

        val divisor = BigDecimal(10).pow(asset.decimals.toBigDecimal())
        val baseUnit = BigDecimal(value).div(divisor)
        val prefix = if (value == "0") "${asset.symbol}0" else asset.symbol
        return transformAny(baseUnit, asset, prefix)
    }

    private fun transformAny(baseUnit:BigDecimal, asset:AssetBankModel, prefix:String) : String? {

        val baseUnitString = baseUnit.setScale(asset.decimals.intValueExact()).toPlainString()
        if (baseUnitString.contains('.')) {

            val separator = '.'
            val integer = BigDecimal(baseUnitString.split(".")[0])
            var decimal = baseUnitString.split(".")[1]
            if (decimal.length < Constants.MIN_FRACTION_DIGITS) {
                decimal += '0'
            }

            val format:NumberFormat = DecimalFormat("'${asset.symbol}'#,###")
            format.minimumFractionDigits = 0
            val valueFormatted = integer.format(format)
            return valueFormatted + separator + decimal

        } else {

            val format: NumberFormat = DecimalFormat("'$prefix'#,###")
            format.minimumFractionDigits = Constants.MIN_FRACTION_DIGITS
            format.minimumIntegerDigits = Constants.MIN_INTEGER_DIGITS
            return baseUnit.format(format)
        }
    }
}