package app.cybrid.sdkandroid.mock

import app.cybrid.cybrid_api_bank.client.models.CustomerBankModel
import java.time.OffsetDateTime

object CustomerApiMock: Mocker {

    // Mock Objects
    fun mock(): CustomerBankModel {
        return CustomerBankModel(
            guid = "1234",
            bankGuid = "1234",
            type = CustomerBankModel.Type.individual,
            createdAt = OffsetDateTime.now(),
            state = CustomerBankModel.State.verified,
            name = null,
            address = null,
            dateOfBirth = null,
            phoneNumber = null,
            emailAddress = null
        )
    }
}