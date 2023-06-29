package app.cybrid.sdkandroid

import app.cybrid.cybrid_api_bank.client.infrastructure.ApiClient

object AppModule {

    private const val baseUrl = "https://bank.%s.cybrid.app"

    fun getClient(): ApiClient {

        val clientBuilder = Cybrid.getOKHttpClient()
        return ApiClient(
            baseUrl = getApiUrl(),
            okHttpClientBuilder = clientBuilder
        )
    }

    internal fun getApiUrl(): String {
        return String.format(baseUrl, Cybrid.environment.name.lowercase())
    }
}