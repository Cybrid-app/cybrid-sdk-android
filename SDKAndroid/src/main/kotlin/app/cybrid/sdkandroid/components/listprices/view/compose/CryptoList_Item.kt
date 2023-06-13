package app.cybrid.sdkandroid.components.listprices.view.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.cybrid_api_bank.client.models.SymbolPriceBankModel
import app.cybrid.sdkandroid.components.ListPricesViewCustomStyles
import app.cybrid.sdkandroid.components.listprices.view.ListPricesViewModel
import app.cybrid.sdkandroid.core.BigDecimalPipe
import app.cybrid.sdkandroid.core.toBigDecimal
import app.cybrid.sdkandroid.ui.Theme.robotoFont
import app.cybrid.sdkandroid.util.getImageUrl
import coil.compose.rememberAsyncImagePainter

@Composable
fun CryptoList_Item(crypto: SymbolPriceBankModel,
                    vm: ListPricesViewModel,
                    index: Int, selectedIndex: Int,
                    customStyles: ListPricesViewCustomStyles,
                    onClick: (asset: AssetBankModel,
                              pairAsset: AssetBankModel
                    ) -> Unit) {

    val backgroundColor = if (index == selectedIndex) MaterialTheme.colors.primary else Color.Transparent
    if (crypto.symbol != null) {

        val loadingErrorVal = "-1"
        val asset = vm.findAsset(vm.getSymbol(crypto.symbol!!))
        val pairAsset = vm.findAsset(vm.getPair(crypto.symbol!!))
        val imageName = vm.getSymbol(crypto.symbol!!).lowercase()
        val imagePainter = rememberAsyncImagePainter(getImageUrl(imageName))

        val name = asset?.name ?: ""
        val valueString = crypto.buyPrice?.let {
            if (pairAsset != null) {
                BigDecimalPipe.transform(it.toBigDecimal(), pairAsset)
            } else { loadingErrorVal }
        } ?: loadingErrorVal
        val value = buildAnnotatedString {
            append(valueString)
            withStyle(style = SpanStyle(color = customStyles.itemsCodeTextColor)) {
                append(" (${pairAsset?.code ?: ""})")
            }
        }
        if (valueString != loadingErrorVal) {
            Surface(color = backgroundColor) {

                Column(modifier = Modifier
                    .height(46.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(vertical = 0.dp)
                            .height(45.dp)
                            .clickable { onClick(asset!!, pairAsset!!) },
                    ) {

                        Image(
                            painter = imagePainter,
                            contentDescription = "{$name}",
                            modifier = Modifier
                                .padding(horizontal = 0.dp)
                                .padding(0.dp)
                                .size(24.dp),
                            contentScale = ContentScale.Fit
                        )
                        Text(
                            text = name,
                            modifier = Modifier.padding(start = 13.dp),
                            fontFamily = robotoFont,
                            fontWeight = FontWeight.Normal,
                            fontSize = customStyles.itemsTextSize,
                            color = customStyles.itemsTextColor
                        )
                        Text(
                            text = asset?.code ?: "",
                            modifier = Modifier.padding(start = 5.5.dp),
                            fontFamily = robotoFont,
                            fontWeight = FontWeight.Normal,
                            fontSize = customStyles.itemsCodeTextSize,
                            color = customStyles.itemsCodeTextColor
                        )
                        Text(
                            text = value,
                            modifier = Modifier
                                .padding(end = 0.dp)
                                .weight(1f),
                            textAlign = TextAlign.End,
                            fontFamily = robotoFont,
                            fontWeight = FontWeight.Normal,
                            fontSize = customStyles.itemsTextPriceSize,
                            color = customStyles.itemsTextColor
                        )
                    }
                    Divider(
                        color = colorResource(id = app.cybrid.sdkandroid.R.color.accounts_view_balance_title),
                        modifier = Modifier
                            .height((0.40).dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}