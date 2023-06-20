package app.cybrid.demoapp.core

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.cybrid.cybrid_api_bank.client.models.BankBankModel
import app.cybrid.cybrid_api_bank.client.models.CustomerBankModel
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class App : Application(), CybridSDKEvents {

    private val _sdkConfig = SDKConfig(
        environment = demonEnv
    )
    private val tokenRequest = TokenRequest(
        client_id = BuildConfig.CLIENT_ID,
        client_secret = BuildConfig.CLIENT_SECRET
    )

    override fun onCreate() {

        super.onCreate()
        context = applicationContext
    }

    fun getSDKConfig(request: TokenRequest? = null, customerGuid: String, completion: (SDKConfig) -> Unit) {

        this._sdkConfig.customerGuid = customerGuid
        val tokenService = Util.getIdpClient().create(AppService::class.java)
        val token = request ?: this.tokenRequest
        tokenService.getBearer(token).enqueue(object : Callback<TokenResponse> {
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

    fun getCustomerToken(sdkConfig: SDKConfig, bankBearer: String, completion: (SDKConfig) -> Unit) {

        val tokenService = Util.getIdpClient().create(AppService::class.java)
        val token = request ?: this.tokenRequest
        tokenService.getBearer(token).enqueue(object : Callback<TokenResponse> {
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



    fun setupCybridSDK(customerGuid: String, customer: CustomerBankModel, bank: BankBankModel) {

        val sdkConfig = SDKConfig(
            environment = demonEnv,
            customerGuid = if (customerGuid.isEmpty()) customerGuid else BuildConfig.CUSTOMER_GUID,
            customer = customer,
            bank = bank,
            listener = this
        )
        Cybrid.getInstance().setup(sdkConfig = sdkConfig) {

        }
    }

    override fun onTokenExpired() {

        Log.d(TAG, "onBearerExpired")
        this.getBearer()
    }

    override fun onEvent(level: Int, message: String) {

        if (level == Log.ERROR && context != null) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    // -- Helper method to get the bearer
    fun getBearer(listener: BearerListener? = null, request: TokenRequest? = null) {

        val tokenService = Util.getIdpClient().create(AppService::class.java)
        val token = request ?: this.tokenRequest
        tokenService.getBearer(token).enqueue(object : Callback<TokenResponse> {
            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {

                if (response.isSuccessful) {

                    val tokenResponse:TokenResponse = response.body()!!
                    tokenResponse.let {

                        Log.d(TAG, "Bearer: " + it.accessToken)
                        Cybrid.instance.setBearer(it.accessToken)
                        listener?.onBearerReady()
                    }
                } else {

                    Log.d(TAG, "Error: " + response.raw())
                    listener?.onBearerError()
                }
            }

            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {

                Log.d(TAG, "Error getting bearer token: " + t.message)
                listener?.onBearerError()
            }
        })
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