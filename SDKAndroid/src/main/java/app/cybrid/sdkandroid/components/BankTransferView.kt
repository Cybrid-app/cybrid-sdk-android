package app.cybrid.sdkandroid.components

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.accounts.view.AccountsViewModel
import app.cybrid.sdkandroid.components.activity.BankTransferActivity
import app.cybrid.sdkandroid.components.bankAccounts.compose.BankAccountsView_Done
import app.cybrid.sdkandroid.components.bankAccounts.compose.BankAccountsView_Error
import app.cybrid.sdkandroid.components.bankAccounts.compose.BankAccountsView_Loading
import app.cybrid.sdkandroid.components.bankAccounts.compose.BankAccountsView_Required
import app.cybrid.sdkandroid.components.bankAccounts.view.BankAccountsViewModel
import app.cybrid.sdkandroid.components.bankTransfer.view.BankTransferViewModel
import app.cybrid.sdkandroid.core.Constants
import app.cybrid.sdkandroid.ui.Theme.robotoFont
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

    // -- Content
    Surface {

        // -- UIState
        when(currentState.value) {

            BankTransferView.ViewState.LOADING -> {
                BankTransferView_Loading()
            }

            BankTransferView.ViewState.IN_LIST -> {
                BankTransferView_List(
                    bankTransferViewModel = bankTransferViewModel
                )
            }
        }
    }
}

@Composable
fun BankTransferView_Loading() {

    Box(
        modifier = Modifier
            .height(120.dp)
            .testTag(Constants.BankAccountsView.LoadingView.id)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Loading",
                fontFamily = robotoFont,
                fontWeight = FontWeight.Normal,
                fontSize = 17.sp,
                color = colorResource(id = R.color.primary_color)
            )
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .testTag(Constants.BankAccountsView.LoadingViewIndicator.id),
                color = colorResource(id = R.color.primary_color)
            )
        }
    }
}

@Composable
fun BankTransferView_List(bankTransferViewModel: BankTransferViewModel?) {

    // -- Vars
    bankTransferViewModel?.calculateFiatBalance()

    // -- Compose
    Column(
        modifier = Modifier
            .background(Color.Transparent)
            .testTag(Constants.AccountsViewTestTags.List.id)
    ) {
        ConstraintLayout(
            Modifier.fillMaxSize()
        ) {

            val (balance) = createRefs()

            BankTransferView_List_Balance(
                bankTransferViewModel = bankTransferViewModel,
                modifier = Modifier.constrainAs(balance) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(parent.top, margin = 0.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                }
            )
        }
    }
}

@Composable
fun BankTransferView_List_Balance(
    bankTransferViewModel: BankTransferViewModel?,
    modifier: Modifier
) {

    // -- Vars
    val balanceFormatted = buildAnnotatedString {
        append(bankTransferViewModel?.fiatBalance ?: "")
        withStyle(style = SpanStyle(
            color = colorResource(id = R.color.list_prices_asset_component_code_color),
            fontFamily = robotoFont,
            fontWeight = FontWeight.Normal,
            fontSize = 17.sp
        )
        ) {
            append(" ${bankTransferViewModel?.currentFiatCurrency}")
        }
    }

    // -- Content
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 25.5.dp)
    ) {

        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Available to Trade",
                modifier = Modifier,
                fontFamily = robotoFont,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = colorResource(id = R.color.accounts_view_balance_title)
            )

            Text(
                text = balanceFormatted,
                modifier = Modifier.
                padding(top = 1.dp),
                fontFamily = robotoFont,
                fontWeight = FontWeight.Normal,
                fontSize = 23.sp,
                lineHeight = 32.sp,
                color = Color.Black
            )
        }
    }
}