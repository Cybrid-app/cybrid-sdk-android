package app.cybrid.sdkandroid.core

class Constants {

    enum class QuoteConfirmation(val id:String) {
        LoadingIndicator("QuoteConfirmationLoadingProgressIndicator")
    }

    enum class AccountsViewTestTags(val id:String) {
        Surface("AccountsView_MainSurface"),
        Loading("AccountsView_Loading"),
        List("AccountsView_List")
    }

    enum class IdentityVerificationView(val id:String) {
        Surface("IdentityVerificationView_MainSurface"),
        LoadingView("IdentityVerificationView_LoadingView"),
        LoadingViewIndicator("IdentityVerificationView_LoadingView_Indicator"),
        RequiredView("IdentityVerificationView_RequiredView"),
        VerifiedView("IdentityVerificationView_VerifiedView"),
        ErrorView("IdentityVerificationView_ErrorView"),
        ReviewingView("IdentityVerificationView_ReviewingView"),
    }

    companion object {

        const val MIN_INTEGER_DIGITS = 0
        const val MIN_FRACTION_DIGITS = 2
    }
}