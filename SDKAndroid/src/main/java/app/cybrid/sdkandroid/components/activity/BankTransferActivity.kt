package app.cybrid.sdkandroid.components.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.BankTransferView
import app.cybrid.sdkandroid.components.bankTransfer.view.BankTransferViewModel

class BankTransferActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_transfer)

        val bankTransferViewModel: BankTransferViewModel by viewModels()
        val bankTransferView = findViewById<BankTransferView>(R.id.component)

        bankTransferView.setViewModel(bankTransferViewModel)
    }
}