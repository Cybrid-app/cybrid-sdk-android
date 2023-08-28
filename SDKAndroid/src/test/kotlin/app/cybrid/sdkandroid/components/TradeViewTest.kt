package app.cybrid.sdkandroid.components

import android.content.Context
import android.view.LayoutInflater
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.mutableStateOf
import app.cybrid.sdkandroid.components.trade.view.TradeViewModel
import app.cybrid.sdkandroid.tools.MainDispatcherRule
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestScope
import org.junit.*

class TradeViewTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var context: Context

    @MockK
    private lateinit var layoutInflater: LayoutInflater

    private lateinit var tradeView: TradeView

    @ExperimentalCoroutinesApi
    private val scope = TestScope()

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {

        MockKAnnotations.init(this, relaxed = true)

        every { LayoutInflater.from(context) } returns layoutInflater
        every { layoutInflater.inflate(any<Int>(), any()) } returns mockk()

        tradeView = spyk(TradeView(context))
        every { tradeView.context } returns context
    }

    @Test
    fun test_init() = runBlocking {

        // -- Given
        val tradeViewModel = TradeViewModel()
        val expectState = mutableStateOf(TradeView.ViewState.LOADING)

        // -- When
        tradeView.setViewModel(tradeViewModel = tradeViewModel)

        // -- Then
        Assert.assertNotNull(tradeView)
        Assert.assertNotNull(tradeView.tradeViewModel)
        Assert.assertEquals(tradeView.tradeViewModel?.uiState?.value, expectState.value)

        expectState.value = TradeView.ViewState.LIST_PRICES
        tradeView.tradeViewModel?.uiState?.value = expectState.value
        Assert.assertEquals(tradeView.tradeViewModel?.uiState?.value, expectState.value)
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