package app.cybrid.sdkandroid.tools

import app.cybrid.sdkandroid.BuildConfig
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull

class ErrorMockInterceptor(code: Int) : Interceptor {

    private var responseCode = code

    override fun intercept(chain: Interceptor.Chain): Response {

        if (BuildConfig.DEBUG) {

            return chain.proceed(chain.request())
                .newBuilder()
                .code(403)
                .request(chain.request())
                .protocol(Protocol.HTTP_2)
                .build()
        } else {
            throw IllegalAccessError("MockInterceptor is only meant for Testing Purposes and " +
                    "bound to be used only with DEBUG mode")
        }
    }
}