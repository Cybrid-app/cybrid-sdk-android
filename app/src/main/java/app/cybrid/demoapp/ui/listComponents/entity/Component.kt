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
            components.add(pricesComponents)

            // -- ListPrices Assets Component
            val pricesAssetsComponents = Component(2, "ListPrices + Assets Component")
            components.add(pricesAssetsComponents)

            // --
            return components
        }
    }
}