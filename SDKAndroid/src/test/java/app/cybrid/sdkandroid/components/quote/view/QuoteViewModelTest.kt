package app.cybrid.sdkandroid.components.quote.view

import app.cybrid.cybrid_api_bank.client.models.*
import app.cybrid.sdkandroid.components.quote.view.test.PostQuoteBankModelTest
import app.cybrid.sdkandroid.components.quote.view.test.QuoteBankModelTest
import app.cybrid.sdkandroid.core.AssetPipe
import app.cybrid.sdkandroid.core.BigDecimal
import app.cybrid.sdkandroid.tools.TestConstants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class QuoteViewModelTest {

    @ExperimentalCoroutinesApi
    @Test
    fun initTest() = runBlocking {

        // -- Given
        val viewModel = QuoteViewModel()

        // -- Then
        Assert.assertNotNull(viewModel)
        Assert.assertEquals(viewModel.canUpdateQuote, true)
        Assert.assertEquals(viewModel.quoteBankModel, QuoteBankModelTest())
        Assert.assertEquals(viewModel.tradeBankModel, TradeBankModel())
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getQuoteObjectTest() = runBlocking {

        // -- Given
        val viewModel = QuoteViewModel()
        val cryptoAsset = TestConstants.BTC_ASSET
        val fiatAsset = TestConstants.CAD_ASSET
        val quote = viewModel.getQuoteObject(
            amount = BigDecimal(10),
            input = AssetBankModel.Type.crypto,
            side = PostQuoteBankModel.Side.buy,
            asset =  cryptoAsset,
            pairAsset = fiatAsset)

        // -- When
        val postQuoteBankModel = PostQuoteBankModelTest(
            customerGuid = "",
            symbol = "BTC-CAD",
            side = PostQuoteBankModel.Side.buy,
            receiveAmount = AssetPipe.transform(
                value = BigDecimal(10),
                asset = cryptoAsset,
                unit = "base"
            ).toJavaBigDecimal()
        )

        // -- Then
        Assert.assertEquals(quote, postQuoteBankModel)
    }
}