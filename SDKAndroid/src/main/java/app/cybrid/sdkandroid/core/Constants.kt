package app.cybrid.sdkandroid.core

import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import java.math.BigDecimal

class Constants {

    enum class QuoteConfirmation(val id: String) {
        LoadingIndicator("QuoteConfirmationLoadingProgressIndicator")
    }

    enum class AccountsViewTestTags(val id: String) {
        Surface("AccountsView_MainSurface"),
        Loading("AccountsView_Loading"),
        List("AccountsView_List")
    }

    enum class IdentityVerificationView(val id: String) {
        Surface("IdentityVerificationView_MainSurface"),
        LoadingView("IdentityVerificationView_LoadingView"),
        LoadingViewIndicator("IdentityVerificationView_LoadingView_Indicator"),
        RequiredView("IdentityVerificationView_RequiredView"),
        VerifiedView("IdentityVerificationView_VerifiedView"),
        ErrorView("IdentityVerificationView_ErrorView"),
        ReviewingView("IdentityVerificationView_ReviewingView"),
    }

    enum class BankAccountsView(val id: String) {
        Surface("BankAccountsView_MainSurface"),
        LoadingView("BankAccountsView_LoadingView"),
        LoadingViewIndicator("BankAccountsView_LoadingView_Indicator"),
        RequiredView("BankAccountsView_RequiredView"),
        DoneView("BankAccountsView_DoneView"),
        ErrorView("BankAccountsView_ErrorView")
    }

    companion object {

        const val MIN_INTEGER_DIGITS = 0
        const val MIN_FRACTION_DIGITS = 2

        val USD_ASSET: AssetBankModel = AssetBankModel(
            code = "USD",
            decimals = BigDecimal(2),
            name = "American Dollar",
            symbol = "$",
            type= AssetBankModel.Type.fiat,
        )
    }
}