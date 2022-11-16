package app.cybrid.sdkandroid.tools

import app.cybrid.sdkandroid.BuildConfig
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody

class JSONMock: Interceptor {

    private fun getResponse(request: Request): String {

        val method = request.method
        val divider = "://"
        val toReplace = "${request.url.scheme}${divider}${request.url.host}/api/"
        val endpoint = request.url.toUri().toString().replace(toReplace, "")
        var response = """[]"""

        when (endpoint) {

            "customers" -> {

                when (method) {

                    "POST" -> {}
                }
            }
        }
        return response
    }

    override fun intercept(chain: Interceptor.Chain): Response {

        if (BuildConfig.DEBUG) {

            val response = getResponse(chain.request())
            val mediaType = "application/json; charset=utf-8".toMediaType()
            return chain.proceed(chain.request())
                .newBuilder()
                .code(200)
                .request(chain.request())
                .protocol(Protocol.HTTP_2)
                .body(response.toResponseBody(mediaType))
                .message(response)
                .addHeader("content-type", "application/json")
                .build()
        } else {
            throw IllegalAccessError("MockInterceptor is only meant for Testing Purposes and " +
                    "bound to be used only with DEBUG mode")
        }
    }
}