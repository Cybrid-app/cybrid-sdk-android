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
            token = "link-sandbox-09703628-aa09-462f-bdca-2014b9aa3d29"
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