package app.cybrid.sdkandroid.components.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cybrid.cybrid_api_bank.client.apis.*
import app.cybrid.cybrid_api_bank.client.infrastructure.ApiClient
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.cybrid_api_bank.client.models.BankBankModel
import app.cybrid.cybrid_api_bank.client.models.CustomerBankModel
import app.cybrid.sdkandroid.AppModule
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.util.Logger
import app.cybrid.sdkandroid.util.LoggerEvents
import app.cybrid.sdkandroid.util.getResult
import app.cybrid.sdkandroid.util.isSuccessful
import kotlinx.coroutines.async

class CybridViewModel: ViewModel() {

    private var customerService = AppModule.getClient().createService(CustomersApi::class.java)
    private var bankService = AppModule.getClient().createService(BanksApi::class.java)
    private var assetsService = AppModule.getClient().createService(AssetsApi::class.java)

    var customerGuid = Cybrid.getInstance().customerGuid

    fun setDataProvider(dataProvider: ApiClient)  {

        customerService = dataProvider.createService(CustomersApi::class.java)
        bankService = dataProvider.createService(BanksApi::class.java)
        assetsService = dataProvider.createService(AssetsApi::class.java)
    }

    suspend fun fetchCustomer(): CustomerBankModel? {

        var customer: CustomerBankModel? = null
        Cybrid.getInstance().let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.let { scope ->
                    val waitFor = scope.async {
                        val customerResult = getResult {
                            customerService.getCustomer(customerGuid = customerGuid)
                        }
                        customerResult.let {
                            if (isSuccessful(it.code ?: 500)) {
                                Logger.log(LoggerEvents.DATA_FETCHED, "Fetch - Customer")
                                customer = it.data
                                return@async customer
                            } else {
                                Logger.log(LoggerEvents.NETWORK_ERROR, "Fetch - Customer")
                            }
                        }
                    }
                    waitFor.await()
                }
            }
        }
        return customer
    }

    suspend fun fetchBank(guid: String): BankBankModel? {

        var bank: BankBankModel? = null
        Cybrid.getInstance().let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.let { scope ->
                    val waitFor = scope.async {
                        val bankResult = getResult {
                            bankService.getBank(bankGuid = guid)
                        }
                        bankResult.let {
                            if (isSuccessful(it.code ?: 500)) {
                                Logger.log(LoggerEvents.DATA_FETCHED, "Fetch - Bank")
                                bank = it.data
                                return@async bank
                            } else {
                                Logger.log(LoggerEvents.NETWORK_ERROR, "Fetch - Bank")
                            }
                        }
                    }
                    waitFor.await()
                }
            }
        }
        return bank
    }

    suspend fun fetchAssets(): List<AssetBankModel>? {

        var assets: List<AssetBankModel>? = null
        Cybrid.getInstance().let { cybrid ->
            if (!cybrid.invalidToken) {
                viewModelScope.let { scope ->
                    val waitFor = scope.async {
                        val assetsResponse = getResult { assetsService.listAssets() }
                        assetsResponse.let {
                            if (isSuccessful(it.code ?: 500)) {
                                Logger.log(LoggerEvents.DATA_REFRESHED, "Fetch - Workflow")
                                assets = it.data?.objects
                                return@async assets
                            }
                        }
                    }
                    waitFor.await()
                }
            }
        }
        return assets
    }
}