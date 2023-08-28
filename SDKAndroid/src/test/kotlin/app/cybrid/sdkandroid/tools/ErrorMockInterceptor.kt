package app.cybrid.sdkandroid.tools

import app.cybrid.sdkandroid.BuildConfig
import okhttp3.*

class ErrorMockInterceptor(code: Int) : Interceptor {

    private var responseCode = code

    override fun intercept(chain: Interceptor.Chain): Response {

        if (BuildConfig.DEBUG) {

            return chain.proceed(chain.request())
                .newBuilder()
                .code(responseCode)
                .request(chain.request())
                .protocol(Protocol.HTTP_2)
                .build()
        } else {
            throw IllegalAccessError("MockInterceptor is only meant for Testing Purposes and " +
                    "bound to be used only with DEBUG mode")
        }
    }
}