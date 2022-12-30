package app.cybrid.sdkandroid.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.constraintlayout.compose.ConstraintLayout
import app.cybrid.cybrid_api_bank.client.models.ExternalBankAccountBankModel
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.bankTransfer.compose.BankTransferView_Accounts
import app.cybrid.sdkandroid.components.bankTransfer.compose.BankTransferView_Loading
import app.cybrid.sdkandroid.components.bankTransfer.modal.BankTransferModal
import app.cybrid.sdkandroid.components.bankTransfer.view.BankTransferViewModel
import app.cybrid.sdkandroid.core.Constants
import app.cybrid.sdkandroid.ui.Theme.robotoFont
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BankTransferView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0):
Component(context, attrs, defStyle) {

    enum class ViewState { LOADING, IN_LIST }

    private var currentState = mutableStateOf(ViewState.LOADING)
    var bankTransferViewModel: BankTransferViewModel? = null

    init {

        LayoutInflater.from(context).inflate(R.layout.bank_transfer_component, this, true)
        this.composeView = findViewById(R.id.composeContent)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun setViewModel(bankTransferViewModel: BankTransferViewModel) {

        this.bankTransferViewModel = bankTransferViewModel
        this.currentState = bankTransferViewModel.uiState
        this.initComposeView()
        GlobalScope.launch {

            bankTransferViewModel.fetchAccounts()
            bankTransferViewModel.fetchExternalAccounts()
        }
    }

    private fun initComposeView() {

        this.composeView?.let { compose ->
            compose.setContent {
                BankTransferView(
                    currentState = currentState,
                    bankTransferViewModel = bankTransferViewModel
                )
            }
        }
    }
}

/**
 * Composable Function for Bank Transfer
 **/

@Composable
fun BankTransferView(
    currentState: MutableState<BankTransferView.ViewState>,
    bankTransferViewModel: BankTransferViewModel?
) {

    // -- Vars for views
    val showDialog = remember { mutableStateOf(false) }
    val selectedTabIndex = remember { mutableStateOf(0) }
    val externalBankAccount: MutableState<ExternalBankAccountBankModel?> = remember { mutableStateOf(null) }
    val amountMutableState = remember { mutableStateOf("") }

    // -- Content
    Surface(modifier = Modifier.testTag(Constants.TransferView.Surface.id)) {

        BankTransferView_Accounts(
            bankTransferViewModel = bankTransferViewModel,
            selectedTabIndex = selectedTabIndex,
            externalBankAccount = externalBankAccount,
            amountMutableState = amountMutableState,
            showDialog = showDialog
        )

        // -- UIState
        /*when(currentState.value) {

            BankTransferView.ViewState.LOADING -> {
                BankTransferView_Loading()
            }

            BankTransferView.ViewState.IN_LIST -> {
                BankTransferView_List(
                    bankTransferViewModel = bankTransferViewModel
                )
            }
        }*/

        // -- Dialog
        if (showDialog.value) {
            BankTransferModal(
                bankTransferViewModel = bankTransferViewModel,
                externalBankAccount = externalBankAccount.value,
                showDialog = showDialog,
                selectedTabIndex = selectedTabIndex,
                amountMutableState = amountMutableState
            )
        }
    }
}