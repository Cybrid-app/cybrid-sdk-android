package app.cybrid.sdkandroid.components.accounts.entity
import app.cybrid.cybrid_api_bank.client.models.AccountBankModel
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import java.math.BigDecimal as JavaBigDecimal

data class AccountAssetPriceModel (

    var accountAssetCode:String,
    var accountBalance:JavaBigDecimal,
    var accountGuid:String,
    var accountType: AccountBankModel.Type,
    var accountCreated:java.time.OffsetDateTime,

    var assetName:String,
    var assetSymbol:String,
    var assetType: AssetBankModel.Type,
    var assetDecimals:JavaBigDecimal,

    var buyPrice:JavaBigDecimal,
    var sellPrice:JavaBigDecimal
)