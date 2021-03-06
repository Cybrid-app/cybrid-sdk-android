package app.cybrid.sdkandroid.util

import android.util.Log
import app.cybrid.sdkandroid.Cybrid

enum class LoggerEvents(val level:Int, val message:String) {

    AUTH_SET(Log.INFO, "Setting auth token"),
    AUTH_EXPIRED(Log.ERROR, "Session is expired"),
    NETWORK_ERROR(Log.ERROR, "Network error"),
    COMPONENT_INIT(Log.INFO, "Initializing"),
    DATA_REFRESHED(Log.INFO, "Refreshing"),
    DATA_ERROR(Log.ERROR, "There was an error fetching")
}

object Logger {

    fun log(event:LoggerEvents) {

        Log.println(event.level, Cybrid.instance.tag, event.message)
    }

    fun log(event:LoggerEvents, data:String) {

        Log.println(event.level, Cybrid.instance.tag, event.message + ": " + data)
    }
}