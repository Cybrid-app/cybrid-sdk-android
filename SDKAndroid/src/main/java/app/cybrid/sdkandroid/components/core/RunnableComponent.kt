package app.cybrid.sdkandroid.components.core

import android.os.Handler

abstract class RunnableComponent {

    var updateInterval = 5000L

    private var _handler: Handler? = null
    private var _runnable:Runnable? = null

     fun refresh() {}
}