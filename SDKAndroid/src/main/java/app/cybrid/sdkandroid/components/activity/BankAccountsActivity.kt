package app.cybrid.sdkandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import app.cybrid.sdkandroid.components.BankAccountsView
import app.cybrid.sdkandroid.components.bankAccounts.view.BankAccountsViewModel

class BankAccountsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_accounts)

        val bankAccountViewModel: BankAccountsViewModel by viewModels()
        val bankAccountView = findViewById<BankAccountsView>(R.id.bankAccountView)

        bankAccountView.setViewModel(bankAccountViewModel)
    }
}