package app.cybrid.sdkandroid

import androidx.lifecycle.viewModelScope
import app.cybrid.cybrid_api_bank.client.auth.HttpBearerAuth
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.cybrid_api_bank.client.models.BankBankModel
import app.cybrid.cybrid_api_bank.client.models.CustomerBankModel
import app.cybrid.sdkandroid.components.core.CybridViewModel
import app.cybrid.sdkandroid.listener.CybridSDKEvents
import app.cybrid.sdkandroid.listener.CybridSDKListener
import app.cybrid.sdkandroid.util.Logger
import app.cybrid.sdkandroid.util.LoggerEvents
import app.cybrid.sdkandroid.util.getUSD
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

open class Cybrid {

    private var bearer: String = ""
    var customerGuid: String = ""

    var tag: String = "CybridSDK"
        private set
    var invalidToken = false
        private set
    var eventsListener: CybridSDKEvents? = null
    var readyListener: CybridSDKListener? = null
    var imagesUrl = "https://images.cybrid.xyz/sdk/assets/png/color/"
        private set
    var imagesSize = "@2x.png"
        private set
    var env = CybridEnv.SANDBOX
        private set

    var customer: CustomerBankModel? = null
        private set
    var bank: BankBankModel? = null
        private set
    var assets: List<AssetBankModel> = listOf()
    var fiat: AssetBankModel = getUSD()
        private set

    var accountsRefreshObservable = MutableStateFlow(false)

    fun setup(
        bearer: String = "",
        tag: String = "CybridSDK",
        env: CybridEnv,
        eventsListener: CybridSDKEvents,
        readyListener: CybridSDKListener,

    ) {

        this.bearer = bearer
        this.tag = tag
        this.env = env
        this.eventsListener = eventsListener
        this.readyListener = readyListener
        this.autoLoad()
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
        private var INSTANCE: Cybrid? = null

        fun getInstance(): Cybrid {

            return INSTANCE ?: synchronized(this) {
                val instance = Cybrid()
                INSTANCE = instance
                instance
            }
        }
    }

    // -- Autoload
    private fun autoLoad() {

        val viewModel = CybridViewModel()

        // -- Fetch the customer if its needed
        if (this.customer == null) {
            viewModel.viewModelScope.launch {
                customer = viewModel.fetchCustomer()
            }
        }

        // -- Fetch the bank if its needed
        if (this.bank == null) {
            viewModel.viewModelScope.launch {
                bank = viewModel.fetchBank(guid = customer?.bankGuid!!)
            }
        }

        // -- Fetch assets if its needed
        if (this.assets.isEmpty()) {
            viewModel.viewModelScope.launch {
                assets = viewModel.fetchAssets() ?: listOf()
            }
        }
        val defaultCode = this.bank?.supportedFiatAccountAssets!!.first()
        this.fiat = this.assets.first { it.code == defaultCode }

        // -- Ready
        readyListener?.onReady()
    }
}
enum class CybridEnv { STAGING, SANDBOX, PRODUCTION }