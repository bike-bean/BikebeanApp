package de.bikebean.app.db.type.types

import de.bikebean.app.db.settings.settings.add_to_list_settings.WappState
import de.bikebean.app.db.settings.settings.add_to_list_settings.location_settings.Acc
import de.bikebean.app.db.settings.settings.add_to_list_settings.location_settings.Lat
import de.bikebean.app.db.settings.settings.add_to_list_settings.location_settings.Lng
import de.bikebean.app.db.settings.settings.add_to_list_settings.location_settings.Location
import de.bikebean.app.db.type.Type
import de.bikebean.app.ui.drawer.status.location.ResponseBody

class LocationType(responseBody: ResponseBody, val wappState: WappState) : Type() {

    override val settings = listOf(
            Lat(responseBody, wappState),
            Lng(responseBody, wappState),
            Acc(responseBody, wappState),
            Location(0.0, wappState)
    )
}