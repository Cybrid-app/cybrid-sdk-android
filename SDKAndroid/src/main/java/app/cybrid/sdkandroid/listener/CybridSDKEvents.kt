package app.cybrid.sdkandroid.listener

interface CybridSDKEvents {

    fun onTokenExpired()
    fun onEvent(level: Int, message:String)
}