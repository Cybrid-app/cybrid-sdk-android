package app.cybrid.demoapp.ui.listComponents.entity

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
            val tradeFlow = Component(1, "Trade Component")
            components.add(tradeFlow)

            // -- Transfer Component
            val transferComponent = Component(2, "Transfer Component")
            components.add(transferComponent)

            // -- Accounts Component
            val accountsComponent = Component(3, "Accounts Component")
            components.add(accountsComponent)

            // -- KYC Component
            val kycComponent = Component(4, "KYC Component")
            components.add(kycComponent)

            // -- Bank Accounts Component
            val bankAccountsComponent = Component(5, "Bank Accounts Component")
            components.add(bankAccountsComponent)

            // -- Bank Accounts Component
            val externalWalletComponent = Component(6, "External Wallets Component")
            components.add(externalWalletComponent)

            // --
            return components
        }
    }
}