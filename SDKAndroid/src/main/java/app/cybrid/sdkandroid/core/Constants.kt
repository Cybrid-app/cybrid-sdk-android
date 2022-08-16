package app.cybrid.sdkandroid.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.ui.Theme.robotoFont

class Constants {

    enum class QuoteConfirmation(val id:String) {
        LoadingIndicator("QuoteConfirmationLoadingProgressIndicator")
    }

    enum class AccountsViewTestTags(val id:String) {
        Surface("AccountsView_MainSurface"),
        Loading("AccountsView_Loading"),
        List("AccountsView_List")
    }

    companion object {

        const val MIN_INTEGER_DIGITS = 0
        const val MIN_FRACTION_DIGITS = 2

        @Composable
        fun getAnnotatedStyle(fontSize:TextUnit): SpanStyle {
            return SpanStyle(
                color = colorResource(id = R.color.list_prices_asset_component_code_color),
                fontFamily = robotoFont,
                fontWeight = FontWeight.Normal,
                fontSize = fontSize
            )
        }
    }
}