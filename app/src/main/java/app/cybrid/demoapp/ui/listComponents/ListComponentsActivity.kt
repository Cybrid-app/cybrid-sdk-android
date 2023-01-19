package app.cybrid.demoapp.ui.listComponents

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import app.cybrid.demoapp.R
import app.cybrid.demoapp.core.App
import app.cybrid.demoapp.listener.ComponentListener
import app.cybrid.demoapp.ui.listComponents.adapter.ListComponentsAdapter
import app.cybrid.demoapp.ui.listComponents.entity.Component
import app.cybrid.demoapp.ui.listPricesComponent.ListPricesAssetsComponentActivity
import app.cybrid.demoapp.ui.listPricesComponent.ListPricesComponentActivity
import app.cybrid.sdkandroid.components.activity.AccountsActivity
import app.cybrid.sdkandroid.components.activity.KYCActivity
import app.cybrid.sdkandroid.components.activity.BankAccountsActivity
import app.cybrid.sdkandroid.components.activity.TradeActivity

class ListComponentsActivity : AppCompatActivity(), ComponentListener {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_components)
        this.initComponentsList()
    }

    private fun initComponentsList() {

        val list = findViewById<RecyclerView>(R.id.list)
        App.createVerticalRecyclerList(list, this)

        val adapter = ListComponentsAdapter(Component.getComponents())
        adapter.listener = this
        list.adapter = adapter
    }

    override fun onComponentClick(component: Component) {

        when (component.id) {

            1 -> startActivity(Intent(this, ListPricesComponentActivity::class.java))
            2 -> startActivity(Intent(this, ListPricesAssetsComponentActivity::class.java))
            3 -> startActivity(Intent(this, TradeActivity::class.java))
            4 -> startActivity(Intent(this, AccountsActivity::class.java))
            5 -> startActivity(Intent(this, KYCActivity::class.java))
            6 -> startActivity(Intent(this, BankAccountsActivity::class.java))
        }
    }
}