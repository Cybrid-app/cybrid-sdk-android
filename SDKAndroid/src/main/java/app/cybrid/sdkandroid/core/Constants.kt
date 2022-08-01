package app.cybrid.sdkandroid.core

class Constants {

    enum class QuoteConfirmation(val id:String) {
        LoadingIndicator("QuoteConfirmationLoadingProgressIndicator"),
        ContentPurchaseAmount("QuoteConfirmationContentPurchaseAmount"),
        ContentPurchaseQuantity("QuoteConfirmationContentPurchaseQuantity"),
        ContentFee("QuoteConfirmationContentPurchaseFee"),
        ContentButtons("QuoteConfirmationContentButtons")
    }

    enum class AccountsViewTestTags(val id:String) {
        Surface("AccountsView_MainSurface"),
        Loading("AccountsView_Loading"),
        List("AccountsView_List"),
        Balance("AccountsView_Balance")
    }

    companion object {

        const val MIN_INTEGER_DIGITS = 0;
        const val MIN_FRACTION_DIGITS = 2;
    }
}