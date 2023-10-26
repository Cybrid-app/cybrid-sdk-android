package app.cybrid.sdkandroid.components.wallets.view

import app.cybrid.cybrid_api_bank.client.models.PostExternalWalletBankModel
import app.cybrid.sdkandroid.BaseTest
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.components.ExternalWalletsView
import app.cybrid.sdkandroid.mock.ExternalWalletBankModelMock
import app.cybrid.sdkandroid.mock.ExternalWalletsApiMock
import app.cybrid.sdkandroid.mock.TransferBankModelMock
import app.cybrid.sdkandroid.mock.TransfersApiMock
import io.mockk.coVerify
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class ExternalWalletViewModelTest: BaseTest() {

    @Before
    override fun setup() {

        super.setup()
        Cybrid.customerGuid = "1234"
        Cybrid.invalidToken = false
        Cybrid.bearer = "1234"
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_fetchExternalWallets() = runTest {

        // -- Given
        val customerGuid = Cybrid.customerGuid
        val walletListResponse = ExternalWalletBankModelMock.list(listOf(
            ExternalWalletBankModelMock.mock()
        ))
        val response = Response.success(walletListResponse)

        // -- Mock
        val externalWalletViewModel = spyk(ExternalWalletViewModel())
        val mockExternalWalletApi = ExternalWalletsApiMock.mock_listExternalWallets(response)

        // -- When
        externalWalletViewModel.fetchExternalWallets()

        // -- Verify
        coVerify { mockExternalWalletApi.listExternalWallets(customerGuid = customerGuid) }

        // -- Then
        Assert.assertFalse(externalWalletViewModel.externalWallets.isEmpty())
        Assert.assertEquals(externalWalletViewModel.externalWallets.count(), 1)
        Assert.assertFalse(externalWalletViewModel.externalWalletsActive.isEmpty())
        Assert.assertEquals(externalWalletViewModel.externalWalletsActive.count(), 1)
        Assert.assertEquals(externalWalletViewModel.uiState.value, ExternalWalletsView.State.WALLETS)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_fetchExternalWallets_With_Deleted_Wallets() = runTest {

        // -- Given
        val customerGuid = Cybrid.customerGuid
        val walletListResponse = ExternalWalletBankModelMock.list(listOf(
            ExternalWalletBankModelMock.mock_deleted(),
            ExternalWalletBankModelMock.mock_deleting()
        ))
        val response = Response.success(walletListResponse)

        // -- Mock
        val externalWalletViewModel = spyk(ExternalWalletViewModel())
        val mockExternalWalletApi = ExternalWalletsApiMock.mock_listExternalWallets(response)

        // -- When
        externalWalletViewModel.fetchExternalWallets()

        // -- Verify
        coVerify { mockExternalWalletApi.listExternalWallets(customerGuid = customerGuid) }

        // -- Then
        Assert.assertFalse(externalWalletViewModel.externalWallets.isEmpty())
        Assert.assertEquals(externalWalletViewModel.externalWallets.count(), 2)
        Assert.assertTrue(externalWalletViewModel.externalWalletsActive.isEmpty())
        Assert.assertEquals(externalWalletViewModel.externalWalletsActive.count(), 0)
        Assert.assertEquals(externalWalletViewModel.uiState.value, ExternalWalletsView.State.WALLETS)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_createWallet() = runTest {

        // -- Given
        val customerGuid = Cybrid.customerGuid
        val walletResponse = ExternalWalletBankModelMock.mock()
        val response = Response.success(walletResponse)
        val postExternalWalletBankModel = PostExternalWalletBankModel(
            name = "Test",
            asset = "BTC",
            address = "0x1234",
            customerGuid = customerGuid,
            tag = null
        )

        // -- Mock
        val externalWalletViewModel = spyk(ExternalWalletViewModel())
        val mockExternalWalletApi = ExternalWalletsApiMock.mock_createExternalWallet(response)

        // -- When
        externalWalletViewModel.createWallet(postExternalWalletBankModel)

        // -- Verify
        coVerify { mockExternalWalletApi.createExternalWallet(postExternalWalletBankModel = postExternalWalletBankModel) }
        coVerify { mockExternalWalletApi.listExternalWallets(customerGuid = customerGuid) }

        // -- Then
        Assert.assertFalse(externalWalletViewModel.externalWallets.isEmpty())
        Assert.assertEquals(externalWalletViewModel.externalWallets.count(), 1)
        Assert.assertFalse(externalWalletViewModel.externalWalletsActive.isEmpty())
        Assert.assertEquals(externalWalletViewModel.externalWalletsActive.count(), 1)
        Assert.assertEquals(externalWalletViewModel.uiState.value, ExternalWalletsView.State.WALLETS)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_deleteExternalWallet() = runTest {

        // -- Given
        val customerGuid = Cybrid.customerGuid
        val walletResponse = ExternalWalletBankModelMock.mock()
        val response = Response.success(walletResponse)
        val currentWallet = ExternalWalletBankModelMock.mock()

        // -- Mock
        val externalWalletViewModel = spyk(ExternalWalletViewModel())
        externalWalletViewModel.currentWallet = currentWallet
        val mockExternalWalletApi = ExternalWalletsApiMock.mock_deleteExternalWallet(response)

        // -- When
        externalWalletViewModel.deleteExternalWallet()

        // -- Verify
        coVerify { mockExternalWalletApi.deleteExternalWallet(externalWalletGuid = currentWallet.guid!!) }
        coVerify { mockExternalWalletApi.listExternalWallets(customerGuid = customerGuid) }

        // -- Then
        Assert.assertFalse(externalWalletViewModel.externalWallets.isEmpty())
        Assert.assertEquals(externalWalletViewModel.externalWallets.count(), 1)
        Assert.assertTrue(externalWalletViewModel.externalWalletsActive.isEmpty())
        Assert.assertEquals(externalWalletViewModel.externalWalletsActive.count(), 0)
        Assert.assertNull(externalWalletViewModel.currentWallet)
        Assert.assertEquals(externalWalletViewModel.uiState.value, ExternalWalletsView.State.WALLETS)
    }

    @Test
    fun test_goToWalletDetail() {

        // -- Given
        val externalWalletViewModel = ExternalWalletViewModel()
        val wallet = ExternalWalletBankModelMock.mock()

        // -- When
        externalWalletViewModel.goToWalletDetail(wallet)

        // -- Then
        Assert.assertNotNull(externalWalletViewModel.currentWallet)
        Assert.assertEquals(externalWalletViewModel.currentWallet, wallet)
        Assert.assertEquals(externalWalletViewModel.uiState.value, ExternalWalletsView.State.WALLET)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_fetchTransfers() = runTest {

        // -- Given
        val customerGuid = Cybrid.customerGuid
        val transfersResponse = TransferBankModelMock.list(listOf(
            TransferBankModelMock.mock(),
            TransferBankModelMock.mock_with_wallet()
        ))
        val response = Response.success(transfersResponse)

        // -- Mock
        val externalWalletViewModel = spyk(ExternalWalletViewModel())
        val mockTransfersApi = TransfersApiMock.mock_listTransfers(response)

        // -- When
        externalWalletViewModel.currentWallet = ExternalWalletBankModelMock.mock()
        externalWalletViewModel.fetchTransfers()

        // -- Verify
        coVerify { mockTransfersApi.listTransfers(customerGuid = customerGuid) }

        // -- Then
        Assert.assertTrue(externalWalletViewModel.transfers.value.isNotEmpty())
        Assert.assertEquals(externalWalletViewModel.transfers.value.count(), 1)
    }

    @Test
    fun test_handleQRScanned_Without_Dots() {

        // -- Given
        val externalWalletViewModel = ExternalWalletViewModel()
        val code = "123456"

        // -- When
        externalWalletViewModel.handleQRScanned(code)

        // -- Then
        Assert.assertEquals(externalWalletViewModel.addressScannedValue.value, code)
    }

    @Test
    fun test_handleQRScanned_With_Dots() {

        // -- Given
        val externalWalletViewModel = ExternalWalletViewModel()
        val codeOne = "bitcoin:123456"
        val codeTwo = "bitcoin:98765&tag=234"

        // -- When Case 1
        externalWalletViewModel.handleQRScanned(codeOne)
        Assert.assertEquals(externalWalletViewModel.addressScannedValue.value, "123456")

        // -- When Case 2
        externalWalletViewModel.handleQRScanned(codeTwo)
        Assert.assertEquals(externalWalletViewModel.addressScannedValue.value, "98765")
        Assert.assertEquals(externalWalletViewModel.tagScannedValue.value, "234")
    }

    @Test
    fun test_getTransfersOfTheWallet() {

        // -- Given
        val externalWalletViewModel = ExternalWalletViewModel()
        val transfersComplete = listOf(
            TransferBankModelMock.mock(),
            TransferBankModelMock.mock_with_wallet()
        )
        val transfersWithoutWallet = listOf( TransferBankModelMock.mock() )

        // -- Case: currentWallet is null
        externalWalletViewModel.currentWallet = null
        externalWalletViewModel.getTransfersOfTheWallet(transfersComplete)
        Assert.assertTrue(externalWalletViewModel.transfers.value.isEmpty())
        Assert.assertEquals(externalWalletViewModel.transfersUiState.value, ExternalWalletsView.TransfersState.EMPTY)

        // -- Case: Transfers without matching wallet
        externalWalletViewModel.currentWallet = ExternalWalletBankModelMock.mock()
        externalWalletViewModel.getTransfersOfTheWallet(transfersWithoutWallet)
        Assert.assertTrue(externalWalletViewModel.transfers.value.isEmpty())
        Assert.assertEquals(externalWalletViewModel.transfersUiState.value, ExternalWalletsView.TransfersState.EMPTY)

        // -- Case: Transfers with matching wallet
        externalWalletViewModel.currentWallet = ExternalWalletBankModelMock.mock()
        externalWalletViewModel.getTransfersOfTheWallet(transfersComplete)
        Assert.assertFalse(externalWalletViewModel.transfers.value.isEmpty())
        Assert.assertEquals(externalWalletViewModel.transfers.value.count(), 1)
        Assert.assertEquals(externalWalletViewModel.transfersUiState.value, ExternalWalletsView.TransfersState.TRANSFERS)
    }
}