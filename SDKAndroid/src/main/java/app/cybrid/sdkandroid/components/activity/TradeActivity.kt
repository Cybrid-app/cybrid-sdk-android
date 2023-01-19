package app.cybrid.sdkandroid.components.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.TradeView
import app.cybrid.sdkandroid.components.listprices.view.ListPricesViewModel
import app.cybrid.sdkandroid.components.trade.view.TradeViewModel

class TradeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.trade_activity)

        // -- Creating ViewModel's
        val tradeViewModel: TradeViewModel by viewModels()
        val listPricesViewModel: ListPricesViewModel by viewModels()
        tradeViewModel.listPricesViewModel = listPricesViewModel

        // -- Getting the view
        val tradeView = findViewById<TradeView>(R.id.component)

        // -- Set viewModel to View
        tradeView.setViewModel(tradeViewModel)
    }
}