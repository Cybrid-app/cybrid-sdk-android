package app.cybrid.sdkandroid.util

import android.os.Handler
import android.os.Looper
import androidx.core.os.postDelayed
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class Polling(
    initialDelay: Long = 4L,
    runner: () -> Unit
) {

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
        this.executor.scheduleWithFixedDelay(this.runnable, initialDelay, updateInterval, TimeUnit.SECONDS)
    }

    fun stop() {

        this.executor.shutdownNow()
        this.runnable = null
        this.canceled = true
    }
}