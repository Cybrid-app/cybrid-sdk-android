package app.cybrid.sdkandroid.components.plaid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import app.cybrid.sdkandroid.R
import com.plaid.link.OpenPlaidLink
import com.plaid.link.linkTokenConfiguration
import com.plaid.link.result.LinkExit
import com.plaid.link.result.LinkSuccess

class PlaidIntegration : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plaid_integration)

        // --
        val linkTokenConfiguration = linkTokenConfiguration {
            token = "link-sandbox-bc71e7b3-4b82-454d-90a8-58fa9dbfb699"
        }

        val linkAccountToPlaid = registerForActivityResult(OpenPlaidLink()) {
                when (it) {
                    is LinkSuccess -> {}
                    is LinkExit -> {}
                }
            }
        linkAccountToPlaid.launch(linkTokenConfiguration)
    }
}