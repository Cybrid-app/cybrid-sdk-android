package app.cybrid.sdkandroid.components.cryptoTransfer.view

import app.cybrid.cybrid_api_bank.client.models.AccountBankModel
import app.cybrid.cybrid_api_bank.client.models.PostQuoteBankModel
import app.cybrid.cybrid_api_bank.client.models.PostTransferBankModel
import app.cybrid.sdkandroid.BaseTest
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.components.CryptoTransferView
import app.cybrid.sdkandroid.mock.AssetsApiMock
import app.cybrid.sdkandroid.mock.CryptoTransferApiMock
import app.cybrid.sdkandroid.mock.CryptoTransferApiMockModel
import app.cybrid.sdkandroid.mock.ExternalWalletBankModelMock
import app.cybrid.sdkandroid.mock.ExternalWalletsApiMock
import io.mockk.coVerify
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import retrofit2.Response
import java.math.BigDecimal as JavaBigDecimal

class CryptoTransferViewModelTest: BaseTest() {

    override fun setup() {

        super.setup()
        Cybrid.customerGuid = "12345"
        Cybrid.invalidToken = false
        Cybrid.bearer = "1234"
        Cybrid.assets = AssetsApiMock.AssetBankModelObject.mock()
    }

    // -- Server methods
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_fetchAccounts() = runTest {

        // -- Given
        val accountsMockResponse = CryptoTransferApiMockModel.accountList()
        val accountsResponse = Response.success(accountsMockResponse)

        val walletsMockResponse = ExternalWalletBankModelMock.list(listOf(ExternalWalletBankModelMock.mock()))
        val walletsResponse = Response.success(walletsMockResponse)

        // -- Mock
        val cryptoTransferViewModel = spyk(CryptoTransferViewModel())
        val mockAccountsApi = CryptoTransferApiMock.mock_listAccounts(accountsResponse)
        ExternalWalletsApiMock.mock_listExternalWallets(walletsResponse, false)

        // -- When
        cryptoTransferViewModel.fetchAccounts()

        // -- Verify
        coVerify { mockAccountsApi.listAccounts(perPage = any(), customerGuid = any()) }

        // -- Then
        Assert.assertFalse(cryptoTransferViewModel.accounts.isEmpty())
        Assert.assertEquals(cryptoTransferViewModel.accounts.count(), 1)
        Assert.assertEquals(cryptoTransferViewModel.accounts.first().type, AccountBankModel.Type.trading)
        Assert.assertNotNull(cryptoTransferViewModel.currentAccount.value)
        Assert.assertEquals(cryptoTransferViewModel.currentAccount.value, cryptoTransferViewModel.accounts.first())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_fetchExternalWallets() = runTest {

        // -- Given
        val customerGuid = Cybrid.customerGuid
        val walletsMockResponse = ExternalWalletBankModelMock.list(listOf(
            ExternalWalletBankModelMock.mock(),
            ExternalWalletBankModelMock.mock_deleted(),
            ExternalWalletBankModelMock.mock_deleting()
        ))
        val walletsResponse = Response.success(walletsMockResponse)

        // -- Mock
        val cryptoTransferViewModel = spyk(CryptoTransferViewModel())
        val mockWalletsApi = ExternalWalletsApiMock.mock_listExternalWallets(walletsResponse)

        // -- When
        cryptoTransferViewModel.fetchExternalWallets()

        // -- Verify
        coVerify { mockWalletsApi.listExternalWallets(customerGuid = customerGuid) }

        // -- Then
        Assert.assertFalse(cryptoTransferViewModel.wallets.isEmpty())
        Assert.assertEquals(cryptoTransferViewModel.wallets.count(), 1)
        Assert.assertEquals(cryptoTransferViewModel.wallets[0].asset, "BTC")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_fetchPrices() = runTest {

        // -- Given
        val pricesMockResponse = CryptoTransferApiMockModel.pricesList()
        val pricesResponse = Response.success(pricesMockResponse)

        // -- Mock
        val cryptoTransferViewModel = spyk(CryptoTransferViewModel())
        val mockPricesApi = CryptoTransferApiMock.mock_listPrices(pricesResponse)

        // -- When
        cryptoTransferViewModel.fetchPrices()

        // -- Verify
        coVerify { mockPricesApi.listPrices() }

        // -- Then
        Assert.assertFalse(cryptoTransferViewModel.prices.value.isEmpty())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_createQuote() = runTest {

        // -- Given
        val postQuoteBankModel = CryptoTransferApiMockModel.postQuote()
        val quoteMockResponse = CryptoTransferApiMockModel.quote()
        val quoteResponse = Response.success(quoteMockResponse)

        // -- Mock
        val cryptoTransferViewModel = spyk<CryptoTransferViewModel>()
        val mockQuotesApi = CryptoTransferApiMock.mock_createQuote(quoteResponse)

        // -- Case: Current Account is null
        cryptoTransferViewModel.currentQuote.value = null
        cryptoTransferViewModel.currentAccount.value = null
        cryptoTransferViewModel.createQuote("2")
        Assert.assertTrue(cryptoTransferViewModel.modalIsOpen.value)
        Assert.assertEquals(cryptoTransferViewModel.modalUiState.value, CryptoTransferView.ModalState.ERROR)
        Assert.assertNull(cryptoTransferViewModel.currentQuote.value)

        // -- Case: Current Account is not null
        cryptoTransferViewModel.currentQuote.value = null
        cryptoTransferViewModel.currentAccount.value = CryptoTransferApiMockModel.accountBTC()
        cryptoTransferViewModel.createQuote("2")
        coVerify { mockQuotesApi.createQuote(postQuoteBankModel) }
        Assert.assertTrue(cryptoTransferViewModel.modalIsOpen.value)
        Assert.assertEquals(cryptoTransferViewModel.modalUiState.value, CryptoTransferView.ModalState.QUOTE)
        Assert.assertNotNull(cryptoTransferViewModel.currentQuote.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_createTransfer() = runTest {

        // -- Given
        val transferMockResponse = CryptoTransferApiMockModel.transfer()
        val transferResponse = Response.success(transferMockResponse)

        // -- Mock
        val cryptoTransferViewModel = spyk<CryptoTransferViewModel>()
        val mockTransferApi = CryptoTransferApiMock.mock_createTransfer(transferResponse)

        // -- Case: PostTransferBankModel as null (currentQuote null)
        cryptoTransferViewModel.currentTransfer.value = null
        cryptoTransferViewModel.currentQuote.value = null
        cryptoTransferViewModel.createTransfer()
        Assert.assertEquals(cryptoTransferViewModel.modalUiState.value, CryptoTransferView.ModalState.ERROR)
        Assert.assertNull(cryptoTransferViewModel.currentTransfer.value)

        // -- Case: PostTransferBankModel as null (currentWallet null)
        cryptoTransferViewModel.currentTransfer.value = null
        cryptoTransferViewModel.currentQuote.value = CryptoTransferApiMockModel.quote()
        cryptoTransferViewModel.currentWallet.value = null
        cryptoTransferViewModel.createTransfer()
        Assert.assertEquals(cryptoTransferViewModel.modalUiState.value, CryptoTransferView.ModalState.ERROR)
        Assert.assertNull(cryptoTransferViewModel.currentTransfer.value)

        // -- Case: Good
        cryptoTransferViewModel.currentTransfer.value = null
        cryptoTransferViewModel.currentQuote.value = CryptoTransferApiMockModel.quote()
        cryptoTransferViewModel.currentWallet.value = ExternalWalletBankModelMock.mock()
        cryptoTransferViewModel.createTransfer()
        coVerify { mockTransferApi.createTransfer(any()) }
        Assert.assertNotNull(cryptoTransferViewModel.currentTransfer.value)
        Assert.assertEquals(cryptoTransferViewModel.modalUiState.value, CryptoTransferView.ModalState.DONE)
    }

    // -- Accounts Methods
    @Test
    fun test_changeCurrentAccount() {

        // -- Given
        val cryptoTransferViewModel = CryptoTransferViewModel()
        cryptoTransferViewModel.wallets = listOf(
            ExternalWalletBankModelMock.mock(),
        )

        // -- Case: currentAccount (MXN)
        cryptoTransferViewModel.currentAccount.value = null
        cryptoTransferViewModel.currentWallet.value = null
        cryptoTransferViewModel.currentAsset.value = null
        cryptoTransferViewModel.changeCurrentAccount(CryptoTransferApiMockModel.accountMXN())
        Assert.assertNotNull(cryptoTransferViewModel.currentAccount.value)
        Assert.assertEquals(cryptoTransferViewModel.currentAccount.value, CryptoTransferApiMockModel.accountMXN())
        Assert.assertTrue(cryptoTransferViewModel.currentWallets.value.isEmpty())
        Assert.assertNull(cryptoTransferViewModel.currentWallet.value)
        Assert.assertNull(cryptoTransferViewModel.currentAsset.value)
        Assert.assertFalse(cryptoTransferViewModel.isAmountInFiat.value)

        // -- Case: currentAccount (MXN)
        cryptoTransferViewModel.currentAccount.value = null
        cryptoTransferViewModel.currentWallet.value = null
        cryptoTransferViewModel.currentAsset.value = null
        cryptoTransferViewModel.changeCurrentAccount(CryptoTransferApiMockModel.accountBTC())
        Assert.assertNotNull(cryptoTransferViewModel.currentAccount.value)
        Assert.assertEquals(cryptoTransferViewModel.currentAccount.value, CryptoTransferApiMockModel.accountBTC())
        Assert.assertFalse(cryptoTransferViewModel.currentWallets.value.isEmpty())
        Assert.assertNotNull(cryptoTransferViewModel.currentWallet.value)
        Assert.assertNotNull(cryptoTransferViewModel.currentAsset.value)
        Assert.assertFalse(cryptoTransferViewModel.isAmountInFiat.value)
    }

    @Test
    fun test_getMaxAmountOfAccount() {

        // -- Given
        val cryptoTransferViewModel = CryptoTransferViewModel()

        // -- Case: Current account null
        cryptoTransferViewModel.currentAccount.value = null
        val valueOne = cryptoTransferViewModel.getMaxAmountOfAccount()
        Assert.assertEquals(valueOne, "0")

        // -- Case: Current account with null asset
        cryptoTransferViewModel.currentAccount.value = CryptoTransferApiMockModel.accountWithoutAsset()
        val valueTwo = cryptoTransferViewModel.getMaxAmountOfAccount()
        Assert.assertEquals(valueTwo, "0")

        // -- Case: Current account as MXN
        cryptoTransferViewModel.currentAccount.value = CryptoTransferApiMockModel.accountMXN()
        val valueThree = cryptoTransferViewModel.getMaxAmountOfAccount()
        Assert.assertEquals(valueThree, "0")

        // -- Case Current account BTC with balances in null
        cryptoTransferViewModel.currentAccount.value = CryptoTransferApiMockModel.accountBTCWithoutBalance()
        val valueFour = cryptoTransferViewModel.getMaxAmountOfAccount()
        Assert.assertEquals(valueFour, "0")

        // -- Case: Good
        cryptoTransferViewModel.currentAccount.value = CryptoTransferApiMockModel.accountBTC()
        val value = cryptoTransferViewModel.getMaxAmountOfAccount()
        Assert.assertEquals(value, "0.001")
    }

    // -- Quote Methods
    @Test
    fun test_createPostQuoteBankModel() {

        // -- Given
        val customerGuid = Cybrid.customerGuid
        val cryptoTransferViewModel = CryptoTransferViewModel()
        val inputAmount = "2"

        // -- Case: Current account is null
        cryptoTransferViewModel.currentAccount.value = null
        val postQuoteBankModelOne = cryptoTransferViewModel.createPostQuoteBankModel(inputAmount)
        Assert.assertNull(postQuoteBankModelOne)

        // -- Case: Current account is not null but doesn't have asset
        cryptoTransferViewModel.currentAccount.value = CryptoTransferApiMockModel.accountWithoutAsset()
        val postQuoteBankModelTwo = cryptoTransferViewModel.createPostQuoteBankModel(inputAmount)
        Assert.assertNull(postQuoteBankModelTwo)

        // -- Case: Current account is MXN account (Account not supported supported)
        cryptoTransferViewModel.currentAccount.value = CryptoTransferApiMockModel.accountMXN()
        val postQuoteBankModelThree = cryptoTransferViewModel.createPostQuoteBankModel(inputAmount)
        Assert.assertNull(postQuoteBankModelThree)

        // -- Case: Everything is good
        cryptoTransferViewModel.currentAccount.value = CryptoTransferApiMockModel.accountBTC()
        val postQuoteBankModel = cryptoTransferViewModel.createPostQuoteBankModel(inputAmount)
        Assert.assertNotNull(postQuoteBankModel)
        Assert.assertEquals(postQuoteBankModel?.productType, PostQuoteBankModel.ProductType.cryptoTransfer)
        Assert.assertEquals(postQuoteBankModel?.customerGuid, customerGuid)
        Assert.assertEquals(postQuoteBankModel?.asset, "BTC")
        Assert.assertEquals(postQuoteBankModel?.side, PostQuoteBankModel.Side.withdrawal)
        Assert.assertEquals(postQuoteBankModel?.deliverAmount, JavaBigDecimal(200000000))
    }

    @Test
    fun `test_calculatePreQuote __ currentAsset is null`() {

        // -- Given
        val cryptoTransferViewModel = CryptoTransferViewModel()

        // -- Case: currentAccount as null
        cryptoTransferViewModel.currentAsset.value = null
        cryptoTransferViewModel.modalErrorString = ""
        cryptoTransferViewModel.calculatePreQuote()
        Assert.assertFalse(cryptoTransferViewModel.preQuoteValueHasErrorState.value)
        Assert.assertEquals(cryptoTransferViewModel.modalErrorString, CryptoTransferViewModelErrors.assetNotFoundError())
        Assert.assertEquals(cryptoTransferViewModel.preQuoteValueState.value, "0")
    }

    @Test
    fun `test_calculatePreQuote __ currentAmountInput as String (Hello)`() {

        // -- Given
        val cryptoTransferViewModel = CryptoTransferViewModel()
        cryptoTransferViewModel.currentAsset.value = CryptoTransferApiMockModel.btc()

        // -- Case: currentAmountInput as String (Hello)
        cryptoTransferViewModel.modalErrorString = ""
        cryptoTransferViewModel.currentAccount.value = CryptoTransferApiMockModel.accountBTC()
        cryptoTransferViewModel.currentAmountInput.value = "Hello"
        cryptoTransferViewModel.calculatePreQuote()
        Assert.assertFalse(cryptoTransferViewModel.preQuoteValueHasErrorState.value)
        Assert.assertEquals(cryptoTransferViewModel.modalErrorString, CryptoTransferViewModelErrors.amountError())
        Assert.assertEquals(cryptoTransferViewModel.preQuoteValueState.value, "0")
    }

    @Test
    fun `test_calculatePreQuote __ sellPrice is null`() {

        // -- Given
        val cryptoTransferViewModel = CryptoTransferViewModel()
        cryptoTransferViewModel.currentAsset.value = CryptoTransferApiMockModel.btc()

        // -- Case: sellPrice is null
        cryptoTransferViewModel.modalErrorString = ""
        cryptoTransferViewModel.currentAccount.value = CryptoTransferApiMockModel.accountBTC()
        cryptoTransferViewModel.currentAmountInput.value = "1"
        cryptoTransferViewModel.prices.value = CryptoTransferApiMockModel.pricesListWithoutPrices()
        cryptoTransferViewModel.calculatePreQuote()
        Assert.assertFalse(cryptoTransferViewModel.preQuoteValueHasErrorState.value)
        Assert.assertEquals(cryptoTransferViewModel.modalErrorString, CryptoTransferViewModelErrors.priceError())
        Assert.assertEquals(cryptoTransferViewModel.preQuoteValueState.value, "0")
    }

    @Test
    fun `test_calculatePreQuote __ tradeValue is bigger than accountBalance in isTransferInFiat_value as true`() {

        // -- Given
        val cryptoTransferViewModel = CryptoTransferViewModel()
        cryptoTransferViewModel.currentAsset.value = CryptoTransferApiMockModel.btc()

        // -- Case: currentAmountInput as String (Hello)
        cryptoTransferViewModel.modalErrorString = ""
        cryptoTransferViewModel.currentAccount.value = CryptoTransferApiMockModel.accountBTC()
        cryptoTransferViewModel.currentAmountInput.value = "999999"
        cryptoTransferViewModel.prices.value = CryptoTransferApiMockModel.pricesList()
        cryptoTransferViewModel.isAmountInFiat.value = true
        cryptoTransferViewModel.calculatePreQuote()
        Assert.assertTrue(cryptoTransferViewModel.preQuoteValueHasErrorState.value)
        Assert.assertEquals(cryptoTransferViewModel.modalErrorString, "")
        Assert.assertEquals(cryptoTransferViewModel.preQuoteValueState.value, "36.51450448")
    }

    @Test
    fun `test_calculatePreQuote __ tradeValue is bigger than accountBalance in isTransferInFiat_value as false`() {

        // -- Given
        val cryptoTransferViewModel = CryptoTransferViewModel()
        cryptoTransferViewModel.currentAsset.value = CryptoTransferApiMockModel.btc()

        // -- Case: currentAmountInput as String (Hello)
        cryptoTransferViewModel.modalErrorString = ""
        cryptoTransferViewModel.currentAccount.value = CryptoTransferApiMockModel.accountBTC()
        cryptoTransferViewModel.currentAmountInput.value = "1"
        cryptoTransferViewModel.prices.value = CryptoTransferApiMockModel.pricesList()
        cryptoTransferViewModel.isAmountInFiat.value = false
        cryptoTransferViewModel.calculatePreQuote()
        Assert.assertTrue(cryptoTransferViewModel.preQuoteValueHasErrorState.value)
        Assert.assertEquals(cryptoTransferViewModel.modalErrorString, "")
        Assert.assertEquals(cryptoTransferViewModel.preQuoteValueState.value, "$27,386.35 USD")
    }

    // -- Transfer Methods
    @Test
    fun test_createPostTransferBankModel() {

        // -- Given
        val cryptoTransferViewModel = CryptoTransferViewModel()

        // -- Case: Current Quote is null
        cryptoTransferViewModel.currentQuote.value = null
        val postTransferOne = cryptoTransferViewModel.createPostTransferBankModel()
        Assert.assertNull(postTransferOne)

        // -- Case: Current wallet is null
        cryptoTransferViewModel.currentQuote.value = CryptoTransferApiMockModel.quote()
        cryptoTransferViewModel.currentWallet.value = null
        val postTransferTwo = cryptoTransferViewModel.createPostTransferBankModel()
        Assert.assertNull(postTransferTwo)

        // -- Case: Good
        cryptoTransferViewModel.currentQuote.value = CryptoTransferApiMockModel.quote()
        cryptoTransferViewModel.currentWallet.value = ExternalWalletBankModelMock.mock()
        val postTransferBankModel = cryptoTransferViewModel.createPostTransferBankModel()
        Assert.assertNotNull(postTransferBankModel)
        Assert.assertEquals(postTransferBankModel?.quoteGuid, "12345")
        Assert.assertEquals(postTransferBankModel?.transferType, PostTransferBankModel.TransferType.crypto)
        Assert.assertEquals(postTransferBankModel?.externalWalletGuid, "1234")
    }

    // -- Prices Methods
    @Test
    fun test_getPrice() {

        // -- Given
        val cryptoTransferViewModel = CryptoTransferViewModel()

        // -- Case: prices are empty
        cryptoTransferViewModel.prices.value = listOf()
        val priceOne = cryptoTransferViewModel.getPrice("BTC-USD")
        Assert.assertNull(priceOne.symbol)

        // -- Case: prices doesn't contains MXN
        cryptoTransferViewModel.prices.value = CryptoTransferApiMockModel.pricesList()
        val priceTwo = cryptoTransferViewModel.getPrice("BTC-MXN")
        Assert.assertNull(priceTwo.symbol)

        // -- Case: Good
        cryptoTransferViewModel.prices.value = CryptoTransferApiMockModel.pricesList()
        val price = cryptoTransferViewModel.getPrice("BTC-USD")
        Assert.assertNotNull(price.symbol)
    }

    // -- View Methods
    @Test
    fun test_openModal() {

        // -- Given
        val cryptoTransferViewModel = CryptoTransferViewModel()
        cryptoTransferViewModel.modalIsOpen.value = false
        cryptoTransferViewModel.modalUiState.value = CryptoTransferView.ModalState.ERROR

        // -- When
        cryptoTransferViewModel.openModal()

        // -- Then
        Assert.assertTrue(cryptoTransferViewModel.modalIsOpen.value)
        Assert.assertEquals(cryptoTransferViewModel.modalUiState.value, CryptoTransferView.ModalState.LOADING)
    }

    @Test
    fun test_closeModal() {

        // -- Given
        val cryptoTransferViewModel = CryptoTransferViewModel()
        cryptoTransferViewModel.modalIsOpen.value = true
        cryptoTransferViewModel.modalUiState.value = CryptoTransferView.ModalState.ERROR

        // -- When
        cryptoTransferViewModel.closeModal()

        // -- Then
        Assert.assertFalse(cryptoTransferViewModel.modalIsOpen.value)
        Assert.assertEquals(cryptoTransferViewModel.modalUiState.value, CryptoTransferView.ModalState.LOADING)
    }

    @Test
    fun test_maxButtonClickHandler() {

        // -- Given
        val cryptoTransferViewModel = CryptoTransferViewModel()
        cryptoTransferViewModel.currentAccount.value = CryptoTransferApiMockModel.accountBTC()

        // -- When
        cryptoTransferViewModel.maxButtonClickHandler()

        // -- Then
        Assert.assertEquals(cryptoTransferViewModel.currentAmountInput.value, "0.001")
    }

    @Test
    fun test_resetAmountInput() {

        // -- Given
        val cryptoTransferViewModel = CryptoTransferViewModel()
        cryptoTransferViewModel.currentAmountInput.value = "123"

        // -- When
        cryptoTransferViewModel.resetAmountInput("555")

        // -- Then
        Assert.assertEquals(cryptoTransferViewModel.currentAmountInput.value, "555")
    }
}