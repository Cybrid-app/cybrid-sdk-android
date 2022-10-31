package app.cybrid.sdkandroid.util

import android.os.Handler
import android.os.Looper

class Polling(runner: () -> Unit) {

    var updateInterval = 4000L
    var handler: Handler = Handler(Looper.getMainLooper())
    var runnable: Runnable?
    var runner: () -> Unit

    init {

        this.runner = runner
        runnable = Runnable {
            runner.invoke()
        }
        handler.postDelayed(runnable!!, updateInterval)
    }

    fun stop() {

        runnable = null
    }
}