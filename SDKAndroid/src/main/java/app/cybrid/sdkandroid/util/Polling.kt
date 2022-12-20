package app.cybrid.sdkandroid.util

import android.os.Handler
import android.os.Looper
import androidx.core.os.postDelayed
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class Polling(runner: () -> Unit) {

    var updateInterval = 4L
    var handler: Handler = Handler(Looper.getMainLooper())
    var runnable: Runnable? = null
    var runner: () -> Unit
    var executor: ScheduledThreadPoolExecutor

    private var canceled = false

    init {

        this.runner = runner
        this.runnable = Runnable { runner.invoke() }
        this.executor = ScheduledThreadPoolExecutor(1)
        this.executor.scheduleWithFixedDelay(this.runnable, updateInterval, updateInterval, TimeUnit.SECONDS)
    }

    fun resume() {

        this.runnable = Runnable { runner.invoke() }
        this.executor = ScheduledThreadPoolExecutor(1)
        this.executor.scheduleWithFixedDelay(this.runnable, updateInterval, updateInterval, TimeUnit.SECONDS)
        this.canceled = false
    }

    fun stop() {

        this.executor.shutdownNow()
        this.runnable = null
        this.canceled = true
    }

    fun isCanceled(): Boolean {
        return this.canceled
    }
}