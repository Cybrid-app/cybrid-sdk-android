package app.cybrid.demoapp.api.auth.entity

import com.google.gson.annotations.SerializedName

data class TokenRequest (

    @SerializedName("grant_type")
    var grant_type:String = "client_credentials",

    @SerializedName("client_id")
    var client_id:String,

    @SerializedName("client_secret")
    var client_secret:String,

    @SerializedName("scope")
    var scope:String = "banks:read banks:write accounts:read accounts:execute customers:read customers:write customers:execute prices:read quotes:execute trades:execute trades:read workflows:execute workflows:read external_bank_accounts:execute external_bank_accounts:read transfers:read transfers:execute"
)