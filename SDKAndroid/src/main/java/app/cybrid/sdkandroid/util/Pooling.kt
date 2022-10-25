package app.cybrid.sdkandroid.util

import android.os.Handler
import android.os.Looper

class Pooling(runner: () -> Unit) {

    var updateInterval = 4000L
    private var handler: Handler? = Handler(Looper.getMainLooper())
    private var runnable: Runnable?
    private var runner: () -> Unit

    init {

        this.runner = runner
        runnable = Runnable {
            runner.invoke()
        }
        handler?.postDelayed(runnable!!, updateInterval)
    }

    fun stop() {

        handler?.removeCallbacks(this.runner)
        handler?.removeCallbacksAndMessages(null)
        handler = null
        runnable = null
    }
}