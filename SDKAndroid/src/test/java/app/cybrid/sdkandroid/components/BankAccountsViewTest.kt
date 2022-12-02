package app.cybrid.sdkandroid.components

import android.content.Context
import android.view.LayoutInflater
import androidx.compose.runtime.mutableStateOf
import app.cybrid.sdkandroid.components.bankAccounts.view.BankAccountsViewModel
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.*

class BankAccountsViewTest {

    @MockK
    private lateinit var context: Context

    @MockK
    private lateinit var layoutInflater: LayoutInflater

    private lateinit var bankAccountsView: BankAccountsView

    @Before
    fun setup() {

        MockKAnnotations.init(this, relaxed = true)

        every { LayoutInflater.from(context) } returns layoutInflater
        every { layoutInflater.inflate(any<Int>(), any()) } returns mockk()

        bankAccountsView = BankAccountsView(context)
    }

    @Test
    fun test_init() = runBlocking {

        // -- Given
        val bankAccountsViewModel = BankAccountsViewModel()
        val expectState = mutableStateOf(BankAccountsView.BankAccountsViewState.LOADING)

        // -- When
        bankAccountsView.setViewModel(bankAccountsViewModel = bankAccountsViewModel)

        // -- Then
        Assert.assertNotNull(bankAccountsView)
        Assert.assertNotNull(bankAccountsView.bankAccountsViewModel)
        Assert.assertEquals(bankAccountsView.bankAccountsViewModel?.uiState?.value, expectState.value)

        expectState.value = BankAccountsView.BankAccountsViewState.REQUIRED
        bankAccountsView.bankAccountsViewModel?.uiState?.value = expectState.value
        Assert.assertEquals(bankAccountsView.bankAccountsViewModel?.uiState?.value, expectState.value)
    }

    companion object {

        @JvmStatic
        @BeforeClass
        fun setUpClass() {
            mockkStatic(LayoutInflater::class)
        }

        @JvmStatic
        @AfterClass
        fun tearDownClass() {
            unmockkStatic(LayoutInflater::class)
        }
    }
}