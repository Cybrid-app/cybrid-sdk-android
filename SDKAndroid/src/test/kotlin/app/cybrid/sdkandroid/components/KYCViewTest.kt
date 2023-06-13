package app.cybrid.sdkandroid.components

import android.content.Context
import android.view.LayoutInflater
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.mutableStateOf
import app.cybrid.sdkandroid.components.kyc.view.IdentityVerificationViewModel
import app.cybrid.sdkandroid.tools.MainDispatcherRule
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import org.junit.*

@ExperimentalCoroutinesApi
class KYCViewTest {

    @MockK
    private lateinit var context: Context

    @MockK
    private lateinit var layoutInflater: LayoutInflater

    private lateinit var kycView: KYCView

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {

        MockKAnnotations.init(this, relaxed = true)

        every { LayoutInflater.from(context) } returns layoutInflater
        every { layoutInflater.inflate(any<Int>(), any()) } returns mockk()

        kycView = KYCView(context)
    }


    @Test
    fun test_init() = runBlocking {

        // -- Given
        val identityVerificationViewModel = IdentityVerificationViewModel()
        val expectState = mutableStateOf(KYCView.KYCViewState.LOADING)

        // -- When
        kycView.setViewModel(identityViewModel = identityVerificationViewModel)

        // -- Then
        Assert.assertNotNull(kycView)
        Assert.assertNotNull(kycView.identityViewModel)
        Assert.assertEquals(kycView.identityViewModel?.uiState?.value, expectState.value)

        expectState.value = KYCView.KYCViewState.REQUIRED
        kycView.identityViewModel?.uiState?.value = expectState.value
        Assert.assertEquals(kycView.identityViewModel?.uiState?.value, expectState.value)
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