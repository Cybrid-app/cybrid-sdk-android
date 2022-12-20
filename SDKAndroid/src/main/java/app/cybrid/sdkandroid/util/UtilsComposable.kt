package app.cybrid.sdkandroid.util

import android.annotation.SuppressLint
import androidx.compose.runtime.*
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.ui.Theme.robotoFont
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@SuppressLint("NewApi")
fun getDateInFormat(date: OffsetDateTime, pattern:String = "MMM dd, YYYY"): String {

    val formatter = DateTimeFormatter.ofPattern(pattern)
    return date.format(formatter)
}

@Composable
fun getSpannableStyle(
    text: String,
    secondaryText: String,
    style: SpanStyle
): AnnotatedString {
    return buildAnnotatedString {
        append(text)
        withStyle(style) { append(secondaryText) }
    }
}
@Composable
fun getAnnotatedStyle(fontSize: TextUnit): SpanStyle {
    return SpanStyle(
        color = colorResource(id = R.color.list_prices_asset_component_code_color),
        fontFamily = robotoFont,
        fontWeight = FontWeight.Normal,
        fontSize = fontSize
    )
}

@Composable
fun Lifecycle.observeAsState(): State<Lifecycle.Event> {
    val state = remember { mutableStateOf(Lifecycle.Event.ON_ANY) }
    DisposableEffect(this) {
        val observer = LifecycleEventObserver { _, event ->
            state.value = event
        }
        this@observeAsState.addObserver(observer)
        onDispose {
            this@observeAsState.removeObserver(observer)
        }
    }
    return state
}