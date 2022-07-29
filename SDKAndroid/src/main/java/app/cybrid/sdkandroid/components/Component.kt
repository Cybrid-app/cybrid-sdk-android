package app.cybrid.sdkandroid.components

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import androidx.compose.ui.platform.ComposeView
import androidx.constraintlayout.widget.ConstraintLayout

open class Component @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    var updateInterval = 5000L

    private var _handler: Handler? = null
    private var _runnable: Runnable? = null
    protected var composeView:ComposeView? = null
    var isUpdating = false

    fun setupRunnable(runner: () -> Unit) {

        _handler = Handler(Looper.getMainLooper())
        _runnable = Runnable {

            isUpdating = true
            runner.invoke()
            _handler.let {
                _runnable.let { _it ->
                    it?.postDelayed(_it!!, updateInterval)
                }
            }
        }
        _handler?.postDelayed(_runnable!!, updateInterval)
    }
}