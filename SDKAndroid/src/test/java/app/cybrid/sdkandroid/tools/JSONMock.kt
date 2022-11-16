package app.cybrid.sdkandroid.tools

import app.cybrid.sdkandroid.BuildConfig
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class JSONMock: Interceptor {

    private fun getResponse(): String {
        return ""
    }

    override fun intercept(chain: Interceptor.Chain): Response {

        if (BuildConfig.DEBUG) {

            val responseString = """[]"""
            val mediaType = "application/json; charset=utf-8".toMediaType()
            return chain.proceed(chain.request())
                .newBuilder()
                .code(200)
                .request(chain.request())
                .protocol(Protocol.HTTP_2)
                .body(responseString.toResponseBody(mediaType))
                .message(responseString)
                .addHeader("content-type", "application/json")
                .build()
        } else {
            throw IllegalAccessError("MockInterceptor is only meant for Testing Purposes and " +
                    "bound to be used only with DEBUG mode")
        }
    }
}