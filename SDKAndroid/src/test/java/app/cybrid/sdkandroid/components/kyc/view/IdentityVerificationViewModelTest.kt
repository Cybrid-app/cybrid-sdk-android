package app.cybrid.sdkandroid.components.kyc.view

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.mutableStateOf
import app.cybrid.cybrid_api_bank.client.infrastructure.ApiClient
import app.cybrid.cybrid_api_bank.client.models.CustomerBankModel
import app.cybrid.cybrid_api_bank.client.models.IdentityVerificationBankModel
import app.cybrid.cybrid_api_bank.client.models.IdentityVerificationWithDetailsBankModel
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.components.KYCView
import app.cybrid.sdkandroid.tools.JSONMock
import app.cybrid.sdkandroid.util.Polling
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import okhttp3.OkHttpClient
import org.junit.*

class IdentityVerificationViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    private val scope = TestScope()

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher(scope.testScheduler))
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun prepareClient(state: JSONMock.JSONMockState): ApiClient {

        val interceptor = JSONMock(state)
        val clientBuilder = OkHttpClient()
            .newBuilder().addInterceptor(interceptor)
        return ApiClient(okHttpClientBuilder = clientBuilder)
    }

    private fun createViewModel(): IdentityVerificationViewModel {

        Cybrid.instance.invalidToken = false
        val viewModel = IdentityVerificationViewModel()
        viewModel.uiState = mutableStateOf(KYCView.KYCViewState.LOADING)
        return viewModel
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_init() = runTest {

        // -- Given
        val viewModel = createViewModel()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(viewModel.customerGuid)
        Assert.assertNotNull(viewModel.uiState)
        Assert.assertNull(viewModel.latestIdentityVerification)
        Assert.assertEquals(viewModel.viewDismiss.value, false)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_init_withDataProvider() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(viewModel.customerGuid)
        Assert.assertNotNull(viewModel.uiState)
        Assert.assertNull(viewModel.latestIdentityVerification)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_createCustomerTest_Successfully() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)
        val originalCustomerGuid = viewModel.customerGuid

        // -- When
        viewModel.createCustomerTest()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotEquals(viewModel.customerGuid, originalCustomerGuid)
        Assert.assertEquals(viewModel.uiState?.value, KYCView.KYCViewState.LOADING)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_getCustomerStatus_Successfully() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)
        val originalCustomerGuid = viewModel.customerGuid

        // -- When
        viewModel.getCustomerStatus()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertEquals(viewModel.customerGuid, originalCustomerGuid)
        Assert.assertEquals(viewModel.uiState?.value, KYCView.KYCViewState.LOADING)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_fetchIdentityVerificationWithDetailsStatus_Successfully() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        val identityStatus = viewModel.fetchIdentityVerificationWithDetailsStatus("1234")

        // -- Then
        Assert.assertNotNull(identityStatus)
        Assert.assertEquals(identityStatus?.type, IdentityVerificationWithDetailsBankModel.Type.kyc)
        Assert.assertEquals(identityStatus?.guid, "1234")
        Assert.assertEquals(identityStatus?.customerGuid, "1234")
        Assert.assertEquals(identityStatus?.method, IdentityVerificationWithDetailsBankModel.Method.idAndSelfie)
        Assert.assertEquals(identityStatus?.state, IdentityVerificationWithDetailsBankModel.State.storing)
        Assert.assertNull(identityStatus?.personaInquiryId)
        Assert.assertNull(identityStatus?.personaState)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_getLastIdentityVerification_Successfully() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        val identity = viewModel.getLastIdentityVerification()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(identity)
        Assert.assertEquals(identity?.type, IdentityVerificationBankModel.Type.kyc)
        Assert.assertEquals(identity?.guid, "1234")
        Assert.assertEquals(identity?.customerGuid, "1234")
        Assert.assertEquals(identity?.method, IdentityVerificationBankModel.Method.idAndSelfie)
        Assert.assertEquals(identity?.state, IdentityVerificationBankModel.State.storing)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_getLastIdentityVerification_Successfully_Empty() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.EMPTY)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        val identity = viewModel.getLastIdentityVerification()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNull(identity)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_createIdentityVerification_Successfully() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        val identity = viewModel.createIdentityVerification()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(identity)
        Assert.assertEquals(identity?.type, IdentityVerificationBankModel.Type.kyc)
        Assert.assertEquals(identity?.guid, "1234")
        Assert.assertEquals(identity?.customerGuid, "1234")
        Assert.assertEquals(identity?.method, IdentityVerificationBankModel.Method.idAndSelfie)
        Assert.assertEquals(identity?.state, IdentityVerificationBankModel.State.storing)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_getIdentityVerificationStatus_Successfully() = runTest {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- When
        viewModel.getIdentityVerificationStatus()

        // -- Then
        Assert.assertNotNull(viewModel)
    }

    @Test
    fun test_checkCustomerStatus() {

        // -- Given
        val viewModel = createViewModel()

        // -- state: storing - UIState: LOADING
        var customer = CustomerBankModel(state = CustomerBankModel.State.storing)
        Assert.assertNull(viewModel.customerJob)
        viewModel.checkCustomerStatus(customer.state!!)
        Assert.assertNotNull(viewModel.customerJob)

        // -- state: storing - UIState: VERIFIED
        customer = CustomerBankModel(state = CustomerBankModel.State.verified)
        viewModel.customerJob = Polling {}
        viewModel.checkCustomerStatus(customer.state!!)
        Assert.assertNull(viewModel.customerJob)
        Assert.assertEquals(viewModel.uiState?.value, KYCView.KYCViewState.VERIFIED)

        // -- state: unverified - UIState: LOADING
        customer = CustomerBankModel(state = CustomerBankModel.State.unverified)
        viewModel.customerJob = Polling {}
        viewModel.checkCustomerStatus(customer.state!!)
        Assert.assertNull(viewModel.customerJob)
        Assert.assertEquals(viewModel.uiState?.value, KYCView.KYCViewState.VERIFIED)

        // -- state: rejected - UIState: LOADING
        customer = CustomerBankModel(state = CustomerBankModel.State.rejected)
        viewModel.customerJob = Polling {}
        viewModel.checkCustomerStatus(customer.state!!)
        Assert.assertNull(viewModel.customerJob)
        Assert.assertEquals(viewModel.uiState?.value, KYCView.KYCViewState.ERROR)
    }

    @Test
    fun test_checkIdentityRecordStatus() {

        // -- Given
        val viewModel = createViewModel()
        var record = IdentityVerificationViewModel.IdentityVerificationWrapper(
            identity = IdentityVerificationBankModel(state = IdentityVerificationBankModel.State.storing),
            details = IdentityVerificationWithDetailsBankModel(
                state = IdentityVerificationWithDetailsBankModel.State.storing
            )
        )

        // -- state: storing - UIState: LOADING
        Assert.assertNull(viewModel.identityJob)
        viewModel.checkIdentityRecordStatus(record)
        Assert.assertNotNull(viewModel.identityJob)
        Assert.assertEquals(viewModel.uiState?.value, KYCView.KYCViewState.LOADING)

        // -- state: waiting - personaState: completed - UIState: LOADING
        record = IdentityVerificationViewModel.IdentityVerificationWrapper(
            identity = IdentityVerificationBankModel(state = IdentityVerificationBankModel.State.waiting),
            details = IdentityVerificationWithDetailsBankModel(
                state = IdentityVerificationWithDetailsBankModel.State.waiting,
                personaState = IdentityVerificationWithDetailsBankModel.PersonaState.completed)
        )
        viewModel.identityJob = null
        viewModel.checkIdentityRecordStatus(record)
        Assert.assertNotNull(viewModel.identityJob)
        Assert.assertEquals(viewModel.uiState?.value, KYCView.KYCViewState.LOADING)

        // -- state: waiting - personaState: processing - UIState: LOADING
        record = IdentityVerificationViewModel.IdentityVerificationWrapper(
            identity = IdentityVerificationBankModel(state = IdentityVerificationBankModel.State.waiting),
            details = IdentityVerificationWithDetailsBankModel(
                state = IdentityVerificationWithDetailsBankModel.State.waiting,
                personaState = IdentityVerificationWithDetailsBankModel.PersonaState.processing)
        )
        viewModel.identityJob = null
        viewModel.checkIdentityRecordStatus(record)
        Assert.assertNotNull(viewModel.identityJob)
        Assert.assertEquals(viewModel.uiState?.value, KYCView.KYCViewState.LOADING)

        // -- state: waiting - personaState: reviewing - UIState: LOADING
        record = IdentityVerificationViewModel.IdentityVerificationWrapper(
            identity = IdentityVerificationBankModel(state = IdentityVerificationBankModel.State.waiting),
            details = IdentityVerificationWithDetailsBankModel(
                state = IdentityVerificationWithDetailsBankModel.State.waiting,
                personaState = IdentityVerificationWithDetailsBankModel.PersonaState.reviewing)
        )
        viewModel.checkIdentityRecordStatus(record)
        Assert.assertNull(viewModel.identityJob)
        Assert.assertEquals(viewModel.uiState?.value, KYCView.KYCViewState.REVIEWING)

        // -- state: expired - UIState: LOADING
        record = IdentityVerificationViewModel.IdentityVerificationWrapper(
            identity = IdentityVerificationBankModel(state = IdentityVerificationBankModel.State.expired),
            details = IdentityVerificationWithDetailsBankModel(
                state = IdentityVerificationWithDetailsBankModel.State.expired,
                personaState = null)
        )
        viewModel.uiState?.value = KYCView.KYCViewState.LOADING
        viewModel.identityJob = Polling {}
        viewModel.checkIdentityRecordStatus(record)
        Assert.assertNull(viewModel.identityJob)
        Assert.assertEquals(viewModel.uiState?.value, KYCView.KYCViewState.LOADING)

        // -- state: completed - UIState: VERIFIED
        record = IdentityVerificationViewModel.IdentityVerificationWrapper(
            identity = IdentityVerificationBankModel(state = IdentityVerificationBankModel.State.completed),
            details = IdentityVerificationWithDetailsBankModel(
                state = IdentityVerificationWithDetailsBankModel.State.completed,
                personaState = null)
        )
        viewModel.uiState?.value = KYCView.KYCViewState.LOADING
        viewModel.identityJob = Polling {}
        viewModel.checkIdentityRecordStatus(record)
        Assert.assertNull(viewModel.identityJob)
        Assert.assertEquals(viewModel.uiState?.value, KYCView.KYCViewState.VERIFIED)
    }

    @Test
    fun test_checkIdentityPersonaStatus() {

        // -- Given
        val viewModel = createViewModel()
        var record = IdentityVerificationViewModel.IdentityVerificationWrapper(
            identity = null,
            details = IdentityVerificationWithDetailsBankModel(
                personaState = IdentityVerificationWithDetailsBankModel.PersonaState.waiting
            )
        )

        // -- Persona: waiting - UIState: REQUIRED
        viewModel.checkIdentityPersonaStatus(record)
        Assert.assertEquals(viewModel.latestIdentityVerification, record)
        Assert.assertEquals(viewModel.uiState?.value, KYCView.KYCViewState.REQUIRED)

        // -- Persona: pending - UIState: REQUIRED
        record = IdentityVerificationViewModel.IdentityVerificationWrapper(
            identity = null,
            details = IdentityVerificationWithDetailsBankModel(
                personaState = IdentityVerificationWithDetailsBankModel.PersonaState.pending
            )
        )
        viewModel.uiState?.value = KYCView.KYCViewState.LOADING
        viewModel.checkIdentityPersonaStatus(record)
        Assert.assertEquals(viewModel.latestIdentityVerification, record)
        Assert.assertEquals(viewModel.uiState?.value, KYCView.KYCViewState.REQUIRED)

        // -- Persona: reviewing - UIState: REVIEWING
        record = IdentityVerificationViewModel.IdentityVerificationWrapper(
            identity = null,
            details = IdentityVerificationWithDetailsBankModel(
                personaState = IdentityVerificationWithDetailsBankModel.PersonaState.reviewing
            )
        )
        viewModel.uiState?.value = KYCView.KYCViewState.LOADING
        viewModel.checkIdentityPersonaStatus(record)
        Assert.assertEquals(viewModel.latestIdentityVerification, record)
        Assert.assertEquals(viewModel.uiState?.value, KYCView.KYCViewState.REVIEWING)

        // -- Persona: completed - UIState: ERROR
        record = IdentityVerificationViewModel.IdentityVerificationWrapper(
            identity = null,
            details = IdentityVerificationWithDetailsBankModel(
                personaState = IdentityVerificationWithDetailsBankModel.PersonaState.completed
            )
        )
        viewModel.uiState?.value = KYCView.KYCViewState.LOADING
        viewModel.checkIdentityPersonaStatus(record)
        Assert.assertEquals(viewModel.latestIdentityVerification, record)
        Assert.assertEquals(viewModel.uiState?.value, KYCView.KYCViewState.ERROR)

        // -- Persona: expired - UIState: ERROR
        record = IdentityVerificationViewModel.IdentityVerificationWrapper(
            identity = null,
            details = IdentityVerificationWithDetailsBankModel(
                personaState = IdentityVerificationWithDetailsBankModel.PersonaState.expired
            )
        )
        viewModel.uiState?.value = KYCView.KYCViewState.LOADING
        viewModel.checkIdentityPersonaStatus(record)
        Assert.assertEquals(viewModel.latestIdentityVerification, record)
        Assert.assertEquals(viewModel.uiState?.value, KYCView.KYCViewState.LOADING)

        // -- Persona: processing - UIState: ERROR
        record = IdentityVerificationViewModel.IdentityVerificationWrapper(
            identity = null,
            details = IdentityVerificationWithDetailsBankModel(
                personaState = IdentityVerificationWithDetailsBankModel.PersonaState.processing
            )
        )
        viewModel.uiState?.value = KYCView.KYCViewState.LOADING
        viewModel.checkIdentityPersonaStatus(record)
        Assert.assertEquals(viewModel.latestIdentityVerification, record)
        Assert.assertEquals(viewModel.uiState?.value, KYCView.KYCViewState.ERROR)

        // -- Persona: unknown - UIState: ERROR
        record = IdentityVerificationViewModel.IdentityVerificationWrapper(
            identity = null,
            details = IdentityVerificationWithDetailsBankModel(
                personaState = IdentityVerificationWithDetailsBankModel.PersonaState.unknown
            )
        )
        viewModel.uiState?.value = KYCView.KYCViewState.LOADING
        viewModel.checkIdentityPersonaStatus(record)
        Assert.assertEquals(viewModel.latestIdentityVerification, record)
        Assert.assertEquals(viewModel.uiState?.value, KYCView.KYCViewState.ERROR)
    }

    @Test
    fun test_dismissView() {

        // -- Given
        val viewModel = createViewModel()

        // -- When
        viewModel.dismissView()

        // -- Then
        Assert.assertEquals(viewModel.viewDismiss.value, true)
    }
}