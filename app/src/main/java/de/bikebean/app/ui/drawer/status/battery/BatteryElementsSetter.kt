package de.bikebean.app.ui.drawer.status.battery

import de.bikebean.app.db.state.State

interface BatteryElementsSetter {
    fun setBatteryElementsConfirmed(state: State)
    fun setIntervalElementsConfirmed()
    fun setWifiElementsConfirmed(state: State)

    fun setBatteryElementsPending(state: State)
    fun setBatteryElementsUnset(state: State)

    fun setButtonEnabled()
    fun setButtonDisabled()
    fun setButtonForceEnabled()
    fun setButtonForceDisabled()
}