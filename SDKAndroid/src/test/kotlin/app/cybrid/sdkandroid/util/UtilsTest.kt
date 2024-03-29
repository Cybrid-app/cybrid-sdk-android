package app.cybrid.sdkandroid.util

import org.junit.Assert
import org.junit.Test
import java.time.OffsetDateTime
import java.util.*

class UtilsTest {

    @Test
    fun isSuccessfulTest() {

        // -- Given
        val code0 = 199
        val code1 = 200
        val code2 = 299
        val code3 = 300
        val code4 = 301

        // -- When
        val result0 = isSuccessful(code0)
        val result1 = isSuccessful(code1)
        val result2 = isSuccessful(code2)
        val result3 = isSuccessful(code3)
        val result4 = isSuccessful(code4)

        // -- Then
        Assert.assertFalse(result0)
        Assert.assertTrue(result1)
        Assert.assertTrue(result2)
        Assert.assertFalse(result3)
        Assert.assertFalse(result4)
    }

    @Test
    fun test_getLanguage() {

        // -- EN
        var languageString = "en"
        var language = getLanguage(languageString)
        Assert.assertEquals(language.value, languageString)

        // -- FR
        languageString = "fr"
        language = getLanguage(languageString)
        Assert.assertEquals(language.value, languageString)

        // -- ES
        languageString = "es"
        language = getLanguage(languageString)
        Assert.assertEquals(language.value, languageString)

        // -- NL
        languageString = "nl"
        language = getLanguage(languageString)
        Assert.assertEquals(language.value, languageString)

        // -- NL
        languageString = "nl"
        language = getLanguage(languageString)
        Assert.assertEquals(language.value, languageString)

        // -- Other
        languageString = "other"
        language = getLanguage(languageString)
        Assert.assertEquals(language.value, "en")
    }

    @Test
    fun test_getDateInFormatTest() {

        // -- Given
        Locale.setDefault(Locale.forLanguageTag("us-EN"))
        val dateOne = OffsetDateTime.parse("2022-08-02T10:55:34.039847-05:00")
        val patterOne = "MM,dd,YYYY"

        // -- When
        val dateFormattedOne = getDateInFormat(dateOne)
        val dateFormattedTwo = getDateInFormat(dateOne, patterOne)
        val dateFormattedThree = getDateInFormat(dateOne, "")

        // -- Then
        Assert.assertEquals(dateFormattedOne, "Aug 02, 2022")
        Assert.assertEquals(dateFormattedTwo, "08,02,2022")
        Assert.assertEquals(dateFormattedThree, "")
    }

    @Test
    fun test_getImageUrl() {

        // -- Given
        val imageName = "btc"
        val nameResult = "https://images.cybrid.xyz/sdk/assets/png/color/btc@2x.png"

        // -- When
        val imageUrl = getImageUrl(imageName)

        // -- Then
        Assert.assertEquals(nameResult, imageUrl)
    }
}