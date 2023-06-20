package app.cybrid.sdkandroid.util

import android.annotation.SuppressLint
import app.cybrid.cybrid_api_bank.client.models.PostWorkflowBankModel
import app.cybrid.sdkandroid.Cybrid
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

fun isSuccessful(code: Int): Boolean {
    return code in 200..299
}

fun getLanguage(deviceLanguage: String): PostWorkflowBankModel.Language {

    val language: PostWorkflowBankModel.Language = when(deviceLanguage) {
        "en", "fr", "es", "nl", "de" -> {
            PostWorkflowBankModel.Language.valueOf(deviceLanguage)
        }
        else -> {
            PostWorkflowBankModel.Language.valueOf("en")
        }
    }
    return language
}

@SuppressLint("NewApi")
fun getDateInFormat(date: OffsetDateTime, pattern:String = "MMM dd, YYYY"): String {

    val formatter = DateTimeFormatter.ofPattern(pattern)
    return date.format(formatter)
}

fun getImageUrl(name: String): String {

    val cybrid = Cybrid.getInstance()
    return "${cybrid.imagesUrl}$name${cybrid.imagesSize}"
}