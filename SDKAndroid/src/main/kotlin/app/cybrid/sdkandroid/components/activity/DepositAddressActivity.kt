package app.cybrid.sdkandroid.components.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.AccountsView
import app.cybrid.sdkandroid.components.DepositAddressView
import app.cybrid.sdkandroid.components.accounts.view.AccountsViewModel
import app.cybrid.sdkandroid.components.listprices.view.ListPricesViewModel
import app.cybrid.sdkandroid.components.transfer.view.TransferViewModel

class DepositAddressActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deposit_address)

        // -- Init the component
        val depositAddressView = findViewById<DepositAddressView>(R.id.depositAddressView)
        //accountsView.updateInterval = 2000L

        // -- Get ViewModels
        //val listPricesViewModel: ListPricesViewModel by viewModels()

        // -- Adding ViewModels
        depositAddressView.setViewModel()
    }
}