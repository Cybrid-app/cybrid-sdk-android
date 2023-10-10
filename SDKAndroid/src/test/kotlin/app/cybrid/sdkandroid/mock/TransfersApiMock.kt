package app.cybrid.sdkandroid.mock

import app.cybrid.cybrid_api_bank.client.apis.TransfersApi
import app.cybrid.cybrid_api_bank.client.models.TransferBankModel
import app.cybrid.cybrid_api_bank.client.models.TransferListBankModel
import app.cybrid.sdkandroid.AppModule
import app.cybrid.sdkandroid.Mocker
import java.math.BigDecimal as JavaBigDecimal
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import retrofit2.Response

object TransfersApiMock: Mocker {

    fun mock_listTransfers(response: Response<TransferListBankModel>): TransfersApi {

        this.init()
        val mockTransfersApi = mockk<TransfersApi>()
        coEvery { mockTransfersApi.listTransfers(customerGuid = any()) } returns response
        every { AppModule.getClient().createService(TransfersApi::class.java) } returns mockTransfersApi
        return mockTransfersApi
    }
}

object TransferBankModelMock {

    fun list(objects: List<TransferBankModel>): TransferListBankModel {
        return TransferListBankModel(
            total = JavaBigDecimal(1),
            page = JavaBigDecimal(0),
            perPage = JavaBigDecimal(10),
            objects = objects
        )
    }

    fun mock(): TransferBankModel {
        return TransferBankModel(
            guid = "1234",
            transferType = TransferBankModel.TransferType.funding,
            bankGuid = "1234",
            customerGuid = "1234",
            quoteGuid = "1234",
            externalBankAccountGuid = "1234",
            asset = "USD",
            side = TransferBankModel.Side.deposit,
            state = TransferBankModel.State.completed,
            failureCode = null,
            amount = JavaBigDecimal(100),
            estimatedAmount = JavaBigDecimal(100),
            networkFee = JavaBigDecimal(0)
        )
    }
}