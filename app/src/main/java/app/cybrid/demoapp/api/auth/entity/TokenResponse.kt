package app.cybrid.demoapp.api.auth.entity

import com.google.gson.annotations.SerializedName

data class TokenResponse (

    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("token_type")
    val tokenType: String,

    @SerializedName("expires_in")
    val expiresIn: Int,

    @SerializedName("scope")
    val scope: String,

    @SerializedName("created_at")
    val createdAt: Int
)