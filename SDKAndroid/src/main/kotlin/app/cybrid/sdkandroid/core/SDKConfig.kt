package app.cybrid.sdkandroid.core

import app.cybrid.cybrid_api_bank.client.models.BankBankModel
import app.cybrid.cybrid_api_bank.client.models.CustomerBankModel
import app.cybrid.sdkandroid.listener.CybridSDKEvents

class SDKConfig {

    var environment: CybridEnvironment = CybridEnvironment.SANDBOX
    var bearer: String = ""
    var customerGuid: String = ""
    var customer: CustomerBankModel? = null
    var bank: BankBankModel? = null
    var logTag: String = "CybridSDK"
    var listener: CybridSDKEvents? = null

    constructor(
        environment: CybridEnvironment = CybridEnvironment.SANDBOX,
        bearer: String = "",
        customerGuid: String = "",
        customer: CustomerBankModel? = null,
        bank: BankBankModel? = null,
        logTag: String = "CybridSDK",
        listener: CybridSDKEvents? = null
    ) {
        this.environment = environment
        this.bearer = bearer
        this.customerGuid = customerGuid
        this.customer = customer
        this.bank = bank
        this.logTag = logTag
        this.listener = listener
    }
}