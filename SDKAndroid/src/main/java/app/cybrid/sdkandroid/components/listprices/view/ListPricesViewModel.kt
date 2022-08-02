package app.cybrid.sdkandroid.components.listprices.view

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
import app.cybrid.sdkandroid.util.isSuccessful
import kotlinx.coroutines.launch

class ListPricesViewModel : ViewModel() {

    var prices:List<SymbolPriceBankModel> by mutableStateOf(listOf())
    private var assetsResponse:AssetListBankModel? = null
    var assets:List<AssetBankModel> by mutableStateOf(listOf())

    fun getPricesList(symbol: String? = null) {

        val pricesService = AppModule.getClient().createService(PricesApi::class.java)
        Cybrid.instance.let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.let {
                    it.launch {

                        // -- Getting assets
                        if (assetsResponse == null) { getAssetsList() }

                        // -- Getting prices
                        val pricesResult = getResult { pricesService.listPrices(symbol) }
                        pricesResult.let {
                            prices = if (isSuccessful(it.code ?: 500)) {
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
    }

    private fun getAssetsList() {

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

    fun getCryptoListAsset() : List<AssetBankModel> {

        return ArrayList(this.assets.filter {
            it.type == AssetBankModel.Type.crypto
        })
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

    fun getBuyPrice(symbol: String): SymbolPriceBankModel {

        var ret = SymbolPriceBankModel()
        this.prices.let {
            this.prices.forEach { item ->
                if (item.symbol == symbol) {
                    ret = item
                }
            }
        }
        return ret
    }
}