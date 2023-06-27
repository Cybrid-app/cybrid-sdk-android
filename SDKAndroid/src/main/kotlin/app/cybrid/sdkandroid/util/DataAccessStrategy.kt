package app.cybrid.sdkandroid.util

import android.util.Log
import app.cybrid.sdkandroid.Cybrid
import retrofit2.Response
import java.net.HttpURLConnection.HTTP_FORBIDDEN
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED

suspend fun <T> getResult(call: suspend() -> Response<T>): Resource<T> {

    try {

        val cybrid = Cybrid
        val response = call.invoke()
        val body = response.body()
        val code = response.code()

        if (response.isSuccessful) {

            Log.d(cybrid.logTag, "Data: ${response.code()} - ${response.body()}")
            return Resource.success(body!!, code)

        } else if (response.code() == HTTP_UNAUTHORIZED || response.code() == HTTP_FORBIDDEN) {

            cybrid.let { cybridInstance ->
                cybridInstance.listener.let { listener ->
                    cybridInstance.invalidToken = true
                    listener?.onTokenExpired()
                }
            }
            Logger.log(LoggerEvents.AUTH_EXPIRED, "${response.code()} - ${response.message()}")
            return Resource.error(response.message(), code = response.code())
        } else {
            return Resource.error(
                message = response.message(),
                data = response.body(),
                code = response.code(),
                raw = response.raw()
            )
        }
    } catch (e: Exception) {
        Log.e(Cybrid.logTag, "ThrowsError: ${e.message}")
        return Resource.error("${e.message.toString()} - ${call.javaClass.name}")
    }
}