package app.cybrid.sdkandroid.util

import app.cybrid.cybrid_api_bank.client.models.PostWorkflowBankModel

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