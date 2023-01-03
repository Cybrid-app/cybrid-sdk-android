package app.cybrid.sdkandroid.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import app.cybrid.sdkandroid.R

class BankTransferView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0):
Component(context, attrs, defStyle) {

    enum class ViewState { LOADING, IN_LIST }

    init {

        LayoutInflater.from(context).inflate(R.layout.bankaccounts_component, this, true)
        this.composeView = findViewById(R.id.composeContent)
    }
}