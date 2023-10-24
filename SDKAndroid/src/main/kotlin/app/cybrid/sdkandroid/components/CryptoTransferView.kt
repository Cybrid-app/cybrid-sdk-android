package app.cybrid.sdkandroid.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.cryptoTransfer.compose.CryptoTransferView_Content
import app.cybrid.sdkandroid.components.cryptoTransfer.compose.CryptoTransferView_Loading
import app.cybrid.sdkandroid.components.cryptoTransfer.modal.CryptoTransferModal
import app.cybrid.sdkandroid.components.cryptoTransfer.view.CryptoTransferViewModel
import app.cybrid.sdkandroid.components.wallets.compose.ExternalWalletsView_CreateWallet
import app.cybrid.sdkandroid.components.wallets.compose.ExternalWalletsView_Loading
import app.cybrid.sdkandroid.components.wallets.compose.ExternalWalletsView_Wallet
import app.cybrid.sdkandroid.components.wallets.compose.ExternalWalletsView_Wallets
import app.cybrid.sdkandroid.core.Constants

class CryptoTransferView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0):
    Component(context, attrs, defStyle)
{

    enum class State { LOADING, CONTENT, ERROR }
    enum class ModalState { LOADING, QUOTE, DONE, ERROR }

    private var cryptoTransferViewModel: CryptoTransferViewModel? = null

    init {

        LayoutInflater.from(context).inflate(R.layout.base_component, this, true)
        this.composeView = findViewById(R.id.composeContent)
    }

    fun setViewModel(cryptoTransferViewModel: CryptoTransferViewModel) {

        this.cryptoTransferViewModel = cryptoTransferViewModel
        this.initComposeView()
        this.cryptoTransferViewModel?.initComponent()
    }

    private fun initComposeView() {

        this.composeView?.let { compose ->
            compose.setContent {
                CryptoTransferView(cryptoTransferViewModel = cryptoTransferViewModel!!)
            }
        }
    }
}

@Composable
fun CryptoTransferView(
    cryptoTransferViewModel: CryptoTransferViewModel
) {

    // -- Content
    Surface(
        modifier = Modifier
            .testTag(Constants.AccountsViewTestTags.Surface.id)
    ) {

        when(cryptoTransferViewModel.uiState.value) {

            CryptoTransferView.State.LOADING -> {
                CryptoTransferView_Loading()
            }

            CryptoTransferView.State.CONTENT -> {
                CryptoTransferView_Content(
                    cryptoTransferViewModel = cryptoTransferViewModel
                )
            }

            else -> {
                CryptoTransferView_Loading()
            }
        }

        // -- Modals
        if (cryptoTransferViewModel.modalIsOpen.value) {
            CryptoTransferModal(cryptoTransferViewModel = cryptoTransferViewModel)
        }
    }
}