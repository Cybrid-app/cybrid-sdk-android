package app.cybrid.sdkandroid.components.accounts.entity
import app.cybrid.cybrid_api_bank.client.models.AccountBankModel
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.sdkandroid.core.BigDecimal
import java.math.BigDecimal as JavaBigDecimal

data class AccountAssetPriceModel (

    var accountAssetCode:String,
    var accountBalance:JavaBigDecimal,
    var accountBalanceFormatted:BigDecimal,
    var accountBalanceFormattedString:String,
    var accountBalanceInFiat:BigDecimal,
    var accountBalanceInFiatFormatted:String,
    var accountGuid:String,
    var accountType: AccountBankModel.Type,
    var accountCreated:java.time.OffsetDateTime,

    var assetName:String,
    var assetSymbol:String,
    var assetType: AssetBankModel.Type,
    var assetDecimals:JavaBigDecimal,
    var pairAsset:AssetBankModel,

    var buyPrice:BigDecimal,
    var buyPriceFormatted:String,
    var sellPrice:JavaBigDecimal
)