package app.cybrid.sdkandroid.components.kyc.view

import androidx.compose.runtime.mutableStateOf
import app.cybrid.cybrid_api_bank.client.infrastructure.ApiClient
import app.cybrid.cybrid_api_bank.client.models.CustomerBankModel
import app.cybrid.cybrid_api_bank.client.models.IdentityVerificationBankModel
import app.cybrid.sdkandroid.components.KYCView
import app.cybrid.sdkandroid.tools.JSONMock
import app.cybrid.sdkandroid.util.Polling
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class IdentityVerificationViewModelTest {

    private val dispatcher = TestCoroutineDispatcher()

    private fun prepareClient(state: JSONMock.JSONMockState): ApiClient {

        val interceptor = JSONMock(state)
        val clientBuilder = OkHttpClient()
            .newBuilder().addInterceptor(interceptor)
        return ApiClient(okHttpClientBuilder = clientBuilder)
    }

    private fun createViewModel(): IdentityVerificationViewModel {

        val viewModel = IdentityVerificationViewModel()
        viewModel.UIState = mutableStateOf(KYCView.KYCViewState.LOADING)
        return viewModel
    }

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun test_init() = runBlocking {

        // -- Given
        val viewModel = createViewModel()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(viewModel.customerGuid)
        Assert.assertNotNull(viewModel.UIState)
        Assert.assertNull(viewModel.latestIdentityVerification)
    }

    @Test
    fun test_init_withDataProvider() = runBlocking {

        // -- Given
        val dataProvider = prepareClient(JSONMock.JSONMockState.SUCCESS)
        val viewModel = createViewModel()
        viewModel.setDataProvider(dataProvider)

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(viewModel.customerGuid)
        Assert.assertNotNull(viewModel.UIState)
        Assert.assertNull(viewModel.latestIdentityVerification)
    }

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
        Assert.assertEquals(viewModel.UIState?.value, KYCView.KYCViewState.LOADING)
    }

    @Test
    fun test_getCustomerStatus_Successfully() = runBlocking {

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
        Assert.assertEquals(viewModel.UIState?.value, KYCView.KYCViewState.LOADING)
    }

    @Test
    fun test_getIdentityVerificationStatus_Successfully() = runBlocking {

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
    fun test_getLastIdentityVerification_Successfully() = runBlocking {

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
        Assert.assertNull(identity?.personaInquiryId)
        Assert.assertNull(identity?.personaState)
    }

    @Test
    fun test_getLastIdentityVerification_Successfully_Empty() = runBlocking {

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

    @Test
    fun test_createIdentityVerification_Successfully() = runBlocking {

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
        Assert.assertNull(identity?.personaInquiryId)
        Assert.assertNull(identity?.personaState)
    }

    @Test
    fun test_checkCustomerStatus() {

        // -- Given
        val viewModel = createViewModel()
        var customer = CustomerBankModel()

        // -- state: storing - UIState: LOADING
        customer = CustomerBankModel(state = CustomerBankModel.State.storing)
        Assert.assertNull(viewModel.customerJob)
        viewModel.checkCustomerStatus(customer.state!!)
        Assert.assertNotNull(viewModel.customerJob)

        // -- state: storing - UIState: VERIFIED
        customer = CustomerBankModel(state = CustomerBankModel.State.verified)
        viewModel.customerJob = Polling {}
        viewModel.checkCustomerStatus(customer.state!!)
        Assert.assertNull(viewModel.customerJob)
        Assert.assertEquals(viewModel.UIState?.value, KYCView.KYCViewState.VERIFIED)

        // -- state: unverified - UIState: LOADING
        customer = CustomerBankModel(state = CustomerBankModel.State.unverified)
        viewModel.customerJob = Polling {}
        viewModel.checkCustomerStatus(customer.state!!)
        Assert.assertNull(viewModel.customerJob)
        Assert.assertEquals(viewModel.UIState?.value, KYCView.KYCViewState.VERIFIED)

        // -- state: rejected - UIState: LOADING
        customer = CustomerBankModel(state = CustomerBankModel.State.rejected)
        viewModel.customerJob = Polling {}
        viewModel.checkCustomerStatus(customer.state!!)
        Assert.assertNull(viewModel.customerJob)
        Assert.assertEquals(viewModel.UIState?.value, KYCView.KYCViewState.ERROR)
    }

    @Test
    fun test_checkIdentityRecordStatus() {

        // -- Given
        val viewModel = createViewModel()
        var record = IdentityVerificationBankModel(state = IdentityVerificationBankModel.State.storing)

        // -- state: storing - UIState: LOADING
        Assert.assertNull(viewModel.identityJob)
        viewModel.checkIdentityRecordStatus(record)
        Assert.assertNotNull(viewModel.identityJob)
        Assert.assertEquals(viewModel.UIState?.value, KYCView.KYCViewState.LOADING)

        // -- state: waiting - personaState: completed - UIState: LOADING
        record = IdentityVerificationBankModel(
            state = IdentityVerificationBankModel.State.waiting,
            personaState = IdentityVerificationBankModel.PersonaState.completed)
        viewModel.identityJob = null
        viewModel.checkIdentityRecordStatus(record)
        Assert.assertNotNull(viewModel.identityJob)
        Assert.assertEquals(viewModel.UIState?.value, KYCView.KYCViewState.LOADING)

        // -- state: waiting - personaState: processing - UIState: LOADING
        record = IdentityVerificationBankModel(
            state = IdentityVerificationBankModel.State.waiting,
            personaState = IdentityVerificationBankModel.PersonaState.processing)
        viewModel.identityJob = null
        viewModel.checkIdentityRecordStatus(record)
        Assert.assertNotNull(viewModel.identityJob)
        Assert.assertEquals(viewModel.UIState?.value, KYCView.KYCViewState.LOADING)

        // -- state: waiting - personaState: reviewing - UIState: LOADING
        record = IdentityVerificationBankModel(
            state = IdentityVerificationBankModel.State.waiting,
            personaState = IdentityVerificationBankModel.PersonaState.reviewing)
        viewModel.checkIdentityRecordStatus(record)
        Assert.assertNull(viewModel.identityJob)
        Assert.assertEquals(viewModel.UIState?.value, KYCView.KYCViewState.REVIEWING)

        // -- state: expired - UIState: LOADING
        record = IdentityVerificationBankModel(
            state = IdentityVerificationBankModel.State.expired)
        viewModel.UIState?.value = KYCView.KYCViewState.LOADING
        viewModel.identityJob = Polling {}
        viewModel.checkIdentityRecordStatus(record)
        Assert.assertNull(viewModel.identityJob)
        Assert.assertEquals(viewModel.UIState?.value, KYCView.KYCViewState.LOADING)

        // -- state: completed - UIState: VERIFIED
        record = IdentityVerificationBankModel(
            state = IdentityVerificationBankModel.State.completed)
        viewModel.UIState?.value = KYCView.KYCViewState.LOADING
        viewModel.identityJob = Polling {}
        viewModel.checkIdentityRecordStatus(record)
        Assert.assertNull(viewModel.identityJob)
        Assert.assertEquals(viewModel.UIState?.value, KYCView.KYCViewState.VERIFIED)
    }

    @Test
    fun test_checkIdentityPersonaStatus() {

        // -- Given
        val viewModel = createViewModel()
        var record = IdentityVerificationBankModel(personaState = IdentityVerificationBankModel.PersonaState.waiting)

        // -- Persona: waiting - UIState: REQUIRED
        viewModel.checkIdentityPersonaStatus(record)
        Assert.assertEquals(viewModel.latestIdentityVerification, record)
        Assert.assertEquals(viewModel.UIState?.value, KYCView.KYCViewState.REQUIRED)

        // -- Persona: pending - UIState: REQUIRED
        record = IdentityVerificationBankModel(personaState = IdentityVerificationBankModel.PersonaState.pending)
        viewModel.UIState?.value = KYCView.KYCViewState.LOADING
        viewModel.checkIdentityPersonaStatus(record)
        Assert.assertEquals(viewModel.latestIdentityVerification, record)
        Assert.assertEquals(viewModel.UIState?.value, KYCView.KYCViewState.REQUIRED)

        // -- Persona: reviewing - UIState: REVIEWING
        record = IdentityVerificationBankModel(personaState = IdentityVerificationBankModel.PersonaState.reviewing)
        viewModel.UIState?.value = KYCView.KYCViewState.LOADING
        viewModel.checkIdentityPersonaStatus(record)
        Assert.assertEquals(viewModel.latestIdentityVerification, record)
        Assert.assertEquals(viewModel.UIState?.value, KYCView.KYCViewState.REVIEWING)

        // -- Persona: completed - UIState: ERROR
        record = IdentityVerificationBankModel(personaState = IdentityVerificationBankModel.PersonaState.completed)
        viewModel.UIState?.value = KYCView.KYCViewState.LOADING
        viewModel.checkIdentityPersonaStatus(record)
        Assert.assertEquals(viewModel.latestIdentityVerification, record)
        Assert.assertEquals(viewModel.UIState?.value, KYCView.KYCViewState.ERROR)

        // -- Persona: expired - UIState: ERROR
        record = IdentityVerificationBankModel(personaState = IdentityVerificationBankModel.PersonaState.expired)
        viewModel.UIState?.value = KYCView.KYCViewState.LOADING
        viewModel.checkIdentityPersonaStatus(record)
        Assert.assertEquals(viewModel.latestIdentityVerification, record)
        Assert.assertEquals(viewModel.UIState?.value, KYCView.KYCViewState.ERROR)

        // -- Persona: processing - UIState: ERROR
        record = IdentityVerificationBankModel(personaState = IdentityVerificationBankModel.PersonaState.processing)
        viewModel.UIState?.value = KYCView.KYCViewState.LOADING
        viewModel.checkIdentityPersonaStatus(record)
        Assert.assertEquals(viewModel.latestIdentityVerification, record)
        Assert.assertEquals(viewModel.UIState?.value, KYCView.KYCViewState.ERROR)

        // -- Persona: unknown - UIState: ERROR
        record = IdentityVerificationBankModel(personaState = IdentityVerificationBankModel.PersonaState.unknown)
        viewModel.UIState?.value = KYCView.KYCViewState.LOADING
        viewModel.checkIdentityPersonaStatus(record)
        Assert.assertEquals(viewModel.latestIdentityVerification, record)
        Assert.assertEquals(viewModel.UIState?.value, KYCView.KYCViewState.ERROR)
    }
}