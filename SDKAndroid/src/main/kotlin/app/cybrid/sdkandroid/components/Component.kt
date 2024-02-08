package app.cybrid.sdkandroid.components

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout
import app.cybrid.cybrid_api_bank.client.models.CustomerBankModel
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.core.Constants

open class Component @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    var updateInterval = 5000L

    private var _handler: Handler? = null
    private var _runnable: Runnable? = null
    protected var composeView:ComposeView? = null
    var isUpdating = false

    fun setupRunnable(runner: () -> Unit) {

        _handler = Handler(Looper.getMainLooper())
        _runnable = Runnable {

            isUpdating = true
            runner.invoke()
            _handler.let {
                _runnable.let { _it ->
                    it?.postDelayed(_it!!, updateInterval)
                }
            }
        }
        _handler?.postDelayed(_runnable ?: Runnable {}, updateInterval)
    }

    fun canRenderUI(): Boolean {
        if (Cybrid.customer == null) { return true }
        return Cybrid.customer?.state != "frozen"
    }

    companion object {

        @Composable
        fun CreateLoader(modifier: Modifier, message: String) {
            Column(
                modifier = modifier.height(180.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = message,
                    style = TextStyle(
                        fontSize = 22.sp,
                        lineHeight = 28.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        fontWeight = FontWeight(400),
                        color = colorResource(id = R.color.black),
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.35.sp,
                    )
                )
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .testTag(Constants.AccountsViewTestTags.Loading.id),
                    color = colorResource(id = R.color.primary_color)
                )
            }
        }

        @Composable
        fun Label(modifier: Modifier,
                  message: String,
                  fontSize: TextUnit = 21.sp,
                  lineHeight: TextUnit = 28.sp,
                  fontWeight: Int = 600,
                  letterSpacing: TextUnit = 1.sp,
                  align: TextAlign = TextAlign.Left) {
            Text(
                modifier = modifier,
                text = message,
                style = TextStyle(
                    fontSize = fontSize,
                    lineHeight = lineHeight,
                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                    fontWeight = FontWeight(fontWeight),
                    color = Color.Black,
                    textAlign = align,
                    letterSpacing = letterSpacing,
                )
            )
        }

        @Composable
        fun CreateSuccess(modifier: Modifier, message: String) {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier.size(40.dp),
                    painter = painterResource(id = R.drawable.kyc_verified),
                    contentDescription = "ic_success_desc")
                Text(
                    modifier = Modifier
                        .padding(top = 16.dp),
                    text = message,
                    style = TextStyle(
                        fontSize = 22.sp,
                        lineHeight = 28.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        fontWeight = FontWeight(400),
                        color = colorResource(id = R.color.black),
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.35.sp,
                    )
                )
            }
        }

        @Composable
        fun FrozenCustomerUI() {

            val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

            ConstraintLayout(
                modifier = Modifier.fillMaxSize()
            ) {
                val (container) = createRefs()
                Column(
                    modifier = Modifier
                        .constrainAs(container) {
                            centerHorizontallyTo(parent)
                            centerVerticallyTo(parent)
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        modifier = Modifier.size(70.dp),
                        painter = painterResource(id = R.drawable.kyc_error),
                        contentDescription = "ic_error_desc")
                    Text(
                        modifier = Modifier
                            .padding(top = 25.dp),
                        text = stringResource(id = R.string.customer_frozen),
                        style = TextStyle(
                            fontSize = 23.sp,
                            lineHeight = 28.sp,
                            fontFamily = FontFamily(Font(R.font.inter_regular)),
                            fontWeight = FontWeight(900),
                            color = colorResource(id = R.color.black),
                            textAlign = TextAlign.Center
                        )
                    )
                    Text(
                        modifier = Modifier
                            .padding(top = 15.dp),
                        text = stringResource(id = R.string.customer_frozen_details),
                        style = TextStyle(
                            fontSize = 19.sp,
                            lineHeight = 22.sp,
                            fontFamily = FontFamily(Font(R.font.inter_regular)),
                            fontWeight = FontWeight(400),
                            color = colorResource(id = R.color.black),
                            textAlign = TextAlign.Center
                        )
                    )
                    Button(
                        onClick = { onBackPressedDispatcher?.onBackPressed() },
                        modifier = Modifier
                            .padding(top = 30.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp)
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        elevation = ButtonDefaults.elevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 4.dp,
                            disabledElevation = 0.dp
                        ),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = colorResource(id = R.color.accent_blue),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = stringResource(id = R.string.customer_frozen_button),
                            style = TextStyle(
                                fontSize = 19.sp,
                                lineHeight = 22.sp,
                                fontFamily = FontFamily(Font(R.font.inter_regular)),
                                fontWeight = FontWeight(400),
                                color = colorResource(id = R.color.white),
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            }
        }
    }
}