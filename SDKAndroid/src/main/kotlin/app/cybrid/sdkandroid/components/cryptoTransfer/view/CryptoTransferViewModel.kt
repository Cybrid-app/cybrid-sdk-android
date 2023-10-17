package app.cybrid.sdkandroid.components.cryptoTransfer.view

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cybrid.cybrid_api_bank.client.apis.AccountsApi
import app.cybrid.cybrid_api_bank.client.apis.ExternalWalletsApi
import app.cybrid.cybrid_api_bank.client.apis.PricesApi
import app.cybrid.cybrid_api_bank.client.apis.QuotesApi
import app.cybrid.cybrid_api_bank.client.apis.TransfersApi
import app.cybrid.cybrid_api_bank.client.models.AccountBankModel
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.cybrid_api_bank.client.models.ExternalWalletBankModel
import app.cybrid.cybrid_api_bank.client.models.PostQuoteBankModel
import app.cybrid.cybrid_api_bank.client.models.PostTransferBankModel
import app.cybrid.cybrid_api_bank.client.models.QuoteBankModel
import app.cybrid.cybrid_api_bank.client.models.SymbolPriceBankModel
import app.cybrid.cybrid_api_bank.client.models.TransferBankModel
import app.cybrid.sdkandroid.AppModule
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.components.CryptoTransferView
import app.cybrid.sdkandroid.core.AssetPipe
import app.cybrid.sdkandroid.core.BigDecimal
import app.cybrid.sdkandroid.core.BigDecimalPipe
import app.cybrid.sdkandroid.core.toBigDecimal
import app.cybrid.sdkandroid.util.Logger
import app.cybrid.sdkandroid.util.LoggerEvents
import app.cybrid.sdkandroid.util.Polling
import app.cybrid.sdkandroid.util.getResult
import app.cybrid.sdkandroid.util.isSuccessful
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.math.BigDecimal as JavaBigDecimal

class CryptoTransferViewModel: ViewModel() {

    // -- Internal properties
    internal val customerGuid = Cybrid.customerGuid
    internal val fiat = Cybrid.assets.find { it.code == "USD" }
    internal var accounts: List<AccountBankModel> = listOf()
    internal var wallets: List<ExternalWalletBankModel> = listOf()
    internal val prices: MutableState<List<SymbolPriceBankModel>> = mutableStateOf(listOf())
    internal var pricesPolling: Polling? = null

    internal val currentAccount: MutableState<AccountBankModel?> = mutableStateOf(null)
    internal val currentWallets: MutableState<List<ExternalWalletBankModel>> = mutableStateOf(listOf())
    internal val currentAsset: MutableState<AssetBankModel?> = mutableStateOf(null)
    internal var currentWallet: MutableState<ExternalWalletBankModel?> = mutableStateOf(null)
    internal var currentAmountInput: MutableState<String> = mutableStateOf("")
    internal val isTransferInFiat: MutableState<Boolean> = mutableStateOf(false)
    internal val preQuoteValueState: MutableState<String> = mutableStateOf("")
    internal val preQuoteValueHasErrorState: MutableState<Boolean> = mutableStateOf(false)

    internal val currentQuote: MutableState<QuoteBankModel?> = mutableStateOf(null)
    internal val currentTransfer: MutableState<TransferBankModel?> = mutableStateOf(null)

    // -- Public properties
    val uiState: MutableState<CryptoTransferView.State> = mutableStateOf(CryptoTransferView.State.LOADING)
    val modalUiState: MutableState<CryptoTransferView.ModalState> = mutableStateOf(
        CryptoTransferView.ModalState.LOADING)
    val modalIsOpen: MutableState<Boolean> = mutableStateOf(false)
    var modalErrorString: String = ""

    // -- Init method
    fun initComponent() {
        this.viewModelScope.launch { fetchAccounts() }
        pricesPolling = Polling { viewModelScope.launch { fetchPrices() } }
    }

    // -- Server methods
    internal suspend fun fetchAccounts() {

        val accountsService = AppModule.getClient().createService(AccountsApi::class.java)
        this.uiState.value = CryptoTransferView.State.LOADING
        if (!Cybrid.invalidToken) {
            this.viewModelScope.let { scope ->
                val waitFor = scope.async {

                    val accountsResponse = getResult {
                        accountsService.listAccounts(perPage = JavaBigDecimal(50), customerGuid = customerGuid)
                    }
                    accountsResponse.let { response ->
                        if (isSuccessful(response.code ?: 500)) {

                            // -- Log
                            Logger.log(LoggerEvents.DATA_FETCHED, "Crypto Transfer Component - Accounts")

                            // -- Set accounts (Only trading accounts)
                            accounts = response.data?.objects ?: listOf()
                            accounts = accounts.filter { it.type == AccountBankModel.Type.trading }
                                .sortedBy { it.asset }

                            // -- Choosing first account
                            if (accounts.isNotEmpty()) { changeCurrentAccount(accounts.first()) }

                            // -- Fetch External Wallets
                            fetchExternalWallets()

                        } else {

                            Logger.log(LoggerEvents.DATA_ERROR, "Crypto Transfer Component - Accounts")
                            accounts = listOf()
                            uiState.value = CryptoTransferView.State.ERROR
                        }
                    }
                }
                waitFor.await()
            }
        }
    }

    internal suspend fun fetchExternalWallets() {

        val walletsService = AppModule.getClient().createService(ExternalWalletsApi::class.java)
        this.uiState.value = CryptoTransferView.State.LOADING
        if (!Cybrid.invalidToken) {
            this.viewModelScope.let { scope ->
                val waitFor = scope.async {

                    val walletsResponse = getResult {
                        walletsService.listExternalWallets(customerGuid = customerGuid)
                    }
                    walletsResponse.let { response ->
                        if (isSuccessful(response.code ?: 500)) {

                            // -- Log
                            Logger.log(LoggerEvents.DATA_FETCHED, "Crypto Transfer Component - Wallets")

                            // -- Wallets set (Only active wallets)
                            val allWallets = response.data?.objects ?: listOf()
                            wallets = allWallets.filter {
                                it.state != ExternalWalletBankModel.State.deleting &&
                                it.state != ExternalWalletBankModel.State.deleted
                            }.sortedBy { it.name }
                            if (currentAccount.value != null) { changeCurrentAccount(currentAccount.value!!) }
                            uiState.value = CryptoTransferView.State.CONTENT

                        } else {

                            Logger.log(LoggerEvents.DATA_ERROR, "Crypto Transfer Component - Wallets")
                            wallets = listOf()
                            uiState.value = CryptoTransferView.State.ERROR
                        }
                    }
                }
                waitFor.await()
            }
        }
    }

    internal suspend fun fetchPrices() {

        val pricesService = AppModule.getClient().createService(PricesApi::class.java)
        if (!Cybrid.invalidToken) {
            this.viewModelScope.let { scope ->
                val waitFor = scope.async {

                    val pricesResponse = getResult {
                        pricesService.listPrices()
                    }
                    pricesResponse.let { response ->
                        if (isSuccessful(response.code ?: 500)) {

                            Logger.log(LoggerEvents.DATA_FETCHED, "Crypto Transfer Component - Prices")
                            prices.value = response.data ?: listOf()

                        } else {

                            Logger.log(LoggerEvents.DATA_ERROR, "Crypto Transfer Component - Prices")
                            prices.value = listOf()
                        }
                    }
                }
                waitFor.await()
            }
        }
    }

    suspend fun createQuote(amount: String) {

        this.openModal()
        val postQuoteBankModel = this.createPostQuoteBankModel(amount)
        if (postQuoteBankModel == null) {
            Logger.log(LoggerEvents.DATA_ERROR, "Crypto Transfer Component - Create PostQuoteBankModel")
            modalUiState.value = CryptoTransferView.ModalState.ERROR
            return
        }

        val quotesService = AppModule.getClient().createService(QuotesApi::class.java)
        this.uiState.value = CryptoTransferView.State.LOADING
        if (!Cybrid.invalidToken) {
            this.viewModelScope.let { scope ->
                val waitFor = scope.async {

                    val quoteResponse = getResult {
                        quotesService.createQuote(postQuoteBankModel)
                    }
                    quoteResponse.let { response ->
                        if (isSuccessful(response.code ?: 500)) {

                            Logger.log(LoggerEvents.DATA_FETCHED, "Crypto Transfer Component - QUOTE")
                            currentQuote.value = response.data
                            modalUiState.value = CryptoTransferView.ModalState.QUOTE

                        } else {

                            Logger.log(LoggerEvents.DATA_ERROR, "Crypto Transfer Component - QUOTE")
                            currentQuote.value = null
                            modalUiState.value = CryptoTransferView.ModalState.ERROR
                        }
                    }
                }
                waitFor.await()
            }
        }
    }

    suspend fun createTransfer() {

        this.modalUiState.value = CryptoTransferView.ModalState.LOADING
        val postTransferBankModel = this.createPostTransferBankModel()
        if (postTransferBankModel == null) {
            Logger.log(LoggerEvents.DATA_ERROR, "Crypto Transfer Component - Create PostTransferBankModel")
            modalUiState.value = CryptoTransferView.ModalState.ERROR
            return
        }

        val transferService = AppModule.getClient().createService(TransfersApi::class.java)
        this.uiState.value = CryptoTransferView.State.LOADING
        if (!Cybrid.invalidToken) {
            this.viewModelScope.let { scope ->
                val waitFor = scope.async {

                    val transferResponse = getResult {
                        transferService.createTransfer(postTransferBankModel)
                    }
                    transferResponse.let { response ->
                        if (isSuccessful(response.code ?: 500)) {

                            Logger.log(LoggerEvents.DATA_FETCHED, "Crypto Transfer Component - TRANSFER")
                            currentTransfer.value = response.data
                            modalUiState.value = CryptoTransferView.ModalState.DONE

                        } else {

                            Logger.log(LoggerEvents.DATA_ERROR, "Crypto Transfer Component - TRANSFER")
                            currentQuote.value = null
                            modalUiState.value = CryptoTransferView.ModalState.ERROR
                        }
                    }
                }
                waitFor.await()
            }
        }
    }

    // -- Accounts Methods
    internal fun changeCurrentAccount(account: AccountBankModel) {

        this.currentAccount.value = account
        val assetCode = this.currentAccount.value?.asset ?: ""

        // -- Changing current wallets
        this.currentWallets.value = this.wallets.filter { it.asset == assetCode }
        this.currentWallet.value = if (this.currentWallets.value.isEmpty()) null
        else this.currentWallets.value.first()

        // -- Changing current asset
        this.currentAsset.value = Cybrid.assets.find { it.code == assetCode }

        // -- Setting as no fiat
        this.isTransferInFiat.value = false
    }

    internal fun getMaxAmountOfAccount(): String {

        var accountValue = "0"
        val account = this.currentAccount.value ?: return accountValue
        val assetCode = account.asset ?: return accountValue
        val asset = Cybrid.assets.find { it.code == assetCode } ?: return accountValue
        val accountBalance = account.platformBalance ?: return accountValue
        accountValue = AssetPipe.transform(accountBalance.toBigDecimal(), asset, AssetPipe.AssetPipeTrade).toPlainString()
        return accountValue
    }

    // -- Quote Methods
    internal fun createPostQuoteBankModel(amount: String): PostQuoteBankModel? {

        val currentAccount = currentAccount.value
        if (currentAccount?.asset == null) {
            return null
        }

        val assetCode = currentAccount.asset
        val asset = Cybrid.assets.find { it.code == assetCode } ?: return null

        val amountReady = AssetPipe.transform(amount, asset, AssetPipe.AssetPipeBase)
        return PostQuoteBankModel(
            productType = PostQuoteBankModel.ProductType.cryptoTransfer,
            customerGuid = customerGuid,
            asset = asset.code,
            side = PostQuoteBankModel.Side.withdrawal,
            deliverAmount = amountReady.toJavaBigDecimal()
        )
    }

    internal fun calculatePreQuote() {

        this.preQuoteValueHasErrorState.value = false

        val assetCode = this.currentAccount.value?.asset
        val asset = this.currentAsset.value
        if (asset == null) {
            this.preQuoteValueState.value = "0"
            this.modalErrorString = CryptoTransferViewModelErrors.assetNotFoundError()
            return
        }
        val counterAsset = Cybrid.assets.find { it.code == "USD" }
        val symbol = "${asset.code}-${counterAsset!!.code}"

        try {

            val amount = BigDecimal(this.currentAmountInput.value)

            // -- Assets
            val assetToUse = if (isTransferInFiat.value) counterAsset else asset
            val assetToConvert = if (isTransferInFiat.value) asset else counterAsset

            // -- Buy Price
            val sellPrice = this.getPrice(symbol).sellPrice
            if (sellPrice == null) {
                this.preQuoteValueState.value = "0"
                this.modalErrorString = CryptoTransferViewModelErrors.buyPriceError()
                return
            }

            // -- Trade Value
            val amountFromInput = if (isTransferInFiat.value) {
                AssetPipe.transform(amount, counterAsset, AssetPipe.AssetPipeBase)
            } else {
                amount
            }

            val tradeValue = AssetPipe.trade(
                input = amountFromInput,
                price = sellPrice.toBigDecimal(),
                base = if (isTransferInFiat.value) AssetBankModel.Type.fiat else AssetBankModel.Type.crypto,
                decimals = assetToConvert.decimals.toBigDecimal()
            )
            val accountBalance = this.currentAccount.value?.platformBalance?.toBigDecimal() ?: BigDecimal.zero()

            // -- Validation of balance
            if (this.isTransferInFiat.value) { // Input example: 1 USD

                this.preQuoteValueState.value = tradeValue.toPlainString()

                val accountBalanceInFormat = AssetPipe.transform(accountBalance, asset, AssetPipe.AssetPipeTrade)
                if (tradeValue > accountBalanceInFormat) {
                    this.preQuoteValueHasErrorState.value = true
                }

            } else { // Input example: 1 BTC

                var tradeValueFormatted = BigDecimalPipe.transform(tradeValue, assetToConvert)
                tradeValueFormatted += " ${assetToConvert.code}"
                this.preQuoteValueState.value = tradeValueFormatted

                val amountFromInputInFormat = AssetPipe.transform(amountFromInput, asset, AssetPipe.AssetPipeBase)
                if (amountFromInputInFormat > accountBalance) {
                    this.preQuoteValueHasErrorState.value = true
                }
            }

        } catch(e: Exception) {
            this.preQuoteValueState.value = "0"
            this.modalErrorString = CryptoTransferViewModelErrors.amountError()
        }
    }

    // -- Transfer Methods
    internal fun createPostTransferBankModel(): PostTransferBankModel? {

        val currentQuote = this.currentQuote.value ?: return null
        val currentWallet = this.currentWallet ?: return null
        return PostTransferBankModel(
            quoteGuid = currentQuote.guid!!,
            transferType = PostTransferBankModel.TransferType.crypto,
            externalWalletGuid = currentWallet.value?.guid!!
        )
    }

    // -- Prices Methods
    internal fun getPrice(symbol: String): SymbolPriceBankModel {
        return this.prices.value.find { it.symbol == symbol } ?: SymbolPriceBankModel()
    }

    // -- View Methods
    fun openModal() {
        this.modalIsOpen.value = true
        this.modalUiState.value = CryptoTransferView.ModalState.LOADING
    }

    fun closeModal() {
        this.modalIsOpen.value = false
        this.modalUiState.value = CryptoTransferView.ModalState.LOADING
    }

    fun maxButtonClickHandler() {
        val amount = this.getMaxAmountOfAccount()
        this.resetAmountInput(amount)
    }

    internal fun resetAmountInput(amount: String = "") {
        this.currentAmountInput.value = amount
    }
}

internal object CryptoTransferViewModelErrors {

    fun assetNotFoundError(): String { return "Asset not found" }
    fun amountError(): String { return "Amount has to be numeric" }
    fun buyPriceError(): String { return "No price data at this moment" }
}