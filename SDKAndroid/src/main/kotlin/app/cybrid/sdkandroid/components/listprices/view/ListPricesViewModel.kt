package app.cybrid.sdkandroid.components.listprices.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cybrid.cybrid_api_bank.client.apis.PricesApi
import app.cybrid.cybrid_api_bank.client.apis.AssetsApi
import app.cybrid.cybrid_api_bank.client.infrastructure.ApiClient
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.cybrid_api_bank.client.models.SymbolPriceBankModel
import app.cybrid.sdkandroid.AppModule
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.util.Logger
import app.cybrid.sdkandroid.util.LoggerEvents
import app.cybrid.sdkandroid.util.getResult
import app.cybrid.sdkandroid.util.isSuccessful
import kotlinx.coroutines.async

class ListPricesViewModel : ViewModel() {

    private var assetsService = AppModule.getClient().createService(AssetsApi::class.java)
    private var pricesService = AppModule.getClient().createService(PricesApi::class.java)

    var assets:List<AssetBankModel> by mutableStateOf(listOf())
    var prices:List<SymbolPriceBankModel> by mutableStateOf(listOf())

    fun setDataProvider(dataProvider: ApiClient) {

        assetsService = dataProvider.createService(AssetsApi::class.java)
        pricesService = dataProvider.createService(PricesApi::class.java)
    }

    private suspend fun fetchAssets(): List<AssetBankModel> {

        var assets: List<AssetBankModel> = listOf()
        if (!Cybrid.invalidToken) {
            this.viewModelScope.let { scope ->
                val waitFor = scope.async {
                    val assetsResponse = getResult { assetsService.listAssets() }
                    assetsResponse.let {
                        if (isSuccessful(it.code ?: 500)) {
                            Logger.log(LoggerEvents.DATA_REFRESHED, "Fetch - Workflow")
                            assets = it.data?.objects ?: listOf()
                            return@async assets
                        }
                    }
                }
                waitFor.await()
            }
        }
        return assets
    }

    suspend fun getPricesList(symbol: String? = null) {

        if (!Cybrid.invalidToken) {
            viewModelScope.let { scope ->
                val waitFor = scope.async {

                    // -- Getting assets if are empty
                    if (assets.isEmpty()) { assets = fetchAssets() }

                    // -- Getting the prices
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
                waitFor.await()
            }
        }
    }

    fun getCryptoListAsset() : List<AssetBankModel> {

        return ArrayList(this.assets.filter {
            it.type == "crypto"
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
        return assets.find { it.code == symbol }
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