package app.cybrid.sdkandroid.components.trade.compose

import android.content.Context
import androidx.compose.runtime.Composable
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.sdkandroid.components.CryptoList
import app.cybrid.sdkandroid.components.ListPricesViewCustomStyles
import app.cybrid.sdkandroid.components.trade.view.TradeViewModel

@Composable
fun TradeView_ListPrices(
    tradeViewModel: TradeViewModel,
    context: Context,
    onClick: (asset: AssetBankModel, pairAsset: AssetBankModel) -> Unit
) {
    CryptoList(
        cryptoList = tradeViewModel.listPricesViewModel?.prices ?: listOf(),
        viewModel = tradeViewModel.listPricesViewModel,
        context = context,
        customStyles = ListPricesViewCustomStyles(),
        onClick = onClick)
}