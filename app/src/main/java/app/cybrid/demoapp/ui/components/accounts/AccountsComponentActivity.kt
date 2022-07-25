package app.cybrid.demoapp.ui.components.accounts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import app.cybrid.demoapp.R
import app.cybrid.sdkandroid.components.AccountsView
import app.cybrid.sdkandroid.components.accounts.view.AccountsViewModel
import app.cybrid.sdkandroid.components.listprices.view.ListPricesViewModel

class AccountsComponentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accounts_component)

        // -- Init the component
        val accountsView = findViewById<AccountsView>(R.id.accountsView)

        // -- Get ViewModels
        val listPricesViewModel:ListPricesViewModel by viewModels()
        val accountsViewModel:AccountsViewModel by viewModels()

        // -- Adding ViewModels
        accountsView.setViewModels(
            listPricesViewModel = listPricesViewModel,
            accountsViewModel = accountsViewModel
        )
    }
}