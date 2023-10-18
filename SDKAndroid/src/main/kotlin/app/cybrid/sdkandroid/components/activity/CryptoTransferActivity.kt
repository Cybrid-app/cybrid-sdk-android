package app.cybrid.sdkandroid.components.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.CryptoTransferView
import app.cybrid.sdkandroid.components.cryptoTransfer.view.CryptoTransferViewModel

class CryptoTransferActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crypto_transfer)

        // -- Creating viewModel
        val cryptoTransferViewModel: CryptoTransferViewModel by viewModels()

        // -- Finding the view
        val cryptoTransferView: CryptoTransferView = findViewById(R.id.view)

        // -- Set ViewModel to the View
        cryptoTransferView.setViewModel(cryptoTransferViewModel)
    }
}