package app.cybrid.sdkandroid.components.listprices.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.cybrid.cybrid_api_bank.client.models.AssetBankModel
import app.cybrid.cybrid_api_bank.client.models.SymbolPriceBankModel
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.listprices.view.SymbolPriceBankView

class ListPricesAssetsAdapter(items: List<SymbolPriceBankModel>, assets: List<AssetBankModel>) :
    RecyclerView.Adapter<SymbolPriceBankView>() {

    private var items: List<SymbolPriceBankModel>? = null
    private var assets: List<AssetBankModel>? = null

    init {

        this.items = items
        this.assets = assets
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymbolPriceBankView {

        val layoutView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_prices_assets_component_item, parent, false)
        return SymbolPriceBankView(layoutView)
    }

    override fun onBindViewHolder(holder: SymbolPriceBankView, position: Int) {

        holder.setIsRecyclable(false)
        val symbol: SymbolPriceBankModel = items!![position]

        // -- Setting the views
        holder.name?.text = symbol.symbol
        holder.value?.text = symbol.buyPrice.toString()
    }

    override fun getItemCount(): Int {
        return if (items == null) 0 else items!!.size
    }

    fun reload(items: List<SymbolPriceBankModel>?) {

        this.items = items
        notifyItemRangeInserted(0, this.items!!.size - 1)
    }
}