package app.cybrid.demoapp.core

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.cybrid.cybrid_api_bank.client.apis.BanksApi
import app.cybrid.cybrid_api_bank.client.apis.CustomersApi
import app.cybrid.cybrid_api_bank.client.models.BankBankModel
import app.cybrid.cybrid_api_bank.client.models.CustomerBankModel
import app.cybrid.cybrid_api_id.client.apis.CustomerTokensApi
import app.cybrid.cybrid_api_id.client.models.PostCustomerTokenIdpModel
import app.cybrid.demoapp.BuildConfig
import app.cybrid.demoapp.api.Util
import app.cybrid.demoapp.api.auth.entity.TokenRequest
import app.cybrid.demoapp.api.auth.entity.TokenResponse
import app.cybrid.demoapp.api.auth.service.AppService
import app.cybrid.demoapp.listener.BearerListener
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.core.CybridEnvironment
import app.cybrid.sdkandroid.core.SDKConfig
import app.cybrid.sdkandroid.listener.CybridSDKEvents
import app.cybrid.sdkandroid.util.getResult
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class App : Application(), CybridSDKEvents {

    private val _sdkConfig = SDKConfig(
        environment = demonEnv
    )

    override fun onCreate() {

        super.onCreate()
        context = applicationContext
    }

    fun getSDKConfig(request: TokenRequest, customerGuid: String, completion: (SDKConfig) -> Unit) {

        this._sdkConfig.customerGuid = customerGuid
        val tokenService = Util.getIdpSimpleClient().create(AppService::class.java)
        tokenService.getBearer(request).enqueue(object : Callback<TokenResponse> {
            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {

                if (response.isSuccessful) {

                    val tokenResponse:TokenResponse = response.body()!!
                    tokenResponse.let {

                        Log.d(TAG, "Bank Bearer: " + it.accessToken)
                        val bankBearer = it.accessToken
                        getCustomerToken(
                            sdkConfig = _sdkConfig,
                            bankBearer = bankBearer,
                            completion = completion
                        )
                    }
                } else {
                    Log.d(TAG, "Error: " + response.raw())
                }
            }

            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                Log.d(TAG, "Error getting bearer token: " + t.message)
            }
        })
    }

    @OptIn(DelicateCoroutinesApi::class)
    internal fun getCustomerToken(sdkConfig: SDKConfig, bankBearer: String, completion: (SDKConfig) -> Unit) {

        val idpApi = Util.getIdpClient(bankBearer).create(CustomerTokensApi::class.java)
        val postCustomerToken = PostCustomerTokenIdpModel(
            customerGuid = sdkConfig.customerGuid,
            scopes = ScopeConstants.customerTokenScopes
        )
        GlobalScope.let { scope ->
            scope.launch {
                val tokenResult = getResult {
                    idpApi.createCustomerToken(postCustomerToken)
                }
                sdkConfig.bearer = tokenResult.data?.accessToken ?: ""
                getCustomer(sdkConfig, bankBearer, completion)
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    internal fun getCustomer(sdkConfig: SDKConfig, bankBearer: String, completion: (SDKConfig) -> Unit) {

        val customerApi = Util.getBankClient(bankBearer).create(CustomersApi::class.java)
        GlobalScope.let { scope ->
            scope.launch {
                val customerResult = getResult {
                    customerApi.getCustomer(customerGuid = sdkConfig.customerGuid)
                }
                sdkConfig.customer = customerResult.data
                getBank(sdkConfig, bankBearer, completion)
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    internal fun getBank(sdkConfig: SDKConfig, bankBearer: String, completion: (SDKConfig) -> Unit) {

        val bankApi = Util.getBankClient(bankBearer).create(BanksApi::class.java)
        GlobalScope.let { scope ->
            scope.launch {
                val banksResult = getResult {
                    bankApi.getBank(sdkConfig.customer?.bankGuid ?: "")
                }
                sdkConfig.bank = banksResult.data
                completion.invoke(sdkConfig)
            }
        }
    }

    override fun onTokenExpired() {

        Log.d(TAG, "onBearerExpired")
    }

    override fun onEvent(level: Int, message: String) {

        if (level == Log.ERROR && context != null) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    companion object {

        private val demonEnv = CybridEnvironment.SANDBOX
        val baseBankApiUrl = "https://bank.${demonEnv.name.lowercase()}.cybrid.app"
        val baseIdpApiUrl = "https://id.${demonEnv.name.lowercase()}.cybrid.app"
        const val TAG = "CybridSDKDemo"

        @SuppressLint("StaticFieldLeak")
        var context: Context? = null

        fun createVerticalRecyclerList(list: RecyclerView, context: Context?) {

            val layout = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            list.recycledViewPool.setMaxRecycledViews(0, 10)
            list.layoutManager = layout
            list.setItemViewCacheSize(10)
        }
    }
}