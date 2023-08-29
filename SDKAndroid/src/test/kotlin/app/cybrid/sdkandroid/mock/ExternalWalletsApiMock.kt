package app.cybrid.sdkandroid.mock

import app.cybrid.cybrid_api_bank.client.apis.ExternalWalletsApi
import app.cybrid.cybrid_api_bank.client.models.ExternalWalletBankModel
import app.cybrid.cybrid_api_bank.client.models.ExternalWalletListBankModel
import app.cybrid.sdkandroid.AppModule
import java.math.BigDecimal as JavaBigDecimal
import retrofit2.Response
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk

object ExternalWalletsApiMock: Mocker {

    fun mock_listExternalWallets(response: Response<ExternalWalletListBankModel>): ExternalWalletsApi {

        this.init()
        val mockExternalWalletsApi = mockk<ExternalWalletsApi>()
        coEvery { mockExternalWalletsApi.listExternalWallets() } returns response
        every { AppModule.getClient().createService(ExternalWalletsApi::class.java) } returns mockExternalWalletsApi
        return mockExternalWalletsApi
    }
}

object ExternalWalletBankModelMock {

    fun list(objects: List<ExternalWalletBankModel>): ExternalWalletListBankModel {
        return ExternalWalletListBankModel(
            total = JavaBigDecimal(1),
            page = JavaBigDecimal(0),
            perPage = JavaBigDecimal(10),
            objects = objects
        )
    }

    fun mock(): ExternalWalletBankModel {
        return ExternalWalletBankModel(
            guid = "1234",
            name = "Test",
            asset = "BTC",
            environment = ExternalWalletBankModel.Environment.sandbox,
            bankGuid = "1234",
            customerGuid = "1234",
            address = "0x1234",
            tag = "1234",
            createdAt = java.time.OffsetDateTime.now(),
            state = ExternalWalletBankModel.State.completed,
            failureCode = null
        )
    }
}