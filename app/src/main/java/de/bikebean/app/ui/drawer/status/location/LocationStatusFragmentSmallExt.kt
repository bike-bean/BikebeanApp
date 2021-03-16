package de.bikebean.app.ui.drawer.status.location

import androidx.lifecycle.LifecycleOwner
import de.bikebean.app.db.state.State
import de.bikebean.app.db.type.types.LocationType
import de.bikebean.app.ui.drawer.status.insert
import de.bikebean.app.ui.utils.wapp.WappSorter

fun LocationStatusFragmentSmall.startObservingWapp(l: LifecycleOwner) {
    st.wapp.observe(l, ::updateWapp)
}

private fun LocationStatusFragmentSmall.updateWapp(states: List<State>) {
    WappSorter(states, st).wappStates.forEach { wappState ->
        if (wappState.isNull || isAlreadyParsed(wappState)) return

        LocationUpdater(
                requireContext(), st, lv,
                ::updateLatLngAcc, wappState
        ).execute()
    }
}

private fun LocationStatusFragmentSmall.updateLatLngAcc(locationType: LocationType) {
    sm.markParsed(locationType.wappState.sms)
    st.confirmWapp(locationType.wappState)
    insert(st, locationType)
}
