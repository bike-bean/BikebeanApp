package de.bikebean.app.db.settings.settings.add_to_list_settings.location_settings

import de.bikebean.app.db.settings.settings.add_to_list_settings.LocationSetting
import de.bikebean.app.db.settings.settings.add_to_list_settings.WappState
import de.bikebean.app.db.state.State.KEY
import de.bikebean.app.ui.drawer.status.location.ResponseBody

class Acc : LocationSetting {

    constructor(
            responseBody: ResponseBody,
            wappState: WappState
    ) : super(responseBody.accuracy.toDouble(), wappState, key)

    constructor() : super(key)

    companion object {
        private val key = KEY.ACC
    }

}