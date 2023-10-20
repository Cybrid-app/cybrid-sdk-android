package app.cybrid.sdkandroid.components.trade.compose

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.TradeView
import app.cybrid.sdkandroid.components.trade.view.TradeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeView_QuoteModal(
    tradeViewModel: TradeViewModel,
    asset: MutableState<AssetBankModel?>,
    pairAsset: AssetBankModel?,
    selectedTabIndex: MutableState<Int>,
    updateInterval: Long = 5000
) {

    // -- Content
    ModalBottomSheet(
        onDismissRequest = { tradeViewModel.modalBeDismissed() },
        containerColor = Color.White,
        windowInsets = WindowInsets(0)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            when(tradeViewModel.uiModalState.value) {

                TradeView.QuoteModalViewState.LOADING -> {
                    TradeView_QuoteModal_Loading(
                        textID = R.string.trade_flow_quote_confirmation_modal_pending_title)
                }

                TradeView.QuoteModalViewState.CONTENT -> {
                    TradeView_QuoteModal_Content(
                        tradeViewModel = tradeViewModel,
                        asset = asset,
                        pairAsset = pairAsset,
                        selectedTabIndex = selectedTabIndex,
                        updateInterval = updateInterval
                    )
                }

                TradeView.QuoteModalViewState.LOADING_SUBMITTED -> {
                    TradeView_QuoteModal_Loading(
                        textID = R.string.trade_flow_quote_confirmation_modal_submitted_title)
                }

                TradeView.QuoteModalViewState.DONE -> {
                    TradeView_QuoteModal_Done(
                        tradeViewModel = tradeViewModel,
                        asset = asset,
                        pairAsset = pairAsset,
                        selectedTabIndex = selectedTabIndex
                    )
                }
            }
        }
    }
}