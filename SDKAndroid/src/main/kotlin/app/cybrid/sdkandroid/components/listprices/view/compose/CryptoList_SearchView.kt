package app.cybrid.sdkandroid.components.listprices.view.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.ui.Theme.robotoFont

@Composable
fun CryptoList_SearchView(state: MutableState<TextFieldValue>) {

    TextField(
        value = state.value,
        onValueChange = { value ->
            state.value = value
        },
        placeholder = { Text(stringResource(id = R.string.list_prices_asset_component_search)) },
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .height(50.dp)
            .fillMaxWidth()
            .shadow(5.dp),
        shape = RoundedCornerShape(4.dp),
        textStyle = TextStyle(
            fontFamily = robotoFont,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        ),
        trailingIcon = {

            val iconSize = 20.dp
            if (state.value != TextFieldValue("")) {
                IconButton(
                    onClick = {
                        state.value = TextFieldValue("")
                    }
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(0.dp)
                            .size(iconSize)
                    )
                }
            } else {

                Icon(
                    Icons.Default.Search,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(0.dp)
                        .size(iconSize)
                )
            }
        },
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.Black,
            cursorColor = colorResource(id = R.color.primary_color),
            leadingIconColor = Color.Black,
            trailingIconColor = Color.Black,
            backgroundColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}