package app.cybrid.sdkandroid.components.quote.view

import androidx.lifecycle.ViewModel
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.cybrid_api_bank.client.models.PostQuoteBankModel
import app.cybrid.sdkandroid.Cybrid
import app.cybrid.sdkandroid.core.AssetPipe
import app.cybrid.sdkandroid.core.BigDecimal

class QuoteViewModel: ViewModel() {

    // -- Customer GUID
    private val customerGuid = Cybrid.instance.customerGuid

    // -- Basic postQuoteBankModel object
    private var postQuoteBankModel = PostQuoteBankModel(
        customerGuid = customerGuid,
        symbol = "",
        side = PostQuoteBankModel.Side.buy
    )

    fun get(
        amount: BigDecimal,
        input: QuoteViewModelInput,
        side: PostQuoteBankModel.Side,
        asset: AssetBankModel,
        counterAsset: AssetBankModel
    ): PostQuoteBankModel  {

        // -- Symbol
        val symbol = "${asset.code}-${counterAsset.code}"

        // -- Check side
        when(side) {

            PostQuoteBankModel.Side.buy -> {

                if (input == QuoteViewModelInput.asset) {
                    postQuoteBankModel = PostQuoteBankModel(
                        customerGuid = customerGuid,
                        symbol = symbol,
                        side = side,
                        receiveAmount = AssetPipe.transform(
                            value = amount,
                            asset = asset,
                            unit = "base"
                        ).toInt()
                    )
                } else {
                    postQuoteBankModel = PostQuoteBankModel(
                        customerGuid = customerGuid,
                        symbol = symbol,
                        side = side,
                        deliverAmount = AssetPipe.transform(
                            value = amount,
                            asset = counterAsset,
                            unit = "base"
                        ).toInt()
                    )
                }
            }

            PostQuoteBankModel.Side.sell -> {

                if (input == QuoteViewModelInput.counterAsset) {
                    postQuoteBankModel = PostQuoteBankModel(
                        customerGuid = customerGuid,
                        symbol = symbol,
                        side = side,
                        receiveAmount = AssetPipe.transform(
                            value = amount,
                            asset = counterAsset,
                            unit = "base"
                        ).toInt()
                    )
                } else {
                    postQuoteBankModel = PostQuoteBankModel(
                        customerGuid = customerGuid,
                        symbol = symbol,
                        side = side,
                        deliverAmount = AssetPipe.transform(
                            value = amount,
                            asset = asset,
                            unit = "base"
                        ).toInt()
                    )
                }
            }
        }

        // -- Return PostQuoteBankModel
        return this.postQuoteBankModel
    }

    enum class QuoteViewModelInput {
        asset, counterAsset
    }
}