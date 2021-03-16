package de.bikebean.app.ui.utils.wapp

import de.bikebean.app.db.settings.settings.add_to_list_settings.WappState
import de.bikebean.app.db.state.State
import de.bikebean.app.ui.drawer.status.location.LocationStateViewModel
import kotlin.math.min

class WappSorter(
        states: List<State>,
        private val st: LocationStateViewModel
        ) {

    private val cellTowersStates: List<State> = states.filter { it.isWappCellTowers }
    private val wifiAccessPointsStates: MutableList<State> =
            states.filter { it.isWappWifiAccessPoints } as MutableList<State>

    val wappStates: List<WappState> = List(getLength()) {
        createWappStateIfMatches(cellTowersStates[it])
    }

    private fun createWappStateIfMatches(cellTowersState: State) : WappState =
            wifiAccessPointsStates.filter {
                cellTowersState.isWithinDayRange(it)
            }.let {
                when {
                    it.isEmpty() -> WappState(cellTowersState)
                    else -> {
                        wifiAccessPointsStates.remove(it.first())
                        WappState(
                                st.getCellTowersByWappSync(cellTowersState),
                                st.getWifiAccessPointsByWappSync(it.first())
                        )
                    }
                }
            }

    private fun getLength() = min(cellTowersStates.size, wifiAccessPointsStates.size)

}