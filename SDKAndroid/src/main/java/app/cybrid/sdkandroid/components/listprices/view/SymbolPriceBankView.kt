package app.cybrid.sdkandroid.components.listprices.view

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.cybrid.sdkandroid.R

class SymbolPriceBankView(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var icon:ImageView? = null
    var name:TextView? = null
    var value:TextView? = null

    init {

        this.icon = itemView.findViewById(R.id.crypto_icon)
        this.name = itemView.findViewById(R.id.crypto_name)
        this.value = itemView.findViewById(R.id.crypto_price)
    }
}