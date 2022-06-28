package app.cybrid.sdkandroid.components.quote.view.test

import app.cybrid.cybrid_api_bank.client.models.PostQuoteBankModel
import app.cybrid.cybrid_api_bank.client.models.QuoteBankModel
import app.cybrid.sdkandroid.core.BigDecimal
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface QuotesApiTest {

    @POST("api/quotes")
    suspend fun createQuote(@Body postQuoteBankModel: PostQuoteBankModelTest): Response<QuoteBankModelTest>
}

data class QuoteBankModelTest (

    /* Auto-generated unique identifier for the quote. */
    @SerializedName("guid")
    val guid: kotlin.String? = null,

    /* The unique identifier for the customer. */
    @SerializedName("customer_guid")
    val customerGuid: kotlin.String? = null,

    /* Symbol the quote is being requested for. Format is \"asset-counter_asset\" in uppercase. */
    @SerializedName("symbol")
    val symbol: kotlin.String? = null,

    /* The direction of the quote: either 'buy' or 'sell'. */
    @SerializedName("side")
    val side: QuoteBankModel.Side? = null,

    /* The amount to be received in base units of the currency: currency is \"asset\" for buy and \"counter_asset\" for sell. */
    @SerializedName("receive_amount")
    val receiveAmount: java.math.BigDecimal? = null,

    /* The amount to be delivered in base units of the currency: currency is \"counter_asset\" for buy and \"asset\" for sell. */
    @SerializedName("deliver_amount")
    val deliverAmount: java.math.BigDecimal? = null,

    /* The fee associated with the trade. Denominated in \"counter_asset\" base units */
    @SerializedName("fee")
    val fee: kotlin.Int? = null,

    /* ISO8601 datetime the quote was created at. */
    @SerializedName("issued_at")
    val issuedAt: java.time.OffsetDateTime? = null,

    /* ISO8601 datetime the quote is expiring at. */
    @SerializedName("expires_at")
    val expiresAt: java.time.OffsetDateTime? = null

)

data class PostQuoteBankModelTest (

    /* The unique identifier for the customer. */
    @SerializedName("customer_guid")
    val customerGuid: kotlin.String,

    /* Symbol the quote is being requested for. Format is \"asset-counter_asset\" in uppercase. See the Symbols API for a complete list of cryptocurrencies supported. */
    @SerializedName("symbol")
    val symbol: kotlin.String,

    /* The direction of the quote: either 'buy' or 'sell'. */
    @SerializedName("side")
    val side: PostQuoteBankModel.Side,

    /* The amount to be received in base units of the currency: currency is \"asset\" for buy and \"counter_asset\" for sell. */
    @SerializedName("receive_amount")
    val receiveAmount: java.math.BigDecimal? = null,

    /* The amount to be delivered in base units of the currency: currency is \"counter_asset\" for buy and \"asset\" for sell. */
    @SerializedName("deliver_amount")
    val deliverAmount: java.math.BigDecimal? = null

)