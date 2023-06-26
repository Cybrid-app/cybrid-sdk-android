package app.cybrid.sdkandroid

import app.cybrid.cybrid_api_bank.client.apis.AssetsApi
import app.cybrid.cybrid_api_bank.client.auth.HttpBearerAuth
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.cybrid_api_bank.client.models.AssetListBankModel
import app.cybrid.cybrid_api_bank.client.models.BankBankModel
import app.cybrid.cybrid_api_bank.client.models.CustomerBankModel
import app.cybrid.sdkandroid.core.CybridEnvironment
import app.cybrid.sdkandroid.core.SDKConfig
import app.cybrid.sdkandroid.listener.CybridSDKEvents
import app.cybrid.sdkandroid.util.Logger
import app.cybrid.sdkandroid.util.LoggerEvents
import app.cybrid.sdkandroid.util.getResult
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.math.BigDecimal as JavaBigDecimal

open class Cybrid {

    internal var configured: Boolean = false
    internal var bearer: String = ""
    var logTag: String = "CybridSDK"
    var customerGuid: String = ""
        private set
    var environment = CybridEnvironment.SANDBOX
    var listener: CybridSDKEvents? = null
        private set

    var invalidToken = false
    internal var imagesUrl = "https://images.cybrid.xyz/sdk/assets/png/color/"
        private set
    internal var imagesSize = "@2x.png"
        private set
    internal var accountsRefreshObservable = MutableStateFlow(false)

    // -- Properties for AutoLoad
    // -- fiat
    var customer: CustomerBankModel? = null
        private set
    var bank: BankBankModel? = null
        private set
    var assets: List<AssetBankModel> = emptyList()
    var autoLoadComplete = false
        private set
    var completion: (() -> Unit)? = null
        private set

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
        this.autoLoad()
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

    private fun autoLoad() {

        // -- Fetch assets
        this.fetchAssets {

            this.autoLoadComplete = true
            this.completion?.invoke()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    internal fun fetchAssets(completion: () -> Unit) {

        val assetsApi = AppModule.getClient().createService(AssetsApi::class.java)
        GlobalScope.let { scope ->
            scope.launch {
                val assetsResponse = getResult {
                    assetsApi.listAssets(page = JavaBigDecimal(0), perPage = JavaBigDecimal(50))
                }
                assets = assetsResponse.data?.objects!!
                // -- fiat calculate
                completion()
            }
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

        fun resetInstance() {
            instance = null
        }
    }
}