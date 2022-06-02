package app.cybrid.demoapp.listener

import app.cybrid.demoapp.ui.listComponents.entity.Component

interface ComponentListener {

    fun onComponentClick(component: Component)
}