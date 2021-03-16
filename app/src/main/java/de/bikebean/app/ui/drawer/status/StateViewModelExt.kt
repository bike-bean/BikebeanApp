package de.bikebean.app.ui.drawer.status

import de.bikebean.app.db.DatabaseEntity
import de.bikebean.app.db.MutableObject
import de.bikebean.app.db.state.State
import de.bikebean.app.db.state.State.KEY
import de.bikebean.app.db.type.Type

fun StateViewModel.getConfirmedStateSync(key: KEY): State? {
    return getStateSync(mRepository::getConfirmedStateSync, key, 0)
}

fun StateViewModel.getStateByIdSync(key: KEY, smsId: Int): State? {
    return getStateSync(mRepository::getStateByIdSync, key, smsId)
}

private fun getStateSync(
        stateGetter: (String, Int) -> List<DatabaseEntity>,
        key: KEY, smsId: Int): State? {
    val state = MutableObject()
    return state.getDbEntitySync(stateGetter, key.get(), smsId) as State?
}

fun insert(st: StateViewModel?, type: Type) {
    st?.insert(type.settings)
}