package app.cybrid.sdkandroid.listener

interface CybridSDKEvents {

    fun onSDKReady()
    fun onTokenExpired()
    fun onEvent(level: Int, message:String)
}