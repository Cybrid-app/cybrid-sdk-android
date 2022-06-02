package app.cybrid.demoapp.ui.listComponents.view

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.cybrid.demoapp.R

class ComponentView(itemView: View) : RecyclerView.ViewHolder(itemView) {


    var name:TextView? = null

    init {

        this.name = this.itemView.findViewById(R.id.option_name)
    }
}