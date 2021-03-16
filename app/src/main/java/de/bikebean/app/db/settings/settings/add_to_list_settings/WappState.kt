package de.bikebean.app.db.settings.settings.add_to_list_settings

import de.bikebean.app.db.sms.SmsFactory
import de.bikebean.app.db.state.State
import de.bikebean.app.db.state.StateFactory
import de.bikebean.app.ui.drawer.status.location.LocationStateViewModel

class WappState : Wapp {

    val wifiAccessPoints: State
    val cellTowers: State

    constructor(_cellTowers: State, _wifiAccessPoints: State) :
            super(SmsFactory.createSmsFromState(_cellTowers), 0.0) {
        wifiAccessPoints = _wifiAccessPoints
        cellTowers = _cellTowers
    }

    constructor(_cellTowers: State) :
            super(SmsFactory.createSmsFromState(_cellTowers), 0.0) {
        wifiAccessPoints = StateFactory.createNullState()
        cellTowers = StateFactory.createNullState()
    }

    val smsId: Int
        get() = cellTowers.smsId

    val isNull: Boolean
        get() = cellTowers.isNull || wifiAccessPoints.isNull

    fun getIfNewest(st: LocationStateViewModel): Boolean {
        return (wifiAccessPoints.equalsId(st.getConfirmedLocationSync(wifiAccessPoints))
                && cellTowers.equalsId(st.getConfirmedLocationSync(cellTowers)))
    }

}