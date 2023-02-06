package app.cybrid.demoapp.ui.listPricesComponent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import app.cybrid.demoapp.R
import app.cybrid.sdkandroid.components.ListPricesView
import app.cybrid.sdkandroid.components.listprices.view.ListPricesViewModel

class ListPricesAssetsComponentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_prices_assets_component)

        val viewModel: ListPricesViewModel by viewModels()

        // -- List
        val cryptoList = findViewById<ListPricesView>(R.id.list1)
        cryptoList.setViewModel(viewModel)
        cryptoList.updateInterval = 2000L
    }
}