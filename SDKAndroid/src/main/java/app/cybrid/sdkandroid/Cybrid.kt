package app.cybrid.sdkandroid

import android.database.Observable
import app.cybrid.cybrid_api_bank.client.auth.HttpBearerAuth
import app.cybrid.sdkandroid.listener.CybridSDKEvents
import app.cybrid.sdkandroid.util.Logger
import app.cybrid.sdkandroid.util.LoggerEvents
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient

open class Cybrid {

    private var bearer: String = ""
    var customerGuid: String = ""

    var tag: String = "CybridSDK"
    var invalidToken = false
    var listener: CybridSDKEvents? = null

    var accountsRefreshObservable = MutableStateFlow(false)

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
        val instance = Cybrid()
    }
}