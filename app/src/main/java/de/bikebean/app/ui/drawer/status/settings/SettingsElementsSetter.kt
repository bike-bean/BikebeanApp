package de.bikebean.app.ui.drawer.status.settings

import de.bikebean.app.db.state.State

interface SettingsElementsSetter {
    fun setIntervalElementsConfirmed(state: State)
    fun setWifiElementsConfirmed(state: State)
    fun setWarningNumberElementsConfirmed(state: State)
    fun setStatusElementsConfirmed(state: State)

    fun setIntervalElementsPending(state: State)
    fun setWifiElementsPending(state: State)
    fun setWarningNumberElementsPending(state: State)

    fun setWarningNumberElementsUnset(state: State)
    fun setStatusElementsUnset()
}