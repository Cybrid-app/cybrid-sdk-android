package app.cybrid.demoapp.api.auth.entity

import app.cybrid.demoapp.core.ScopeConstants
import com.google.gson.annotations.SerializedName

data class TokenRequest (

    @SerializedName("grant_type")
    var grant_type:String = "client_credentials",

    @SerializedName("client_id")
    var client_id:String,

    @SerializedName("client_secret")
    var client_secret:String,

    @SerializedName("scope")
    var scope:String = ScopeConstants.bankTokenScopes
)