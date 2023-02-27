package app.cybrid.sdkandroid

import app.cybrid.cybrid_api_bank.client.infrastructure.ApiClient

object AppModule {

    fun getClient(): ApiClient {

        val clientBuilder = Cybrid.instance.getOKHttpClient()
        return ApiClient(
            baseUrl = getApiUrl(),
            okHttpClientBuilder = clientBuilder
        )
    }

    private fun getApiUrl() : String {

        val envURL = when(Cybrid.instance.env) {

            CybridEnv.STAGING -> "https://bank.staging.cybrid.app"
            CybridEnv.SANDBOX -> "https://bank.sandbox.cybrid.app"
            CybridEnv.PRODUCTION -> "https://bank.production.cybrid.app"
        }
        return envURL
    }
}