package app.cybrid.sdkandroid

import app.cybrid.cybrid_api_bank.client.apis.AssetsApi
import app.cybrid.cybrid_api_bank.client.auth.HttpBearerAuth
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.cybrid_api_bank.client.models.AssetListBankModel
import app.cybrid.cybrid_api_bank.client.models.BankBankModel
import app.cybrid.cybrid_api_bank.client.models.CustomerBankModel
import app.cybrid.sdkandroid.components.BankAccountsView
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

object Cybrid {

    var logTag: String = "CybridSDK"
    var customerGuid: String = ""
        internal set
    var environment = CybridEnvironment.SANDBOX
        internal set
    var invalidToken = true
        internal set
    private var configured = false

    internal var bearer: String = ""
    internal var listener: CybridSDKEvents? = null
    internal var imagesUrl = "https://images.cybrid.xyz/sdk/assets/png/color/"
    internal var imagesSize = "@2x.png"
    internal var accountsRefreshObservable = MutableStateFlow(false)
    internal var assets: List<AssetBankModel> = emptyList()
    internal var customer: CustomerBankModel? = null
    internal var bank: BankBankModel? = null
    // -- fiat

    fun setup(sdkConfig: SDKConfig,
              completion: () -> Unit) {

        if (this.configured) {
            Logger.log(LoggerEvents.CONFIG_ERROR)
            return
        }

        this.setBearer(sdkConfig.bearer)
        this.customerGuid = sdkConfig.customerGuid
        this.logTag = sdkConfig.logTag
        this.environment = sdkConfig.environment
        this.customer = sdkConfig.customer
        this.bank = sdkConfig.bank
        this.listener = sdkConfig.listener
        this.autoLoad(completion)
        this.configured = true
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

    private fun autoLoad(completion: () -> Unit) {
        this.fetchAssets { completion.invoke() }
    }

    @OptIn(DelicateCoroutinesApi::class)
    internal fun fetchAssets(completion: () -> Unit) {

        val assetsApi = AppModule.getClient().createService(AssetsApi::class.java)
        GlobalScope.let { scope ->
            scope.launch {
                val assetsResponse = getResult {
                    assetsApi.listAssets(page = JavaBigDecimal(0), perPage = JavaBigDecimal(50))
                }
                if (assetsResponse.data != null) {
                    assets = assetsResponse.data.objects
                }
                completion()
            }
        }
    }

    fun getOKHttpClient(): OkHttpClient.Builder {

        return OkHttpClient()
            .newBuilder()
            .addInterceptor(HttpBearerAuth("Bearer", this.bearer))
    }

    internal fun reset() {

        this.bearer = ""
        this.customerGuid = ""
        this.logTag = "CybridSDK"
        this.environment = CybridEnvironment.SANDBOX
        this.customer = null
        this.bank = null
        this.listener = null
        this.assets = emptyList()
        this.configured = false

        this.invalidToken = true
        this.accountsRefreshObservable = MutableStateFlow(false)
    }

    fun isConfigured(): Boolean = this.configured
}