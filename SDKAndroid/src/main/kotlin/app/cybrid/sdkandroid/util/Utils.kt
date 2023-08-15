package app.cybrid.sdkandroid.util

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import app.cybrid.cybrid_api_bank.client.models.PostWorkflowBankModel
import app.cybrid.sdkandroid.Cybrid
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Hashtable

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

    val cybrid = Cybrid
    return "${cybrid.imagesUrl}$name${cybrid.imagesSize}"
}

fun generateQRCode(text: String, width: Int, height: Int): Bitmap? {

    try {
        val hints = Hashtable<EncodeHintType, Any>()
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.L
        hints[EncodeHintType.MARGIN] = 2

        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints)

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}