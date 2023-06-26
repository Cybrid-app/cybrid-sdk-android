package app.cybrid.sdkandroid.mocks

import app.cybrid.cybrid_api_bank.client.models.AssetListBankModel
import retrofit2.Response
import java.math.BigDecimal

object Mocks {

    fun getAssetsListBankModelMock(): Response<AssetListBankModel> {
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
}