package app.cybrid.demoapp.ui.listPricesComponent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import app.cybrid.demoapp.R
import app.cybrid.sdkandroid.components.ListPricesView
import app.cybrid.sdkandroid.components.listprices.view.ListPricesViewModel

class ListPricesComponentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel: ListPricesViewModel by viewModels()
        val cryptoList = findViewById<ListPricesView>(R.id.composeView)
        cryptoList.setViewModel(viewModel)
        cryptoList.updateInterval = 2000L
    }
}