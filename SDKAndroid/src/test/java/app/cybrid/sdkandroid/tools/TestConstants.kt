package app.cybrid.sdkandroid.tools

import app.cybrid.cybrid_api_bank.client.models.*
import app.cybrid.sdkandroid.components.accounts.entity.AccountAssetPriceModel
import java.math.BigDecimal
import java.time.OffsetDateTime

object TestConstants {

    const val expiredToken = "eyJraWQiOiJsWTdPaTI2SXZaWXhwSXJuUzJsZGp1ODZUWGZxcnlKVDlZNnZVc1hzRUNFIiwiYWxnIjoiUlM1MTIifQ.eyJpc3MiOiJodHRwczovL2lkLmRlbW8uY3licmlkLmFwcCIsImF1ZCI6WyJodHRwczovL2JhbmsuZGVtby5jeWJyaWQuYXBwIiwiaHR0cHM6Ly9hcGktaW50ZXJuYWwta2V5Lmhlcm9rdWFwcC5jb20iLCJodHRwczovL2lkLmRlbW8uY3licmlkLmFwcCIsImh0dHBzOi8vYXBpLWludGVybmFsLWFjY291bnRzLmhlcm9rdWFwcC5jb20iLCJodHRwczovL2FwaS1pbnRlcm5hbC1pZGVudGl0eS5oZXJva3VhcHAuY29tIiwiaHR0cHM6Ly9hcGktaW50ZWdyYXRpb24tZXhjaGFuZ2UuaGVyb2t1YXBwLmNvbSJdLCJzdWIiOiIzYWEyYmM0ODdhZTQ2OWJiYzAzY2I0MmNmNzJlM2FhMyIsInN1Yl90eXBlIjoiYmFuayIsInNjb3BlIjpbImJhbmtzOnJlYWQiLCJiYW5rczp3cml0ZSIsImFjY291bnRzOnJlYWQiLCJhY2NvdW50czpleGVjdXRlIiwiY3VzdG9tZXJzOnJlYWQiLCJjdXN0b21lcnM6d3JpdGUiLCJjdXN0b21lcnM6ZXhlY3V0ZSIsInByaWNlczpyZWFkIiwicXVvdGVzOmV4ZWN1dGUiLCJ0cmFkZXM6ZXhlY3V0ZSIsInRyYWRlczpyZWFkIl0sImlhdCI6MTY1MTg1NDcwNywiZXhwIjoxNjUxODU2NTA3LCJqdGkiOiJkMzcxNjc0ZC0wOTgxLTRkZDUtYjc2YS02ZTFiOTQ4MTk4N2YiLCJ0b2tlbl90eXBlIjoiYWNjZXNzIiwicHJvcGVydGllcyI6eyJ0eXBlIjoic2FuZGJveCJ9fQ.LM8OBLOLHsCuWx4XNHjIHmvmuyZBviO_cYQ-WTqjiszpL0dVuC_z6v9FJ1IwVO8SxBhwH3Ut1aVE3kTapHLOmN6c6ca7y-fURPl8ojxAvs0eDMkT-HYD6QUHZ7ylrppa0w78Q3gOObytELGbFTpJ_QYd8NE1eewsoDeNKDc9p-RhGHoamAjHfnJcC4zOyXKjIr_Etn5RgA4xmYwxe8887vC9rPo9vuOou71gWufNrgqNyUBpuiDpAgTw4zhNynlJBXtpL7kNCaKHn5D8qRUqJyQzSZ1jctmzFLls-hNbC58wb8t0vPAYvVsgzQOYDiLKsJMUBJ4IxAiQbfKVe3HpdobqXCu_7fborK7MB2Q2Msx-YMby9DFseOjXV6i-pNYRxvLIVE1hZkjp15-edsNclTGpIngIGFWGp2Tzwd3m8PG-9WELWRufZCbTlwrzuIVwpCRXdP-4Zt_VP7cXhHKWJQmKFLKJ_WSzKvwqaL_-aMpb6rihv4rjeDuFTtrgGDbx0TXYMAjjIdRoPm9PuAwnmclnASUdBBAHWCXu0V96AUJygn9QQZ-1ndlaQCpQ3Z6URLkGnv5-9MDT8GLTbC6tTzopHYNztNiJ-Z8gQtLJS8lSof3DrhJWgpmyd_OqSwYzeuiiz9lt5haUJnSy8QxFh7tWaS5-wCkcRqy_qAUzZOQ"

    val BTC_ASSET: AssetBankModel = AssetBankModel(
        code = "BTC",
        decimals = BigDecimal(8),
        name = "Bitcoin",
        symbol = "₿",
        type= AssetBankModel.Type.crypto,
    )

    val ETH_ASSET: AssetBankModel = AssetBankModel(
        code = "ETH",
        decimals = BigDecimal(18),
        name = "Ethereum",
        symbol = "Ξ",
        type= AssetBankModel.Type.crypto,
    )

    val CAD_ASSET: AssetBankModel = AssetBankModel(
        code = "CAD",
        decimals = BigDecimal(2),
        name = "Canadian Dollar",
        symbol = "$",
        type= AssetBankModel.Type.fiat,
    )

    val USD_ASSET: AssetBankModel = AssetBankModel(
        code = "USD",
        decimals = BigDecimal(2),
        name = "American Dollar",
        symbol = "$",
        type= AssetBankModel.Type.fiat,
    )

    val prices:List<SymbolPriceBankModel> = listOf(
        SymbolPriceBankModel(
            symbol = "BTC-USD",
            buyPrice = BigDecimal(2374100),
            sellPrice = BigDecimal(2374000)
        ),
        SymbolPriceBankModel(
            symbol = "ETH-USD",
            buyPrice = BigDecimal(168230),
            sellPrice = BigDecimal(168220)
        )
    )

    val assets:List<AssetBankModel> = listOf(
        BTC_ASSET, ETH_ASSET, USD_ASSET, CAD_ASSET
    )

    val buyQuote = QuoteBankModel(
        guid = "cb18e7d490c08da003c1afe5c31b8a6d",
        productType = QuoteBankModel.ProductType.trading,
        customerGuid = "bf10305829337d106b82c521bb6c8fd2",
        symbol = "BTC-USD",
        side = QuoteBankModel.Side.buy,
        receiveAmount = java.math.BigDecimal("1321413"),
        deliverAmount = java.math.BigDecimal("25000"),
        fee = BigDecimal("0"),
        issuedAt = null,
        expiresAt = null
    )

    val sellQuote = QuoteBankModel(
        guid = "cb18e7d490c08da003c1afe5c31b8a6d",
        productType = QuoteBankModel.ProductType.trading,
        customerGuid = "bf10305829337d106b82c521bb6c8fd2",
        symbol = "BTC-USD",
        side = QuoteBankModel.Side.sell,
        receiveAmount = java.math.BigDecimal("25000"),
        deliverAmount = java.math.BigDecimal("1321413"),
        fee = BigDecimal("0"),
        issuedAt = null,
        expiresAt = null
    )

    val accounts:List<AccountBankModel> = listOf(
        AccountBankModel(
            type = AccountBankModel.Type.trading,
            guid = "5b13ffda9fc47c322af321434818709a",
            asset = "ETH",
            name = "ETH-USD",
            createdAt = OffsetDateTime.parse("2022-08-02T10:55:34.039847-05:00"),
            platformBalance = BigDecimal(2500000000000000000)
        ),
        AccountBankModel(
            type = AccountBankModel.Type.trading,
            guid = "15d755452f76634df53a88efc06248ec",
            asset = "BTC",
            name = "BTC-USD",
            createdAt = OffsetDateTime.parse("2022-08-02T10:55:34.039847-05:00"),
            platformBalance = BigDecimal(200000000)
        )
    )

    val trades:List<TradeBankModel> = listOf(
        TradeBankModel(
            guid = "3c0af815210ca8ce21294a6e81979d7b",
            symbol = "ETH-USD",
            side = TradeBankModel.Side.sell,
            receiveAmount = BigDecimal(14278),
            deliverAmount = BigDecimal(100000000000000000),
            fee = BigDecimal(0)
        ),
        TradeBankModel(
            guid = "7b19efae72f928ff378898de3484acac",
            symbol = "ETH-USD",
            side = TradeBankModel.Side.buy,
            receiveAmount = BigDecimal(100000000000000000),
            deliverAmount = BigDecimal(14286),
            fee = BigDecimal(0)
        )
    )

    val accountsFormatted:List<AccountAssetPriceModel> = listOf(
        AccountAssetPriceModel(
            accountAssetCode = "ETH",
            accountBalance = BigDecimal("2500000000000000000"),
            accountBalanceFormatted = app.cybrid.sdkandroid.core.BigDecimal(2.5),
            accountBalanceFormattedString = "2.5",
            accountBalanceInFiat = app.cybrid.sdkandroid.core.BigDecimal(420575.00).setScale(2),
            accountBalanceInFiatFormatted = "$4,205.75",
            accountGuid = "5b13ffda9fc47c322af321434818709a",
            accountType = AccountBankModel.Type.trading,
            accountCreated = OffsetDateTime.parse("2022-08-02T10:55:34.039847-05:00"),
            assetName = "Ethereum",
            assetSymbol = "Ξ",
            assetType = AssetBankModel.Type.crypto,
            assetDecimals = BigDecimal(18),
            pairAsset = USD_ASSET,
            buyPrice = app.cybrid.sdkandroid.core.BigDecimal(168230),
            buyPriceFormatted = "$1,682.30",
            sellPrice = BigDecimal(168220)
        ),
        AccountAssetPriceModel(
            accountAssetCode = "BTC",
            accountBalance = BigDecimal("200000000"),
            accountBalanceFormatted = app.cybrid.sdkandroid.core.BigDecimal(2),
            accountBalanceFormattedString = "2",
            accountBalanceInFiat = app.cybrid.sdkandroid.core.BigDecimal(4748200).setScale(2),
            accountBalanceInFiatFormatted = "$47,482.00",
            accountGuid = "15d755452f76634df53a88efc06248ec",
            accountType = AccountBankModel.Type.trading,
            accountCreated = OffsetDateTime.parse("2022-08-02T10:55:34.039847-05:00"),
            assetName = "Bitcoin",
            assetSymbol = "₿",
            assetType = AssetBankModel.Type.crypto,
            assetDecimals = BigDecimal(8),
            pairAsset = USD_ASSET,
            buyPrice = app.cybrid.sdkandroid.core.BigDecimal(2374100),
            buyPriceFormatted = "$23,741.00",
            sellPrice = BigDecimal(2374000)
        )
    )

    // -- JSON

    const val CREATE_CUSTOMER_SUCCESS = "{\"guid\":\"1234\",\"bank_guid\":\"1234\",\"type\":\"individual\",\"created_at\":\"2022-06-23T07:08:16.718Z\",\"state\":\"storing\"}"
    const val FETCH_CUSTOMER_SUCCESS = "{\"guid\":\"1234\",\"bank_guid\":\"1234\",\"type\":\"individual\",\"created_at\":\"2022-06-23T07:08:16.718Z\",\"state\":\"storing\"}"
    const val CREATE_IDENTITY_VERIFICATION_SUCCESS = "{\"type\":\"kyc\",\"guid\":\"1234\",\"customer_guid\":\"1234\",\"created_at\":\"2022-11-16T23:47:20.110Z\",\"method\":\"id_and_selfie\",\"state\":\"storing\",\"outcome\":null,\"failure_codes\":[]}"
    const val FETCH_LIST_IDENTITY_VERIFICATIONS_SUCCESS_EMPTY = "{\"total\":0,\"page\":0,\"per_page\":0,\"objects\":[]}"
    const val FETCH_LIST_IDENTITY_VERIFICATIONS_SUCCESS = "{\"total\":1,\"page\":0,\"per_page\":1,\"objects\":[{\"type\":\"kyc\",\"guid\":\"1234\",\"customer_guid\":\"1234\",\"created_at\":\"2022-11-16T23:47:20.110Z\",\"method\":\"id_and_selfie\",\"state\":\"storing\",\"outcome\":null,\"failure_codes\":[],\"persona_inquiry_id\":null,\"persona_state\":null}]}"
    const val FETCH_IDENTITY_VERIFICATION_SUCCESS = "{\"type\":\"kyc\",\"guid\":\"1234\",\"customer_guid\":\"1234\",\"created_at\":\"2022-11-16T23:47:20.110Z\",\"method\":\"id_and_selfie\",\"state\":\"storing\",\"outcome\":null,\"failure_codes\":[],\"persona_inquiry_id\":null,\"persona_state\":null}"
    const val CREATE_WORKFLOW_SUCCESS = "{\"guid\":\"1234\",\"customer_guid\":\"1234\",\"type\":\"plaid\",\"created_at\":\"2022-11-29T16:39:33.905Z\"}"
    const val FETCH_WORKFLOW_SUCCESS = "{\"guid\":\"1234\",\"customer_guid\":\"1234\",\"type\":\"plaid\",\"created_at\":\"2022-11-29T16:39:33.905Z\",\"plaid_link_token\":\"link-sandbox-c\"}"
    const val FETCH_WORKFLOW_SUCCESS_INCOMPLETE = "{\"guid\":\"1234\",\"customer_guid\":\"1234\",\"type\":\"plaid\",\"created_at\":\"2022-11-29T16:39:33.905Z\",\"plaid_link_token\":\"\"}"
    const val CREATE_EXTERNAL_BANK_ACCOUNT = "{\"guid\":\"1234\",\"name\":\"USD\",\"asset\":\"USD\",\"account_kind\":\"plaid\",\"environment\":\"sandbox\",\"created_at\":\"2022-11-29T16:40:16.057Z\",\"customer_guid\":\"1234\",\"bank_guid\":\"1234\",\"plaid_access_token\":null}"
    const val FETCH_LIST_EXTERNAL_BANK_ACCOUNT = "{\"total\":1,\"page\":0,\"per_page\":1,\"objects\":[{\"guid\":\"stringstringstringstringstringst\",\"name\":\"string\",\"asset\":\"string\",\"account_kind\":\"plaid\",\"environment\":\"sandbox\",\"bank_guid\":\"stringstringstringstringstringst\",\"customer_guid\":\"stringstringstringstringstringst\",\"created_at\":\"2022-12-15T07:12:23.873Z\",\"plaid_institution_id\":\"string\",\"plaid_account_mask\":\"string\",\"plaid_account_name\":\"string\",\"state\":\"storing\",\"failure_code\":\"string\"}]}"
    const val CREATE_BANK_SUCCESS = "{\"guid\":\"string\",\"organization_guid\":\"string\",\"name\":\"string\",\"type\":\"sandbox\",\"supported_trading_symbols\":[\"string\"],\"supported_fiat_account_assets\":[\"USD\"],\"supported_country_codes\":[\"string\"],\"features\":[\"attestation_identity_records\"],\"created_at\":\"2022-11-29T22:35:04.005Z\"}"
    const val CREATE_BANK_SUCCESS_EMPTY = "{\"guid\":\"string\",\"organization_guid\":\"string\",\"name\":\"string\",\"type\":\"sandbox\",\"supported_trading_symbols\":[\"string\"],\"supported_fiat_account_assets\":[\"string\"],\"supported_country_codes\":[\"string\"],\"features\":[\"attestation_identity_records\"],\"created_at\":\"2022-11-29T22:35:04.005Z\"}"
    const val FETCH_ACCOUNTS_LIST_SUCCESS = "{\"total\":2,\"page\":0,\"per_page\":2,\"objects\":[{\"type\":\"fiat\",\"guid\":\"1234\",\"created_at\":\"2022-12-13T16:42:09.956Z\",\"asset\":\"USD\",\"name\":\"test1\",\"bank_guid\":\"1234\",\"customer_guid\":\"1234\",\"platform_balance\":10,\"platform_available\":10,\"state\":\"storing\"},{\"type\":\"fiat\",\"guid\":\"1234\",\"created_at\":\"2022-11-13T16:42:09.956Z\",\"asset\":\"USD\",\"name\":\"test2\",\"bank_guid\":\"1234\",\"customer_guid\":\"1234\",\"platform_balance\":10,\"platform_available\":10,\"state\":\"created\"}]}"
    const val CREATE_QUOTE_SUCCESS = "{\"guid\":\"stringstringstringstringstringst\",\"product_type\":\"trading\",\"customer_guid\":\"stringstringstringstringstringst\",\"symbol\":\"string\",\"side\":\"buy\",\"receive_amount\":0,\"deliver_amount\":0,\"fee\":0,\"issued_at\":\"2022-12-15T08:49:40.526Z\",\"expires_at\":\"2022-12-15T08:49:40.526Z\"}"
    const val CREATE_TRADE_SUCCESS = "{\"guid\":\"stringstringstringstringstringst\",\"customer_guid\":\"string\",\"quote_guid\":\"stringstringstringstringstringst\",\"symbol\":\"string\",\"side\":\"buy\",\"state\":\"storing\",\"receive_amount\":0,\"deliver_amount\":0,\"fee\":0,\"created_at\":\"2022-12-15T08:39:22.907Z\"}"
    const val FETCH_ASSETS_SUCCESS = "{\"total\":0,\"page\":0,\"per_page\":0,\"objects\":[{\"type\":\"fiat\",\"code\":\"USD\",\"name\":\"USD\",\"symbol\":\"string\",\"decimals\":2}]}"
}