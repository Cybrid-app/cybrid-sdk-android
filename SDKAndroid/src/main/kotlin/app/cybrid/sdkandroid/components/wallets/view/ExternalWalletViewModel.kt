package app.cybrid.sdkandroid.components.wallets.view

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.cybrid_api_bank.client.apis.*
import app.cybrid.cybrid_api_bank.client.models.ExternalWalletBankModel
import app.cybrid.cybrid_api_bank.client.models.PostExternalWalletBankModel
import app.cybrid.cybrid_api_bank.client.models.TransferBankModel
import app.cybrid.sdkandroid.AppModule
import app.cybrid.sdkandroid.components.ExternalWalletsView
import app.cybrid.sdkandroid.util.Logger
import app.cybrid.sdkandroid.util.LoggerEvents
import app.cybrid.sdkandroid.util.getResult
import app.cybrid.sdkandroid.util.isSuccessful
import kotlinx.coroutines.async

class ExternalWalletViewModel: ViewModel() {

    // -- Private properties
    private var walletsService = AppModule.getClient().createService(ExternalWalletsApi::class.java)
    private var transfersService = AppModule.getClient().createService(TransfersApi::class.java)

    // -- Internal properties
    internal var customerGuid = Cybrid.customerGuid
    internal var externalWallets: List<ExternalWalletBankModel> = listOf()
    internal var externalWalletsActive: List<ExternalWalletBankModel> = listOf()
    internal var transfers: List<TransferBankModel> = listOf()

    // -- Public properties
    var uiState: MutableState<ExternalWalletsView.State> = mutableStateOf(ExternalWalletsView.State.LOADING)
    var transfersUiState: MutableState<ExternalWalletsView.TransfersState> = mutableStateOf(ExternalWalletsView.TransfersState.LOADING)
    var addressScannedValue: MutableState<String> = mutableStateOf("")
    var tagScannedValue: MutableState<String> = mutableStateOf("")
    var currentWallet: ExternalWalletBankModel? = null
    var lastUiState: ExternalWalletsView.State = ExternalWalletsView.State.LOADING
    var serverError = ""

    // -- Constructor
    init {}

    // -- Server Methods
    internal suspend fun fetchExternalWallets() {

        this.uiState.value = ExternalWalletsView.State.LOADING
        if (!Cybrid.invalidToken) {
            this.viewModelScope.let { scope ->
                val waitFor = scope.async {

                    val walletsResponse = getResult {
                        walletsService.listExternalWallets(customerGuid = customerGuid)
                    }
                    walletsResponse.let { response ->
                        if (isSuccessful(response.code ?: 500)) {

                            Logger.log(LoggerEvents.DATA_FETCHED, "External Wallets")
                            externalWallets = response.data?.objects ?: listOf()
                            externalWalletsActive = externalWallets.filter { wallet ->
                                wallet.state != ExternalWalletBankModel.State.deleting &&
                                        wallet.state != ExternalWalletBankModel.State.deleted
                            }
                            uiState.value = ExternalWalletsView.State.WALLETS

                        } else {

                            Logger.log(LoggerEvents.DATA_ERROR, "External Wallets")
                            externalWallets= listOf()
                            externalWalletsActive = listOf()
                            uiState.value = ExternalWalletsView.State.ERROR
                        }
                    }
                }
                waitFor.await()
            }
        }
    }

    internal suspend fun createWallet(postExternalWalletBankModel: PostExternalWalletBankModel) {

        this.uiState.value = this.uiState.value
        this.uiState.value = ExternalWalletsView.State.LOADING
        if (!Cybrid.invalidToken) {
            this.viewModelScope.let { scope ->
                val waitFor = scope.async {

                    val walletResponse = getResult {
                        walletsService.createExternalWallet(postExternalWalletBankModel)
                    }
                    walletResponse.let { response ->
                        if (isSuccessful(response.code ?: 500)) {

                            Logger.log(LoggerEvents.DATA_FETCHED, "Create Wallet")
                            fetchExternalWallets()

                        } else {

                            Logger.log(LoggerEvents.DATA_ERROR, "Create Wallet")
                            // handleError
                            uiState.value = ExternalWalletsView.State.ERROR
                        }
                    }
                }
                waitFor.await()
            }
        }
    }

    internal suspend fun deleteExternalWallet() {

        this.uiState.value = ExternalWalletsView.State.LOADING
        if (!Cybrid.invalidToken) {
            this.viewModelScope.let { scope ->
                val waitFor = scope.async {

                    val walletResponse = getResult {
                        walletsService.deleteExternalWallet(externalWalletGuid = currentWallet!!.guid!!)
                    }
                    walletResponse.let { response ->
                        if (isSuccessful(response.code ?: 500)) {

                            Logger.log(LoggerEvents.DATA_FETCHED, "Delete Wallet")
                            fetchExternalWallets()
                            currentWallet = null

                        } else {

                            Logger.log(LoggerEvents.DATA_ERROR, "Delete Wallet")
                            uiState.value = ExternalWalletsView.State.ERROR
                        }
                    }
                }
                waitFor.await()
            }
        }
    }

    internal fun goToWalletDetail(wallet: ExternalWalletBankModel) {

        this.currentWallet = wallet
        this.uiState.value = ExternalWalletsView.State.WALLET
        this.transfers = listOf()
    }

    internal suspend fun fetchTransfers() {

        this.transfersUiState.value = ExternalWalletsView.TransfersState.LOADING
        if (!Cybrid.invalidToken) {
            this.viewModelScope.let { scope ->
                val waitFor = scope.async {

                    val transfersResponse = getResult {
                        transfersService.listTransfers(customerGuid = customerGuid)
                    }
                    transfersResponse.let { response ->
                        if (isSuccessful(response.code ?: 500)) {

                            Logger.log(LoggerEvents.DATA_FETCHED, "Transfers")
                            transfers = response.data?.objects ?: listOf()
                            transfersUiState.value = ExternalWalletsView.TransfersState.EMPTY

                        } else {

                            Logger.log(LoggerEvents.DATA_ERROR, "Transfers")
                            transfers = listOf()
                            transfersUiState.value = ExternalWalletsView.TransfersState.EMPTY
                        }
                    }
                }
                waitFor.await()
            }
        }
    }

    internal fun handleQRScanned(code: String) {

        Log.d(Cybrid.logTag, code)
        var codeValue = code
        if (code.contains(":")) {
            val codeParts = code.split(":")
            val data = codeParts[1]
            val dataComponents = data.split("&")
            if (dataComponents.count() > 1) {
                val address = dataComponents[0]
                val components = dataComponents[1]
                codeValue = address
                // find
            } else {
                codeValue = data
            }
        }
        this.addressScannedValue.value = codeValue
    }

    internal fun findTagInQRData(components: String) {

        var tagValue = ""
        val componentsParts = components.split("?")
        if (componentsParts.isNotEmpty()) {
            for (component in componentsParts) {
                val componentParts = component.split("=")
                if (componentParts.count() == 2) {
                    val itemName = componentParts[0]
                    val itemValue = componentParts[1]
                    if (itemName == "tag") {
                        tagValue = itemValue
                    }
                }
            }
        }
        this.tagScannedValue.value = tagValue
    }
}