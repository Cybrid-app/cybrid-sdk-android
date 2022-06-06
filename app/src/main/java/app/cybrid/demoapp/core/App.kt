package app.cybrid.demoapp.core

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.cybrid.demoapp.BuildConfig
import app.cybrid.demoapp.api.Util
import app.cybrid.demoapp.api.auth.entity.TokenRequest
import app.cybrid.demoapp.api.auth.entity.TokenResponse
import app.cybrid.demoapp.api.auth.service.AppService
import app.cybrid.demoapp.listener.BearerListener
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.listener.CybridSDKEvents
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class App : Application(), CybridSDKEvents {

    private val tokenRequest = TokenRequest(
        client_id = BuildConfig.CLIENT_ID,
        client_secret = BuildConfig.CLIENT_SECRET
    )

    override fun onCreate() {

        super.onCreate()
        setupCybridSDK()
    }

    fun setupCybridSDK() {

        Cybrid.instance.listener = this
    }

    override fun onTokenExpired() {

        Log.d(TAG, "onBearerExpired")
        this.getBearer()
    }

    // -- Helper method to get the bearer
    fun getBearer(listener: BearerListener? = null) {

        val tokenService = Util.getClient().create(AppService::class.java)
        tokenService.getBearer(this.tokenRequest).enqueue(object : Callback<TokenResponse> {
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

        const val demoUrl = "https://id.demo.cybrid.app"
        const val TAG = "CybridSDKDemo"

        fun createVerticalRecyclerList(list: RecyclerView, context: Context?) {

            val layout = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            list.recycledViewPool.setMaxRecycledViews(0, 10)
            list.layoutManager = layout
            list.setItemViewCacheSize(10)
            list.isDrawingCacheEnabled = true
            list.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        }

        fun createRecyclerHorizontalList(list: RecyclerView, context: Context?) {

            val layout = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            list.recycledViewPool.setMaxRecycledViews(0, 10)
            list.layoutManager = layout
            list.setItemViewCacheSize(10)
            list.isDrawingCacheEnabled = true
            list.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        }

    }
}