package app.cybrid.sdkandroid.core

import app.cybrid.cybrid_api_bank.client.models.AssetBankModel

object AssetPipe {

    const val AssetPipeTrade: String = "trade"
    const val AssetPipeBase: String = "base"

    fun transform(value:BigDecimal, asset: AssetBankModel, unit: String) : BigDecimal {
        return transformAny(value, asset.decimals.toBigDecimal(), unit)
    }

    fun transform(value:String, asset: AssetBankModel, unit: String) : BigDecimal {
        return transformAny(BigDecimal(value), asset.decimals.toBigDecimal(), unit)
    }

    fun transform(value:Int, asset: AssetBankModel, unit: String) : BigDecimal {
        return transformAny(BigDecimal(value), asset.decimals.toBigDecimal(), unit)
    }

    fun transform(value:BigDecimal, decimals: BigDecimal, unit: String) : BigDecimal {
        return transformAny(value, decimals, unit)
    }

    fun transform(value:String, decimals: BigDecimal, unit: String) : BigDecimal {
        return transformAny(BigDecimal(value), decimals, unit)
    }

    fun transform(value:Int, decimals: BigDecimal, unit: String) : BigDecimal {
        return transformAny(BigDecimal(value), decimals, unit)
    }

    /**
     * Method to transform a number into a two different ways
     * base:
     *      Takes a number like 10 in asset USD and convert to --> 1000 (cents/base unit)
     * trade:
     *      Takes a number like 1000 in cents of USD and convert to --> 10
     * **/
    private fun transformAny(value:BigDecimal, decimals: BigDecimal, unit: String) : BigDecimal {

        val divisor = BigDecimal(10).pow(decimals)
        val tradeUnit = value.div(divisor)
        val baseUnit = value.times(divisor)

        return when(unit) {

            "trade" -> tradeUnit
            "base" -> baseUnit
            else -> BigDecimal(0)
        }
    }
}