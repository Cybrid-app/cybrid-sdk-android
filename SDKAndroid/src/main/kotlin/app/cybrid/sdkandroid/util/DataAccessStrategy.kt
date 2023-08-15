package app.cybrid.sdkandroid.util

import android.util.Log
import app.cybrid.sdkandroid.Cybrid
import org.json.JSONObject
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

            val errorBody = response.errorBody()?.string()
            if (!errorBody.isNullOrEmpty()) {
                val json = JSONObject(errorBody)
                val message_code = json.optString("message_code", "Unknown error")
                val errorStatus = json.optInt("status", response.code())

                return Resource.error(
                    message = message_code,
                    data = null,
                    code = errorStatus,
                    raw = response.raw()
                )
            } else {
                return Resource.error(
                    message = response.message(),
                    data = null,
                    code = response.code(),
                    raw = response.raw()
                )
            }
        }
    } catch (e: Exception) {
        Log.e(Cybrid.logTag, "ThrowsError: ${e.message}")
        return Resource.error("${e.message.toString()} - ${call.javaClass.name}")
    }
}