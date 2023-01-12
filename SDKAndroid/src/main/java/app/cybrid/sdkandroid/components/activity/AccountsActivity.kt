package app.cybrid.sdkandroid.components.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.AccountsView
import app.cybrid.sdkandroid.components.accounts.view.AccountsViewModel
import app.cybrid.sdkandroid.components.listprices.view.ListPricesViewModel

class AccountsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.accounts_activity)

        // -- Init the component
        val accountsView = findViewById<AccountsView>(R.id.accountsView)
        accountsView.updateInterval = 2000L

        // -- Get ViewModels
        val listPricesViewModel: ListPricesViewModel by viewModels()
        val accountsViewModel: AccountsViewModel by viewModels()

        // -- Adding ViewModels
        accountsView.setViewModels(
            listPricesViewModel = listPricesViewModel,
            accountsViewModel = accountsViewModel
        )
    }
}