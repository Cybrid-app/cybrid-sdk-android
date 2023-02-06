package app.cybrid.sdkandroid.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewModelScope
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.kyc.compose.KYCView_Modal_Warning
import app.cybrid.sdkandroid.components.trade.compose.TradeView_ListPrices
import app.cybrid.sdkandroid.components.trade.compose.TradeView_Loading
import app.cybrid.sdkandroid.components.trade.compose.TradeView_QuoteContent
import app.cybrid.sdkandroid.components.trade.compose.TradeView_QuoteModal
import app.cybrid.sdkandroid.components.trade.view.TradeViewModel
import app.cybrid.sdkandroid.core.Constants
import kotlinx.coroutines.launch

class TradeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0):
Component(context, attrs, defStyle) {

    enum class ViewState { LOADING, LIST_PRICES, QUOTE_CONTENT }
    enum class QuoteModalViewState { LOADING, CONTENT, LOADING_SUBMITTED, DONE }

    private var currentState = mutableStateOf(ViewState.LOADING)
    var tradeViewModel: TradeViewModel? = null

    init {

        LayoutInflater.from(context).inflate(R.layout.trade_component, this, true)
        this.composeView = findViewById(R.id.composeContent)
    }

    fun setViewModel(tradeViewModel: TradeViewModel) {

        this.tradeViewModel = tradeViewModel
        this.currentState = this.tradeViewModel?.uiState!!
        tradeViewModel.viewModelScope.launch {
            tradeViewModel.getPricesList()
        }
        this.initComposeView()
    }

    private fun initComposeView() {
        this.composeView?.let { compose ->
            compose.setContent {
                TradeView(
                    currentState = currentState,
                    tradeViewModel = tradeViewModel!!,
                    context = context
                )
            }
        }
    }
}

/**
 * Composable Function for Trade View
 **/
@Composable
fun TradeView(
    currentState: MutableState<TradeView.ViewState>,
    tradeViewModel: TradeViewModel,
    context: Context
) {

    // -- Content
    Surface(modifier = Modifier.testTag(Constants.TransferView.Surface.id)) {

        // -- Vars
        val selectedTabIndex = remember { mutableStateOf(0) }

        // -- Main Content
        when(currentState.value) {

            TradeView.ViewState.LOADING -> {
                TradeView_Loading()
            }

            TradeView.ViewState.LIST_PRICES -> {
                TradeView_ListPrices(
                    tradeViewModel = tradeViewModel,
                    onClick = { asset, pairAsset ->
                        tradeViewModel.handlePricesOnClick(asset, pairAsset)
                    }
                )
            }

            TradeView.ViewState.QUOTE_CONTENT -> {
                TradeView_QuoteContent(
                    tradeViewModel = tradeViewModel,
                    selectedTabIndex = selectedTabIndex
                )
            }
        }

        // -- Modals
        if (tradeViewModel.showModalDialog.value) {
            TradeView_QuoteModal(
                tradeViewModel = tradeViewModel,
                asset = tradeViewModel.currentAsset,
                pairAsset = tradeViewModel.currentPairAsset.value,
                selectedTabIndex = selectedTabIndex)
        }

        if (tradeViewModel.showKYCWarningModal.value) {
            KYCView_Modal_Warning(
                showDialog = tradeViewModel.showKYCWarningModal,
                context = context
            )
        }
    }
}