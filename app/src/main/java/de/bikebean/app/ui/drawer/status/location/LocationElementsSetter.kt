package de.bikebean.app.ui.drawer.status.location

import de.bikebean.app.db.state.State

interface LocationElementsSetter {
    fun setLocationElementsConfirmed(state: State)
    fun setLatConfirmed(state: State)
    fun setLngConfirmed(state: State)
    fun setAccConfirmed(state: State)
    fun setLocationElementsProgressTimeConfirmed()
    fun setLocationElementsNumbersConfirmed(state: State)

    fun setLocationElementsPending(state: State)
    fun setLocationElementsProgressTimePending(state: State)
    fun setLocationElementsProgressTextPending()

    fun setLocationElementsUnset()
    fun setLocationElementsProgressTimeUnset()

    fun setLocationPending(pending: Boolean)
}