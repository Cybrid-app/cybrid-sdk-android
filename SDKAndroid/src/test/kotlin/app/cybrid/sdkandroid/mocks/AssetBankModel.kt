package app.cybrid.sdkandroid.mocks

import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import java.math.BigDecimal

object AssetBankModelObject {
    fun mock(): List<AssetBankModel> {
        return listOf(
            AssetBankModel(
                type = AssetBankModel.Type.fiat,
                code = "USD",
                name = "Unitade State Dollar",
                symbol = "$",
                decimals = BigDecimal(2)
            ),
            AssetBankModel(
                type = AssetBankModel.Type.crypto,
                code = "BTC",
                name = "Bitcoin",
                symbol = "₿",
                decimals = BigDecimal(8)
            )
        )
    }
}