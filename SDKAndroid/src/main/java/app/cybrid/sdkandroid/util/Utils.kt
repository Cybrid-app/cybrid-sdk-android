package app.cybrid.sdkandroid.util

fun isSuccessful(code: Int): Boolean {
    return code in 200..299
}