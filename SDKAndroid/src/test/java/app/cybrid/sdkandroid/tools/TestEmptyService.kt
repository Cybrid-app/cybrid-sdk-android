package app.cybrid.sdkandroid.tools

import retrofit2.Response

/**
 * Test Interface for test an error in RetrofitClient
 * The interface it build over Retrofit Response class
 * **/
interface TestEmptyService {

    /**
     * Suspend function (Coroutines)
     * Depends from Response Class
     * The function has missing HTTP method "GET" or "POST"
     * This gonna throw an error over DataAccessStrategy
     * **/
    suspend fun getNothing() : Response<String>
}