package app.cybrid.sdkandroid

import app.cybrid.cybrid_api_bank.client.auth.HttpBearerAuth
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
    var customerGuid: String = ""
        private set
    var tag: String = "CybridSDK"
    var env = CybridEnvironment.SANDBOX
        private set

    var invalidToken = false
        private set
    var imagesUrl = "https://images.cybrid.xyz/sdk/assets/png/color/"
        private set
    var imagesSize = "@2x.png"
        private set

    var listener: CybridSDKEvents? = null
    var accountsRefreshObservable = MutableStateFlow(false)

    fun setup(sdkConfig: SDKConfig,
              completion: () -> Unit) {

        this.bearer = sdkConfig.bearer
        this.customerGuid = sdkConfig.customerGuid
    }

    fun setBearer(bearer: String) {

        this.bearer = bearer
        this.invalidToken = false
        Logger.log(LoggerEvents.AUTH_SET)
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