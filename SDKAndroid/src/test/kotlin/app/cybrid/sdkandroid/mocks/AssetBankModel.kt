package app.cybrid.sdkandroid.mocks

import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import java.math.BigDecimal

object AssetBankModelObject {
    fun mock(): List<AssetBankModel> {
        return listOf(
            AssetBankModel(
                type = AssetBankModel.Type.fiat,
                code = "USD",
                name = "United States Dollar",
                symbol = "$",
                decimals = BigDecimal(2)
            ),
            AssetBankModel(
                type = AssetBankModel.Type.fiat,
                code = "CAD",
                name = "Canadian Dollar",
                symbol = "$",
                decimals = BigDecimal(2)
            ),
            AssetBankModel(
                type = AssetBankModel.Type.crypto,
                code = "BTC",
                name = "Bitcoin",
                symbol = "₿",
                decimals = BigDecimal(8)
            ),
            AssetBankModel(
                type = AssetBankModel.Type.crypto,
                code = "ETH",
                name = "Ethereum",
                symbol = "Ξ",
                decimals = BigDecimal(18)
            ),
            AssetBankModel(
                type = AssetBankModel.Type.crypto,
                code = "USDC",
                name = "USDC (ERC-20)",
                symbol = "$",
                decimals = BigDecimal(6)
            ),
            AssetBankModel(
                type = AssetBankModel.Type.crypto,
                code = "SOL",
                name = "Solana",
                symbol = "◎",
                decimals = BigDecimal(9 )
            )
        )
    }
}