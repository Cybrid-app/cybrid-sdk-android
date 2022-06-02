package app.cybrid.sdkandroid.components

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.core.RunnableComponent
import app.cybrid.sdkandroid.components.listprices.adapter.ListPricesAssetsAdapter
import app.cybrid.sdkandroid.components.listprices.view.ListPricesViewModel

class AssetsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(context, attrs, defStyle) {

    var updateInterval = 5000L

    private var _viewModel: ListPricesViewModel? = null
    private var _handler: Handler? = null
    private var _runnable:Runnable? = null

    private var adapter: ListPricesAssetsAdapter? = null
    private var list: RecyclerView? = null

    init {

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.list_prices_assets_component, this, true)

        list = findViewById(R.id.list_prices_assets)
    }

    fun setViewModel(viewModel: ListPricesViewModel) {

        _viewModel = viewModel
        _viewModel?.getListPrices()

        adapter = ListPricesAssetsAdapter(_viewModel?.prices!!, _viewModel?.assets!!)
        list?.adapter = adapter

        _handler = Handler(Looper.getMainLooper())
        _runnable = Runnable { this.refreshPrices() }
        _handler?.postDelayed(_runnable!!, updateInterval)
    }

    private fun refreshPrices() {

        Log.d(Cybrid.instance.tag, "Asset Component: Updating prices")
        _viewModel?.getListPrices()
        _viewModel.let {

            it?.getListPrices()
            adapter?.reload(it?.prices!!)
            adapter?.notifyDataSetChanged()
        }
        _handler.let {
            _runnable.let { _it ->
                it?.postDelayed(_it!!, updateInterval)
            }
        }
    }
}