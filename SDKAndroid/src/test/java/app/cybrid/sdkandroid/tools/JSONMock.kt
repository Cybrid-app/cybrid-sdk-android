package app.cybrid.sdkandroid.tools

import app.cybrid.sdkandroid.BuildConfig
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody

class JSONMock(state: JSONMockState): Interceptor {

    enum class JSONMockState { SUCCESS, EMPTY, ERROR }
    private var state:JSONMockState = state

    override fun intercept(chain: Interceptor.Chain): Response {

        if (BuildConfig.DEBUG) {

            if (state == JSONMockState.ERROR) {

                return chain.proceed(chain.request())
                    .newBuilder()
                    .code(403)
                    .request(chain.request())
                    .protocol(Protocol.HTTP_2)
                    .build()

            } else {

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
            }
        } else {
            throw IllegalAccessError("MockInterceptor is only meant for Testing Purposes and " +
                    "bound to be used only with DEBUG mode")
        }
    }

    private fun getResponse(request: Request): String {

        var response = """[]"""
        val method = request.method
        val divider = "://"
        val toReplace = "${request.url.scheme}${divider}${request.url.host}/api/"

        var endpoint = request.url.toUri().toString().replace(toReplace, "")
        if (endpoint.contains("?")) { endpoint = endpoint.split("?")[0] }

        //val fetchSingle = if (endpoint.contains())

        when (endpoint) {

            "customers" -> {
                when (method) {
                    "POST" -> {
                        when (state) {
                            JSONMockState.SUCCESS -> { response = TestConstants.CREATE_CUSTOMER_SUCCESS }
                        }
                    }
                }
            }

            "identity_verifications" -> {
                when (method) {
                    "GET" -> {
                        when(state) {
                            JSONMockState.SUCCESS -> { response = TestConstants.FETCH_IDENTITY_VERIFICATIONS_SUCCESS }
                            JSONMockState.EMPTY -> { response = TestConstants.FETCH_IDENTITY_VERIFICATIONS_SUCCESS_EMPTY }
                        }
                    }
                    "POST" -> {
                        when(state) {
                            JSONMockState.SUCCESS -> { response = TestConstants.CREATE_IDENTITY_VERIFICATION_SUCCESS }
                        }
                    }
                }
            }
        }
        return response
    }
}