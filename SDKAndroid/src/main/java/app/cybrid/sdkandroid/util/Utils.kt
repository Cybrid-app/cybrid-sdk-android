package app.cybrid.sdkandroid.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

fun isSuccessful(code: Int): Boolean {
    return code in 200..299
}

fun getDateInFormat(date: OffsetDateTime, pattern:String = "MMM dd, YYYY"): String? {

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