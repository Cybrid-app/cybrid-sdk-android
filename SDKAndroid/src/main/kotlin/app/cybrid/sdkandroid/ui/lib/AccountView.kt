package app.cybrid.sdkandroid.ui.lib

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import app.cybrid.cybrid_api_bank.client.models.AccountBankModel
import app.cybrid.sdkandroid.R

@Composable
fun AccountView(
    modifier: Modifier,
    account: AccountBankModel,
    backgroundColor: Color = colorResource(id = R.color.external_wallets_view_add_wallet_input_color)
) {

    Box(
        modifier = modifier
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(shape = RoundedCornerShape(10))
                .background(backgroundColor)
        ) {

            // -- vars
            val (accountRef) = createRefs()

            // -- Content
            RoundedAccountSelector__Item(
                modifier = Modifier
                    .constrainAs(accountRef) {
                        start.linkTo(parent.start, margin = 15.dp)
                        centerVerticallyTo(parent)
                    },
                account = account
            )
        }
    }
}

@Composable
fun AccountLabelView(
    modifier: Modifier,
    titleText: String,
    titleColor: Color = colorResource(id = R.color.external_wallets_view_add_wallet_input_title_color),
    titleSize: TextUnit = 15.5.sp,
    titleWeight: Int = 400,
    account: AccountBankModel,
    backgroundColor: Color = colorResource(id = R.color.external_wallets_view_add_wallet_input_color)
) {
    ConstraintLayout(
        modifier = modifier
    ) {
        val (title, accountRef) = createRefs()

        Text(
            text = titleText,
            modifier = Modifier
                .constrainAs(title) {
                    start.linkTo(parent.start, margin = 0.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                    top.linkTo(parent.top, margin = 0.dp)
                    width = Dimension.fillToConstraints
                },
            style = TextStyle(
                fontSize = titleSize,
                lineHeight = 22.sp,
                fontFamily = FontFamily(Font(R.font.inter_regular)),
                fontWeight = FontWeight(titleWeight),
                color = titleColor,
                textAlign = TextAlign.Left
            )
        )

        AccountView(
            modifier = Modifier
                .constrainAs(accountRef) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(title.bottom, margin = 10.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                    height = Dimension.value(60.dp)
                    width = Dimension.fillToConstraints
                },
            account = account,
            backgroundColor = backgroundColor
        )
    }
}