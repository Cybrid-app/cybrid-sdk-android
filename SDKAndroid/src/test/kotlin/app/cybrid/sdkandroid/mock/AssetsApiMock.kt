package app.cybrid.sdkandroid.mock

import app.cybrid.cybrid_api_bank.client.apis.AssetsApi
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.cybrid_api_bank.client.models.AssetListBankModel
import app.cybrid.sdkandroid.AppModule
import app.cybrid.sdkandroid.Mocker
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import retrofit2.Response
import java.math.BigDecimal

object AssetsApiMock: Mocker {

    fun mockListAssets(
        response: Response<AssetListBankModel> = getAssetsListBankModelMock()
    ): AssetsApi {

        this.init()
        val mockAssetsApi = mockk<AssetsApi>()
        coEvery { mockAssetsApi.listAssets(page = any(), perPage = any()) } returns response
        every { AppModule.getClient().createService(AssetsApi::class.java) } returns mockAssetsApi
        return mockAssetsApi
    }

    private fun getAssetsListBankModelMock(): Response<AssetListBankModel> {
        return Response.success(
            AssetListBankModel(
                total = BigDecimal(4),
                page = BigDecimal(0),
                perPage = BigDecimal(10),
                objects = AssetBankModelObject.mock()
            )
        )
    }

    fun getAssetsListBankModelMock_DataNull(): Response<AssetListBankModel> {
        return Response.success(
            null
        )
    }

    object AssetBankModelObject {
        fun mock(): List<AssetBankModel> {
            return listOf(
                AssetBankModel(
                    type = "fiat",
                    code = "USD",
                    name = "United States Dollar",
                    symbol = "$",
                    decimals = BigDecimal(2)
                ),
                AssetBankModel(
                    type = "fiat",
                    code = "CAD",
                    name = "Canadian Dollar",
                    symbol = "$",
                    decimals = BigDecimal(2)
                ),
                AssetBankModel(
                    type = "crypto",
                    code = "BTC",
                    name = "Bitcoin",
                    symbol = "₿",
                    decimals = BigDecimal(8)
                ),
                AssetBankModel(
                    type = "crypto",
                    code = "ETH",
                    name = "Ethereum",
                    symbol = "Ξ",
                    decimals = BigDecimal(18)
                ),
                AssetBankModel(
                    type = "crypto",
                    code = "USDC",
                    name = "USDC (ERC-20)",
                    symbol = "$",
                    decimals = BigDecimal(6)
                ),
                AssetBankModel(
                    type = "crypto",
                    code = "SOL",
                    name = "Solana",
                    symbol = "◎",
                    decimals = BigDecimal(9 )
                )
            )
        }
    }
}