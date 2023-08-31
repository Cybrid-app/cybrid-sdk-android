package app.cybrid.sdkandroid.components.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.ExternalWalletsView
import app.cybrid.sdkandroid.components.wallets.view.ExternalWalletViewModel

class WalletsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallets)

        // -- Creating ViewModel
        val externalWalletViewModel: ExternalWalletViewModel by viewModels()

        // -- Finding the view
        val externalWalletsView: ExternalWalletsView = findViewById(R.id.view)

        // -- Set controller
        externalWalletsView.setViewModel(externalWalletViewModel)
    }
}