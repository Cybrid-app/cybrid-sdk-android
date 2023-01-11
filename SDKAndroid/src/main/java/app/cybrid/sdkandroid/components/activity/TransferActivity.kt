package app.cybrid.sdkandroid.components.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.TransferView
import app.cybrid.sdkandroid.components.transfer.view.TransferViewModel

class TransferActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.transfer_activity)

        val transferViewModel: TransferViewModel by viewModels()
        val transferView = findViewById<TransferView>(R.id.component)

        transferView.setViewModel(transferViewModel)
        transferView.canDismissView = true
    }
}