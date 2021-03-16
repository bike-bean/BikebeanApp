package de.bikebean.app.db.settings.settings.add_to_list_settings.location_settings

import de.bikebean.app.db.settings.settings.add_to_list_settings.LocationSetting
import de.bikebean.app.db.settings.settings.add_to_list_settings.WappState
import de.bikebean.app.db.state.State.KEY

class Location : LocationSetting {

    constructor(
            location: Double,
            wappState: WappState
    ) : super(location, wappState, key)

    constructor(wappState: WappState) : super(wappState.sms, key)

    constructor() : super(key)

    companion object {
        private val key = KEY.LOCATION
    }

}