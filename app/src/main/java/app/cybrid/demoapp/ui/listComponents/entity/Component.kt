package app.cybrid.demoapp.ui.listComponents.entity

import android.content.res.Resources
import app.cybrid.demoapp.R

class Component() {

    var id: Int = 0
    var name: String = ""

    constructor(id: Int, name: String) : this() {
        this.id = id
        this.name = name
    }

    companion object {

        fun getComponents(): ArrayList<Component> {

            var resources = Resources.getSystem()
            val components = ArrayList<Component>()

            // -- Trade Component
            val tradeFlow = Component(1, resources.getString(R.string.activity_list_components_trade_component))
            components.add(tradeFlow)

            // -- Transfer Component
            val transferComponent = Component(2, resources.getString(R.string.activity_list_components_transfer_component))
            components.add(transferComponent)

            // -- Accounts Component
            val accountsComponent = Component(3, resources.getString(R.string.activity_list_components_accounts_component))
            components.add(accountsComponent)

            // -- KYC Component
            val kycComponent = Component(4, resources.getString(R.string.activity_list_components_kyc_component))
            components.add(kycComponent)

            // -- Bank Accounts Component
            val bankAccountsComponent = Component(5, resources.getString(R.string.activity_list_components_bank_accounts_component))
            components.add(bankAccountsComponent)

            // -- Bank Accounts Component
            val externalWalletComponent = Component(6, resources.getString(R.string.activity_list_components_external_wallets_component))
            components.add(externalWalletComponent)

            // --
            return components
        }
    }
}