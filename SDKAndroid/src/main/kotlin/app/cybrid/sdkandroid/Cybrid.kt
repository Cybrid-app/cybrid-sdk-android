package app.cybrid.sdkandroid

import app.cybrid.cybrid_api_bank.client.auth.HttpBearerAuth
import app.cybrid.cybrid_api_bank.client.models.BankBankModel
import app.cybrid.cybrid_api_bank.client.models.CustomerBankModel
import app.cybrid.sdkandroid.core.CybridEnvironment
import app.cybrid.sdkandroid.core.SDKConfig
import app.cybrid.sdkandroid.listener.CybridSDKEvents
import app.cybrid.sdkandroid.util.Logger
import app.cybrid.sdkandroid.util.LoggerEvents
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.OkHttpClient

open class Cybrid {

    private var configured: Boolean = false
    private var bearer: String = ""
    var logTag: String = "CybridSDK"
    var customerGuid: String = ""
        private set
    var environment = CybridEnvironment.SANDBOX
        private set
    var customer: CustomerBankModel? = null
    var bank: BankBankModel? = null
    var listener: CybridSDKEvents? = null
        private set
    private var completion: (() -> Unit)? = null

    var invalidToken = false
    var imagesUrl = "https://images.cybrid.xyz/sdk/assets/png/color/"
        private set
    var imagesSize = "@2x.png"
        private set
    var accountsRefreshObservable = MutableStateFlow(false)

    fun setup(sdkConfig: SDKConfig,
              completion: () -> Unit) {

        if (this.configured) {
            Logger.log(LoggerEvents.CONFIG_ERROR)
            return
        }

        this.setBearer(sdkConfig.bearer)
        if (this.bearer.isEmpty()) { return }

        this.configured = true
        this.customerGuid = sdkConfig.customerGuid
        this.logTag = sdkConfig.logTag
        this.environment = sdkConfig.environment
        this.customer = sdkConfig.customer
        this.bank = sdkConfig.bank
        this.listener = sdkConfig.listener
        this.completion = completion
    }

    fun setBearer(bearer: String) {

        this.bearer = bearer
        if (this.bearer.isEmpty()) {
            this.invalidToken = true
            Logger.log(LoggerEvents.AUTH_ERROR)
        } else {
            this.invalidToken = false
            Logger.log(LoggerEvents.AUTH_SET)
        }
    }

    fun getOKHttpClient(): OkHttpClient.Builder {

        return OkHttpClient()
            .newBuilder()
            .addInterceptor(HttpBearerAuth("Bearer", this.bearer))
    }

    companion object {

        @Volatile
        private var instance: Cybrid? = null

        fun getInstance(): Cybrid {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = Cybrid()
                    }
                }
            }
            return instance!!
        }
    }
}