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

    enum class TransferView(val id: String) {
        Surface("TransferView_MainSurface"),
        LoadingView("TransferView_LoadingView"),
        AccountsView("TransferView_AccountsView"),
        ModalLoading("TransferView_Modal_Loading"),
        ModalContentAmount("TransferView_Modal_Content_Amount"),
        ModalContentDate("TransferView_Modal_Content_Date"),
        ModalContentFromTo("TransferView_Modal_Content_From_To")
    }

    companion object {

        const val MIN_INTEGER_DIGITS = 0
        const val MIN_FRACTION_DIGITS = 2
    }
}