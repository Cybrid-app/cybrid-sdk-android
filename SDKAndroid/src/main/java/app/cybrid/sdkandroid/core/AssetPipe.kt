package app.cybrid.sdkandroid.core

import app.cybrid.cybrid_api_bank.client.models.AssetBankModel

class AssetPipe {

    companion object {

        fun transform(value:String, asset: AssetBankModel, unit: String = "base") : BigDecimal {
            return transformAny(value.toInt(), asset, unit)
        }

        fun transform(value:Int, asset: AssetBankModel, unit: String = "base") : BigDecimal {
            return transformAny(value, asset, unit)
        }

        private fun transformAny(value:Int, asset: AssetBankModel, unit: String) : BigDecimal {

            val divisor = BigDecimal(10).pow(asset.decimals)
            val tradeUnit = BigDecimal(value).div(divisor)
            val baseUnit = BigDecimal(value).times(divisor)

            return when(unit) {

                "trade" -> tradeUnit
                "base" -> baseUnit
                else -> BigDecimal.ZERO
            }
        }
    }
}