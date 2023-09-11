package app.cybrid.demoapp.ui.listComponents.entity

import android.content.res.Resources
import app.cybrid.demoapp.R
import app.cybrid.demoapp.core.App

class Component() {

    var id: Int = 0
    var name: String = ""

    constructor(id: Int, name: String) : this() {
        this.id = id
        this.name = name
    }

    companion object {

        fun getComponents(): ArrayList<Component> {

            val components = ArrayList<Component>()

            // -- Trade Component
            val tradeTitle = App.context?.getString(R.string.activity_list_components_trade_component)
            val tradeFlow = Component(1, tradeTitle ?: "")
            components.add(tradeFlow)

            // -- Transfer Component
            val transferTitle = App.context?.getString(R.string.activity_list_components_transfer_component)
            val transferComponent = Component(2, transferTitle ?: "")
            components.add(transferComponent)

            // -- Accounts Component
            val accountsTitle = App.context?.getString(R.string.activity_list_components_accounts_component)
            val accountsComponent = Component(3, accountsTitle ?: "")
            components.add(accountsComponent)

            // -- KYC Component
            val kycTitle = App.context?.getString(R.string.activity_list_components_kyc_component)
            val kycComponent = Component(4, kycTitle ?: "")
            components.add(kycComponent)

            // -- Bank Accounts Component
            val backAccountsTitle = App.context?.getString(R.string.activity_list_components_bank_accounts_component)
            val bankAccountsComponent = Component(5, backAccountsTitle ?: "")
            components.add(bankAccountsComponent)

            // -- Bank Accounts Component
            val externalWalletsTitle = App.context?.getString(R.string.activity_list_components_external_wallets_component)
            val externalWalletComponent = Component(6, externalWalletsTitle ?: "")
            components.add(externalWalletComponent)

            // --
            return components
        }
    }
}