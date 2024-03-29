package app.cybrid.sdkandroid.util

import android.util.Log
import app.cybrid.sdkandroid.Cybrid

enum class LoggerEvents(val level:Int, val message:String) {

    AUTH_SET(Log.INFO, "Setting auth token"),
    AUTH_ERROR(Log.ERROR, "Please set a valid token"),
    AUTH_EXPIRED(Log.ERROR, "Session is expired"),
    NETWORK_ERROR(Log.ERROR, "Network error"),
    COMPONENT_INIT(Log.INFO, "Initializing"),
    DATA_FETCHED(Log.INFO, "Data Fetched"),
    DATA_REFRESHED(Log.INFO, "Refreshing"),
    DATA_ERROR(Log.ERROR, "There was an error fetching"),
    CONFIG_ERROR(Log.ERROR, "You can only configure the SDK once"),
    ERROR(Log.ERROR, "Error")
}

object Logger {

    fun log(event: LoggerEvents) {

        Log.println(event.level, Cybrid.logTag, event.message)
        Cybrid.let {
            it.listener?.onEvent(event.level, event.message)
        }
    }

    fun log(event: LoggerEvents, data:String) {

        Log.println(event.level, Cybrid.logTag, event.message + ": " + data)
        Cybrid.let {
            it.listener?.onEvent(event.level, event.message + ": " + data)
        }
    }
}