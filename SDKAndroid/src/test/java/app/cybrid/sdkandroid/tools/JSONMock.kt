package app.cybrid.sdkandroid.tools

import app.cybrid.sdkandroid.BuildConfig
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody

class JSONMock(state: JSONMockState): Interceptor {

    enum class JSONMockState { SUCCESS, EMPTY, ERROR }
    private var state:JSONMockState = state

    override fun intercept(chain: Interceptor.Chain): Response {

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
    }

    private fun getResponse(request: Request): String {

        var response = """[]"""
        val method = request.method
        val divider = "://"
        val toReplace = "${request.url.scheme}${divider}${request.url.host}/api/"

        var endpoint = request.url.toUri().toString().replace(toReplace, "")
        if (endpoint.contains("?")) { endpoint = endpoint.split("?")[0] }

        var fetchSingle = true
        val endpointParts = endpoint.split("/")
        if (endpointParts.size > 1) {
            endpoint = endpointParts[0]
            fetchSingle = false
        }

        when (endpoint) {

            "customers" -> {
                when (method) {
                    "POST" -> {
                        when (state) {
                            JSONMockState.SUCCESS -> { response = TestConstants.CREATE_CUSTOMER_SUCCESS }
                        }
                    }
                    "GET" -> {
                        when (state) {
                            JSONMockState.SUCCESS -> { response = TestConstants.FETCH_CUSTOMER_SUCCESS }
                        }
                    }
                }
            }

            "identity_verifications" -> {
                when (method) {
                    "GET" -> {
                        when(state) {
                            JSONMockState.SUCCESS -> {
                                response = if (fetchSingle) {
                                    TestConstants.FETCH_LIST_IDENTITY_VERIFICATIONS_SUCCESS
                                } else {
                                    TestConstants.FETCH_IDENTITY_VERIFICATION_SUCCESS
                                }
                            }
                            JSONMockState.EMPTY -> {
                                response = if (fetchSingle) {
                                    TestConstants.FETCH_LIST_IDENTITY_VERIFICATIONS_SUCCESS_EMPTY
                                } else {
                                    TestConstants.FETCH_IDENTITY_VERIFICATION_SUCCESS
                                }
                            }
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