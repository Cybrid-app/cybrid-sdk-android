package app.cybrid.demoapp.api.auth.service

import app.cybrid.demoapp.api.auth.entity.TokenRequest
import app.cybrid.demoapp.api.auth.entity.TokenResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AppService {

    @POST("oauth/token")
    fun getBearer(@Body request: TokenRequest) : Call<TokenResponse>
}