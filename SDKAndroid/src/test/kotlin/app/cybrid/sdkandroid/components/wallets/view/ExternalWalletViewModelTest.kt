package app.cybrid.sdkandroid.components.wallets.view

import app.cybrid.sdkandroid.BaseTest
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.components.ExternalWalletsView
import app.cybrid.sdkandroid.mock.ExternalWalletBankModelMock
import app.cybrid.sdkandroid.mock.ExternalWalletsApiMock
import io.mockk.MockKAnnotations
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
}