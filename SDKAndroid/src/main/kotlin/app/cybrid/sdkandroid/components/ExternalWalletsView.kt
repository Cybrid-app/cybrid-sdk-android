package app.cybrid.sdkandroid.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewModelScope
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.wallets.compose.ExternalWalletsView_Loading
import app.cybrid.sdkandroid.components.wallets.view.ExternalWalletViewModel
import app.cybrid.sdkandroid.core.Constants
import kotlinx.coroutines.launch

class ExternalWalletsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0):
    Component(context, attrs, defStyle) {

    enum class State { LOADING, WALLETS, WALLET, CREATE, ERROR }
    enum class TransfersState { LOADING, TRANSFERS, EMPTY }

    // -- Internal properties
    internal var externalWalletViewModel: ExternalWalletViewModel? = null

    // -- Constructor
    init {

        LayoutInflater.from(context).inflate(R.layout.base_component, this, true)
        this.composeView = findViewById(R.id.composeContent)
    }

    fun setViewModel(externalWalletViewModel: ExternalWalletViewModel) {

        this.externalWalletViewModel = externalWalletViewModel
        this.initComposeView()
        this.externalWalletViewModel?.viewModelScope?.launch {
            externalWalletViewModel.fetchExternalWallets()
        }
    }

    private fun initComposeView() {

        this.composeView?.let { compose ->
            compose.setContent {
                ExternalWalletsView(this.externalWalletViewModel!!)
            }
        }
    }
}

@Composable
fun ExternalWalletsView(
    externalWalletViewModel: ExternalWalletViewModel
) {

    // -- Content
    Surface(
        modifier = Modifier
            .testTag(Constants.AccountsViewTestTags.Surface.id)
    ) {

        when(externalWalletViewModel.uiState.value) {

            ExternalWalletsView.State.LOADING -> {
                ExternalWalletsView_Loading()
            }
            else -> {}
        }
    }
}