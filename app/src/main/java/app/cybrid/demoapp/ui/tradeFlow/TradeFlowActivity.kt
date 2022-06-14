package app.cybrid.demoapp.ui.tradeFlow

import  androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import app.cybrid.demoapp.R
import app.cybrid.sdkandroid.components.listprices.view.ListPricesViewModel
import app.cybrid.sdkandroid.flow.TradeFlow

class TradeFlowActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trade_flow)

        // --
        val viewModel: ListPricesViewModel by viewModels()

        // --
        val tradeFlow:TradeFlow = findViewById(R.id.tradeFlow)
        tradeFlow.setListPricesVideModel(viewModel)
    }
}