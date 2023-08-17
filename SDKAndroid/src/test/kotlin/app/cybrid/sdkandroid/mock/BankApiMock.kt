package app.cybrid.sdkandroid.mock

import app.cybrid.cybrid_api_bank.client.models.BankBankModel
import java.time.OffsetDateTime

object BankApiMock: Mocker {

    // Mock Objects
    fun mock(): BankBankModel {
        return BankBankModel(
            guid = "1234",
            organizationGuid = "1234",
            name = "Bank Test",
            type = BankBankModel.Type.sandbox,
            features = listOf(BankBankModel.Features.kycIdentityVerifications),
            createdAt = OffsetDateTime.now(),
            supportedTradingSymbols = listOf("BTC"),
            supportedFiatAccountAssets = listOf("USD"),
            supportedCountryCodes = listOf("US")
        )
    }
}