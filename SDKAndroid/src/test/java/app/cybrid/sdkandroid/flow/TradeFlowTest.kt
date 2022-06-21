package app.cybrid.sdkandroid.flow

import android.content.Context
import android.view.LayoutInflater
import app.cybrid.sdkandroid.components.ListPricesView
import app.cybrid.sdkandroid.components.ListPricesViewType
import app.cybrid.sdkandroid.components.listprices.view.ListPricesViewModel
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.*

class TradeFlowTest {

    @MockK private lateinit var context: Context
    @MockK private lateinit var layoutInflater: LayoutInflater

    private lateinit var tradeFlow: TradeFlow

    @Before
    fun setup() {

        MockKAnnotations.init(this, relaxed = true)

        every { LayoutInflater.from(context) } returns layoutInflater
        every { layoutInflater.inflate(any<Int>(), any()) } returns mockk()

        tradeFlow = spyk(TradeFlow(context))

        every { tradeFlow.context } returns context
        every { tradeFlow.listPricesView } returns ListPricesView(context)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testInit() {

        val listPricesVM = ListPricesViewModel()

        Assert.assertNotNull(tradeFlow.listPricesView)
        Assert.assertNotNull(listPricesVM)

        tradeFlow.setListPricesViewModel(listPricesVM)
        Assert.assertEquals(tradeFlow.listPricesView?.type, ListPricesViewType.Normal)
        Assert.assertNotNull(tradeFlow.listPricesView?.onClick)
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