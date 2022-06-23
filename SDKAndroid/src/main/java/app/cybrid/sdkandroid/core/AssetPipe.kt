package app.cybrid.sdkandroid.core

import app.cybrid.cybrid_api_bank.client.models.AssetBankModel

class AssetPipe {

    companion object {

        fun transform(value:BigDecimal, asset: AssetBankModel, unit: String) : BigDecimal {
            return transformAny(value, asset, unit)
        }

        fun transform(value:String, asset: AssetBankModel, unit: String) : BigDecimal {
            return transformAny(BigDecimal(value.toInt()), asset, unit)
        }

        fun transform(value:Int, asset: AssetBankModel, unit: String) : BigDecimal {
            return transformAny(BigDecimal(value), asset, unit)
        }

        private fun transformAny(value:BigDecimal, asset: AssetBankModel, unit: String) : BigDecimal {

            val divisor = BigDecimal(10).pow(asset.decimals)
            val tradeUnit = value.div(divisor)
            val baseUnit = value.times(divisor)

            return when(unit) {

                "trade" -> tradeUnit
                "base" -> baseUnit
                else -> BigDecimal.ZERO
            }
        }
    }
}