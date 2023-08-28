package app.cybrid.sdkandroid.components

import android.content.Context
import android.view.LayoutInflater
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.mutableStateOf
import app.cybrid.sdkandroid.components.bankAccounts.view.BankAccountsViewModel
import app.cybrid.sdkandroid.tools.MainDispatcherRule
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.*

class BankAccountsViewTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var context: Context

    @MockK
    private lateinit var layoutInflater: LayoutInflater

    private lateinit var bankAccountsView: BankAccountsView

    @ExperimentalCoroutinesApi
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
        val expectState = mutableStateOf(BankAccountsView.State.LOADING)

        // -- When
        bankAccountsView.setViewModel(bankAccountsViewModel = bankAccountsViewModel)

        // -- Then
        Assert.assertNotNull(bankAccountsView)
        Assert.assertNotNull(bankAccountsView.bankAccountsViewModel)
        Assert.assertEquals(bankAccountsView.bankAccountsViewModel?.uiState?.value, expectState.value)

        expectState.value = BankAccountsView.State.CONTENT
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