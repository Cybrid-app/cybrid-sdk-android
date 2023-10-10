package app.cybrid.sdkandroid.mock

import app.cybrid.cybrid_api_bank.client.apis.AccountsApi
import app.cybrid.cybrid_api_bank.client.apis.PricesApi
import app.cybrid.cybrid_api_bank.client.apis.QuotesApi
import app.cybrid.cybrid_api_bank.client.apis.TransfersApi
import app.cybrid.cybrid_api_bank.client.models.AccountBankModel
import app.cybrid.cybrid_api_bank.client.models.AccountListBankModel
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.cybrid_api_bank.client.models.PostQuoteBankModel
import app.cybrid.cybrid_api_bank.client.models.QuoteBankModel
import app.cybrid.cybrid_api_bank.client.models.SymbolPriceBankModel
import app.cybrid.cybrid_api_bank.client.models.TransferBankModel
import app.cybrid.sdkandroid.AppModule
import app.cybrid.sdkandroid.Mocker
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import retrofit2.Response
import java.math.BigDecimal as JavaBigDecimal

object CryptoTransferApiMock: Mocker {

    fun mock_listAccounts(response: Response<AccountListBankModel>, init: Boolean = true): AccountsApi {

        if (init) { this.init() }
        val mockAccountsApi = mockk<AccountsApi>()
        coEvery { mockAccountsApi.listAccounts(perPage = any(), customerGuid = any()) } returns response
        every { AppModule.getClient().createService(AccountsApi::class.java) } returns mockAccountsApi
        return mockAccountsApi
    }

    fun mock_listPrices(response: Response<List<SymbolPriceBankModel>>, init: Boolean = true): PricesApi {

        if (init) { this.init() }
        val mockPricesApi = mockk<PricesApi>()
        coEvery { mockPricesApi.listPrices() } returns response
        every { AppModule.getClient().createService(PricesApi::class.java) } returns mockPricesApi
        return mockPricesApi
    }

    fun mock_createQuote(response: Response<QuoteBankModel>, init: Boolean = true): QuotesApi {

        if (init) { this.init() }
        val mockQuotesApi = mockk<QuotesApi>()
        coEvery { mockQuotesApi.createQuote(any()) } returns response
        every { AppModule.getClient().createService(QuotesApi::class.java) } returns mockQuotesApi
        return mockQuotesApi
    }

    fun mock_createTransfer(response: Response<TransferBankModel>, init: Boolean = true): TransfersApi {

        if (init) { this.init() }
        val mockTransferApi = mockk<TransfersApi>()
        coEvery { mockTransferApi.createTransfer(any()) } returns response
        every { AppModule.getClient().createService(TransfersApi::class.java) } returns mockTransferApi
        return mockTransferApi
    }
}

object CryptoTransferApiMockModel {

    // -- Assets
    fun btc(): AssetBankModel {
        return AssetBankModel(
            type = AssetBankModel.Type.crypto,
            code = "BTC",
            name = "Bitcoin",
            symbol = "BTC",
            decimals = JavaBigDecimal(8)
        )
    }

    // -- Accounts
    fun accountUSD(): AccountBankModel {
        return AccountBankModel(
            guid = "12345",
            type = AccountBankModel.Type.fiat,
            asset = "USD",
            name = "USD Test",
            customerGuid = "12345",
            platformBalance = java.math.BigDecimal(100000),
            platformAvailable = java.math.BigDecimal(30000),
            state = AccountBankModel.State.created
        )
    }

    fun accountBTC(): AccountBankModel {
        return AccountBankModel(
            guid = "12345",
            type = AccountBankModel.Type.trading,
            asset = "BTC",
            name = "BTC Test",
            customerGuid = "12345",
            platformBalance = java.math.BigDecimal(100000),
            platformAvailable = java.math.BigDecimal(40000),
            state = AccountBankModel.State.created
        )
    }

    fun accountBTCWithoutBalance(): AccountBankModel {
        return AccountBankModel(
            guid = "12345",
            type = AccountBankModel.Type.trading,
            asset = "BTC",
            name = "BTC Test",
            customerGuid = "12345",
            platformBalance = null,
            platformAvailable = null,
            state = AccountBankModel.State.created
        )
    }

    fun accountMXN(): AccountBankModel {
        return AccountBankModel(
            guid = "12345",
            type = AccountBankModel.Type.fiat,
            asset = "MXN",
            name = "MXN Test",
            customerGuid = "12345",
            platformBalance = java.math.BigDecimal(100000),
            platformAvailable = java.math.BigDecimal(40000),
            state = AccountBankModel.State.created
        )
    }

    fun accountWithoutAsset(): AccountBankModel {
        return AccountBankModel(
            guid = "12345",
            type = AccountBankModel.Type.trading,
            asset = null,
            name = "No Asset",
            customerGuid = "12345",
            platformBalance = java.math.BigDecimal(100000),
            platformAvailable = java.math.BigDecimal(40000),
            state = AccountBankModel.State.created
        )
    }

    fun accountList(): AccountListBankModel {
        return AccountListBankModel(
            total = java.math.BigDecimal(2),
            page = java.math.BigDecimal(1),
            perPage = java.math.BigDecimal(50),
            objects = listOf(accountUSD(), accountBTC())
        )
    }

    // -- Prices
    fun btcUsdPrice(): SymbolPriceBankModel {
        return SymbolPriceBankModel(
            symbol = "BTC-USD",
            buyPrice = JavaBigDecimal(1000000),
            sellPrice = JavaBigDecimal(2000000)
        )
    }

    fun btcUsdPriceWithPricesAsNull(): SymbolPriceBankModel {
        return SymbolPriceBankModel(
            symbol = "BTC-USD",
            buyPrice = null,
            sellPrice = null
        )
    }

    fun ethUsdPrice(): SymbolPriceBankModel {
        return SymbolPriceBankModel(
            symbol = "ETH-USD",
            buyPrice = JavaBigDecimal(100000),
            sellPrice = JavaBigDecimal(2000000)
        )
    }

    fun pricesList(): List<SymbolPriceBankModel> {
        return listOf(
            btcUsdPrice(),
            ethUsdPrice()
        )
    }

    fun pricesListWithoutPrices(): List<SymbolPriceBankModel> {
        return listOf(
            btcUsdPriceWithPricesAsNull()
        )
    }

    // -- Quotes
    fun postQuote(): PostQuoteBankModel {
        return PostQuoteBankModel(
            productType = PostQuoteBankModel.ProductType.cryptoTransfer,
            customerGuid = "12345",
            asset = "BTC",
            side = PostQuoteBankModel.Side.withdrawal,
            deliverAmount = JavaBigDecimal(200000000)
        )
    }

    fun quote(): QuoteBankModel {
        return QuoteBankModel(
            guid = "12345",
            customerGuid = "12345"
        )
    }

    // -- Transfers
    fun transfer(): TransferBankModel {
        return TransferBankModel(
            guid = "12345",
            transferType = TransferBankModel.TransferType.crypto,
            customerGuid = "12345",
            quoteGuid = "12345",
            asset = "BTC",
            side = TransferBankModel.Side.withdrawal,
            amount = JavaBigDecimal(3000000)
        )
    }
}