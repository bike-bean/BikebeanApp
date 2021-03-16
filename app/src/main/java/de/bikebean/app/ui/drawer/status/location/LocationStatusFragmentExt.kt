package de.bikebean.app.ui.drawer.status.location

import androidx.lifecycle.LifecycleOwner
import de.bikebean.app.db.settings.settings.add_to_list_settings.WappState
import de.bikebean.app.db.state.State
import de.bikebean.app.db.type.types.LocationType
import de.bikebean.app.ui.drawer.status.insert
import de.bikebean.app.ui.utils.wapp.WappSorter

fun LocationStatusFragment.startObservingWapp(l: LifecycleOwner) {
    st.wapp.observe(l, ::updateWapp)
}

private fun LocationStatusFragment.updateWapp(states: List<State>) {
    WappSorter(states, st).wappStates.forEach { wappState ->
        if (wappState.isNull || isAlreadyParsed(wappState)) return

        LocationUpdater(
                requireContext(), st, lv,
                ::updateLatLngAcc, wappState
        ).execute()
    }
}

// cached copy of parsed SMS
private val parsedSms: MutableList<Int> = mutableListOf()

fun isAlreadyParsed(wappState: WappState): Boolean =
    with(listOf(
            wappState.cellTowers.id,
            wappState.wifiAccessPoints.id
    )) {
        when {
            any(parsedSms::contains) -> true
            else -> false.also { forEach{ parsedSms.add(it) } }
        }
    }

private fun LocationStatusFragment.updateLatLngAcc(locationType: LocationType) {
    sm.markParsed(locationType.wappState.sms)
    st.confirmWapp(locationType.wappState)
    insert(st, locationType)
}
