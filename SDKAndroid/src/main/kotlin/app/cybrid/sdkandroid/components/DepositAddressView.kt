package app.cybrid.sdkandroid.components

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewModelScope
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.accounts.compose.AccountsView_List
import app.cybrid.sdkandroid.components.accounts.compose.AccountsView_List_Empty
import app.cybrid.sdkandroid.components.accounts.compose.AccountsView_Loading
import app.cybrid.sdkandroid.components.accounts.compose.AccountsView_Trades
import app.cybrid.sdkandroid.components.accounts.compose.AccountsView_Trades_Detail
import app.cybrid.sdkandroid.components.accounts.compose.AccountsView_Transfers
import app.cybrid.sdkandroid.components.accounts.view.AccountsViewModel
import app.cybrid.sdkandroid.components.activity.DepositAddressActivity
import app.cybrid.sdkandroid.components.listprices.view.ListPricesViewModel
import app.cybrid.sdkandroid.components.transfer.view.TransferViewModel
import app.cybrid.sdkandroid.core.Constants
import app.cybrid.sdkandroid.ui.Theme.interFont
import app.cybrid.sdkandroid.ui.Theme.robotoFont
import app.cybrid.sdkandroid.util.generateQRCode
import kotlinx.coroutines.launch

class DepositAddressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0):
Component(context, attrs, defStyle) {

    enum class State { LOADING, CONTENT, ERROR  }

    private var currentState = mutableStateOf(State.LOADING)
    // private var accountsViewModel: AccountsViewModel? = null

    init {

        LayoutInflater.from(context).inflate(R.layout.accounts_component, this, true)
        this.composeView = findViewById(R.id.composeContent)
    }

    fun setViewModel() {

        //this.transferViewModel = transferViewModel

        //this.currentState = accountsViewModel.uiState
        this.currentState.value = State.CONTENT
        this.initComposeView()

        /*accountsViewModel.viewModelScope.launch {
            accountsViewModel.getAccountsList()
        }*/
    }

    private fun initComposeView() {

        this.composeView?.let { compose ->
            compose.setContent {
                DepositAddressView(
                    currentState = this.currentState
                    //accountsViewModel = this.accountsViewModel,
                    //transferViewModel = this.transferViewModel
                )
            }
        }
    }
}

@Composable
fun DepositAddressView(
    currentState: MutableState<DepositAddressView.State>
) {

    // -- Content
    Surface(
        modifier = Modifier
            .testTag(Constants.AccountsViewTestTags.Surface.id)
    ) {

        when(currentState.value) {

            DepositAddressView.State.LOADING -> {
                DepositAddressView_Loading()
            }

            DepositAddressView.State.CONTENT -> {
                DepositAddressView_Content()
            }

            DepositAddressView.State.ERROR -> {}
        }
    }
}

@Composable
fun DepositAddressView_Loading() {

    Box(
        modifier = Modifier
            .height(120.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.transfer_view_component_loading_view_title),
                fontFamily = robotoFont,
                fontWeight = FontWeight.Normal,
                fontSize = 17.sp,
                color = colorResource(id = R.color.primary_color)
            )
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .testTag(Constants.AccountsViewTestTags.Loading.id),
                color = colorResource(id = R.color.primary_color)
            )
        }
    }
}

@Composable
fun DepositAddressView_Content() {

    Column() {
        ConstraintLayout(
            Modifier.fillMaxSize()
        ) {

            // -- Refs
            val (depositTitle, qrImageView, qrWarning, networkTitle,
                networkValue, addressTitle, addressValue, copyAddressButton,
                warningContainer, button) = createRefs()

            // -- Views
            Text(
                modifier = Modifier
                    .constrainAs(depositTitle) {
                        top.linkTo(parent.top, margin = 10.dp)
                        start.linkTo(parent.start, margin = 0.dp)
                        end.linkTo(parent.end, margin = 0.dp)
                        width = Dimension.fillToConstraints
                    },
                text = "Deposit BTC",
                color = Color.Black,
                fontFamily = interFont,
                fontWeight = FontWeight.Medium,
                fontSize = 21.sp,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Left
            )

            val qrSize = 200
            val qrImageBitmap = generateQRCode("tb1quxzz8469kepp4yym59s2mt03vzlqg9pvkvefmv", qrSize, qrSize)
            qrImageBitmap?.asImageBitmap()?.let { image ->
                Image(
                    bitmap = image,
                    contentDescription = "",
                    modifier = Modifier
                        .constrainAs(qrImageView) {
                            top.linkTo(depositTitle.bottom, margin = 35.dp)
                            centerHorizontallyTo(parent)
                            width = Dimension.value(qrSize.dp)
                            height = Dimension.value(qrSize.dp)
                        },
                    contentScale = ContentScale.Fit
                )
            }

            Text(
                modifier = Modifier
                    .constrainAs(qrWarning) {
                        top.linkTo(qrImageView.bottom, margin = 5.dp)
                        start.linkTo(parent.start, margin = 0.dp)
                        end.linkTo(parent.end, margin = 0.dp)
                        width = Dimension.fillToConstraints
                    },
                text = "Send only BTC to this deposit address.",
                color = colorResource(id = R.color.deposit_address_view_qr_warning_color),
                fontFamily = interFont,
                fontWeight = FontWeight.Light,
                fontSize = 12.sp,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )

            Text(
                modifier = Modifier
                    .constrainAs(networkTitle) {
                        top.linkTo(qrWarning.bottom, margin = 35.dp)
                        start.linkTo(parent.start, margin = 0.dp)
                        end.linkTo(parent.end, margin = 0.dp)
                        width = Dimension.fillToConstraints
                    },
                text = "Network",
                color = colorResource(id = R.color.deposit_address_view_qr_warning_color),
                fontFamily = interFont,
                fontWeight = FontWeight.Light,
                fontSize = 13.sp,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Left
            )

            Text(
                modifier = Modifier
                    .constrainAs(networkValue) {
                        top.linkTo(networkTitle.bottom, margin = 5.dp)
                        start.linkTo(parent.start, margin = 0.dp)
                        end.linkTo(parent.end, margin = 0.dp)
                        width = Dimension.fillToConstraints
                    },
                text = "Bitcoin",
                color = Color.Black,
                fontFamily = interFont,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Left
            )

            Image(
                painter = painterResource(id = R.drawable.ic_copy),
                contentDescription = "",
                modifier = Modifier
                    .constrainAs(copyAddressButton) {
                        top.linkTo(networkValue.bottom, margin = 48.dp)
                        end.linkTo(parent.end, margin = 8.dp)
                        width = Dimension.value(20.dp)
                        height = Dimension.value(20.dp)
                    }
            )

            Text(
                modifier = Modifier
                    .constrainAs(addressTitle) {
                        top.linkTo(networkValue.bottom, margin = 35.dp)
                        start.linkTo(parent.start, margin = 0.dp)
                        end.linkTo(copyAddressButton.start, margin = 7.dp)
                        width = Dimension.fillToConstraints
                    },
                text = "BTC Deposit Address",
                color = colorResource(id = R.color.deposit_address_view_qr_warning_color),
                fontFamily = interFont,
                fontWeight = FontWeight.Light,
                fontSize = 13.sp,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Left
            )

            Text(
                modifier = Modifier
                    .constrainAs(addressValue) {
                        top.linkTo(addressTitle.bottom, margin = 5.dp)
                        start.linkTo(parent.start, margin = 0.dp)
                        end.linkTo(copyAddressButton.start, margin = 7.dp)
                        width = Dimension.fillToConstraints
                    },
                text = "tb1quxzz8469kepp4yym59s2mt03vzlqg9pvkvefmv",
                color = Color.Black,
                fontFamily = interFont,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Left
            )

            Box(
                modifier = Modifier
                    .constrainAs(warningContainer) {
                        start.linkTo(parent.start, margin = 0.dp)
                        top.linkTo(addressValue.bottom, margin = 25.dp)
                        end.linkTo(parent.end, margin = 0.dp)
                        width = Dimension.fillToConstraints
                        height = Dimension.value(100.dp)
                    }
                    .clip(RoundedCornerShape(10.dp))
                    .background(colorResource(id = R.color.deposit_address_view_warning_background_color))
            ) {
                ConstraintLayout(
                    Modifier.fillMaxSize()
                ) {

                    // -- Refs
                    val (icon, label) = createRefs()

                    // -- View
                    Image(
                        painter = painterResource(id = R.drawable.ic_warning),
                        contentDescription = "",
                        modifier = Modifier
                            .constrainAs(icon) {
                                top.linkTo(parent.top, margin = 8.dp)
                                start.linkTo(parent.start, margin = 10.dp)
                                width = Dimension.value(14.dp)
                                height = Dimension.value(14.dp)
                            }
                    )
                    Text(
                        modifier = Modifier
                            .constrainAs(label) {
                                top.linkTo(parent.top, margin = 7.dp)
                                start.linkTo(icon.end, margin = 5.dp)
                                end.linkTo(parent.end, margin = 12.dp)
                                width = Dimension.fillToConstraints
                            },
                        text = "Warning: You can lose all of your coins, bla bla bla bla bla bla bla bla bla bla bla blabla bla bla bla bla blabla bla bla bla bla blabla bla bla bla bla blabla bla bla bla bla blabla bla bla bla",
                        color = colorResource(id = R.color.deposit_address_view_warning_label_color),
                        fontFamily = interFont,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Justify
                    )
                }
            }
            Button(
                onClick = {},
                modifier = Modifier
                    .constrainAs(button) {
                        start.linkTo(parent.start, margin = 0.dp)
                        end.linkTo(parent.end, margin = 0.dp)
                        bottom.linkTo(parent.bottom, margin = 15.dp)
                        width = Dimension.fillToConstraints
                        height = Dimension.value(48.dp)
                    }
                    .testTag(Constants.AccountsViewTestTags.TransferFunds.id),
                shape = RoundedCornerShape(10.dp),
                elevation = ButtonDefaults.elevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 4.dp,
                    disabledElevation = 0.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(id = R.color.primary_color),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Generate payment code",
                    color = Color.White,
                    fontFamily = interFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    letterSpacing = (-0.4).sp
                )
            }
        }
    }
}