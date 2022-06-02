package app.cybrid.sdkandroid

import app.cybrid.cybrid_api_bank.client.infrastructure.ApiClient

object AppModule {

    fun getClient() : ApiClient {

        val clientBuilder = Cybrid.instance.let { it.getOKHttpClient() }
        return ApiClient(okHttpClientBuilder = clientBuilder)
    }
}