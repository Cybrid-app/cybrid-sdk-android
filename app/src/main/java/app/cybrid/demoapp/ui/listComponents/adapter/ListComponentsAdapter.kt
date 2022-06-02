package app.cybrid.demoapp.ui.listComponents.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.cybrid.demoapp.R
import app.cybrid.demoapp.listener.ComponentListener
import app.cybrid.demoapp.ui.listComponents.entity.Component
import app.cybrid.demoapp.ui.listComponents.view.ComponentView

class ListComponentsAdapter(items: ArrayList<Component>) : RecyclerView.Adapter<ComponentView>() {

    private var items: ArrayList<Component>? = null
    var listener: ComponentListener? = null

    init {

        this.items = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComponentView {

        val layoutView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_list_components_item, parent, false)
        return ComponentView(layoutView)
    }

    override fun onBindViewHolder(holder: ComponentView, position: Int) {

        holder.setIsRecyclable(false)
        val component: Component = items!![position]

        // -- Setting the views
        holder.name?.text = component.name
        holder.itemView.setOnClickListener { listener?.onComponentClick(component) }
    }

    override fun getItemCount(): Int {
        return if (items == null) 0 else items!!.size
    }

    fun reload(items: ArrayList<Component>?) {

        this.items = items
        notifyItemRangeInserted(0, this.items!!.size - 1)
    }

    fun clear() {
        val size: Int = items!!.size
        if (size > 0) {
            for (i in 0 until size) {
                items!!.removeAt(0)
            }
            notifyItemRangeRemoved(0, size)
        }
    }
}