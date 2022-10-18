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

            // -- ListPrices Component
            val pricesComponents = Component(1, "ListPrices Component")

            // -- ListPrices Assets Component
            val pricesAssetsComponents = Component(2, "ListPrices + Assets Component")

            // -- Trade Flow
            val tradeFlow = Component(3, "TradeFlow")
            components.add(tradeFlow)

            // -- Accounts Component
            val accountsComponent = Component(4, "Accounts Component")
            components.add(accountsComponent)

            // -- Plaid Component
            val plaidComponent = Component(5, "Plaid Integration")
            components.add(plaidComponent)

            // -- Persona Component
            val personaComponent = Component(6, "Persona Integration")
            components.add(personaComponent)

            // -- KYC Component
            val kycComponent = Component(7, "KYC Component")
            components.add(kycComponent)

            // --
            return components
        }
    }
}