package de.bikebean.app.db.settings.settings.add_to_list_settings

import de.bikebean.app.db.settings.settings.AddToListSetting
import de.bikebean.app.db.sms.Sms
import de.bikebean.app.db.state.State
import de.bikebean.app.db.state.State.KEY
import de.bikebean.app.db.state.StateFactory

class Battery : AddToListSetting {

    constructor(
            sms: Sms, batteryGetter: () -> Double
    ) : super(StateFactory.createNumberState(
            sms, key, batteryGetter(), State.STATUS.CONFIRMED
    ))

    constructor() : super(StateFactory.createUnsetState(key, UNSET_BATTERY))

    companion object {
        const val UNSET_BATTERY = -1.0
        private val key = KEY.BATTERY
    }
}