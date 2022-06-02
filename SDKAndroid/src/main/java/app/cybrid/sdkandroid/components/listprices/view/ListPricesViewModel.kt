package app.cybrid.sdkandroid.components.listprices.view

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cybrid.cybrid_api_bank.client.apis.PricesApi
import app.cybrid.cybrid_api_bank.client.apis.AssetsApi
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.cybrid_api_bank.client.models.AssetListBankModel
import app.cybrid.cybrid_api_bank.client.models.SymbolPriceBankModel
import app.cybrid.sdkandroid.AppModule
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.util.Logger
import app.cybrid.sdkandroid.util.LoggerEvents
import app.cybrid.sdkandroid.util.getResult
import kotlinx.coroutines.launch

class ListPricesViewModel : ViewModel() {

    var prices:List<SymbolPriceBankModel> by mutableStateOf(listOf())
    private var assetsResponse:AssetListBankModel? = null
    var assets:List<AssetBankModel> = mutableListOf()

    fun getListPrices(symbol: String? = null) {

        val pricesService = AppModule.getClient().createService(PricesApi::class.java)
        Cybrid.instance.let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.launch {

                    // -- Getting assets
                    if (assetsResponse == null) { getAssets() }

                    // -- Getting prices
                    val pricesResult = getResult { pricesService.listPrices(symbol) }
                    pricesResult.let {
                        prices = if (it.code == 200) {
                            it.data!!
                        } else {
                            Logger.log(LoggerEvents.DATA_ERROR, "ListPricesView Component - Prices Data :: ${it.message}")
                            listOf()
                        }
                    }
                }
            }
        }
    }

    private fun getAssets() {

        val assetsService = AppModule.getClient().createService(AssetsApi::class.java)
        viewModelScope.launch {

            val assetsResult = getResult { assetsService.listAssets() }
            assetsResult.let {

                assetsResponse = if (it.code == 200) {
                    it.data!!
                } else {
                    Logger.log(LoggerEvents.DATA_ERROR, "ListPricesView Component - Assets Data :: {${it.message}}")
                    null
                }
            }
            assetsResponse?.let {
                assets = it.objects
            }
        }
    }

    fun getSymbol(symbol: String): String {

        val symbolParts = symbol.split("-")
        return symbolParts[0]
    }

    fun getPair(symbol: String): String {

        val symbolParts = symbol.split("-")
        return symbolParts[1]
    }

    fun findAsset(symbol: String): AssetBankModel? {
        val a = assets.find { it.code == symbol }
        return a
    }
}