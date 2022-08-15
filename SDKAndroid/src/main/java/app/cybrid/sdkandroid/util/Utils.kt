package app.cybrid.sdkandroid.util

import android.annotation.SuppressLint
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