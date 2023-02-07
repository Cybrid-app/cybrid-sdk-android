package app.cybrid.sdkandroid.components.listprices.view.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.ListPricesViewCustomStyles
import app.cybrid.sdkandroid.ui.Theme.robotoFont

@Composable
fun CryptoList_HeaderItem(customStyles: ListPricesViewCustomStyles) {

    val priceColor = if (customStyles.headerTextColor != Color(R.color.list_prices_asset_component_header_color)) {
        customStyles.headerTextColor
    } else {
        Color.Black
    }

    Surface(color = Color.Transparent) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {

            Text(
                text = stringResource(id = R.string.list_prices_asset_component_header_currency),
                fontFamily = robotoFont,
                fontWeight = FontWeight.Bold,
                fontSize = customStyles.headerTextSize,
                color = customStyles.headerTextColor
            )
            Text(
                text = stringResource(id = R.string.list_prices_asset_component_header_price),
                modifier = Modifier
                    .padding(end = 0.dp)
                    .weight(1f),
                textAlign = TextAlign.End,
                fontFamily = robotoFont,
                fontWeight = FontWeight.Bold,
                fontSize = customStyles.headerTextSize,
                color = priceColor
            )
        }
    }
}