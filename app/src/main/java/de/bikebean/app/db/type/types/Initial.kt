package de.bikebean.app.db.type.types

import de.bikebean.app.db.settings.Setting
import de.bikebean.app.db.settings.settings.add_to_list_settings.Battery
import de.bikebean.app.db.settings.settings.add_to_list_settings.location_settings.Acc
import de.bikebean.app.db.settings.settings.add_to_list_settings.location_settings.Lat
import de.bikebean.app.db.settings.settings.add_to_list_settings.location_settings.Lng
import de.bikebean.app.db.settings.settings.add_to_list_settings.location_settings.Location
import de.bikebean.app.db.settings.settings.add_to_list_settings.number_settings.CellTowers
import de.bikebean.app.db.settings.settings.add_to_list_settings.number_settings.WifiAccessPoints
import de.bikebean.app.db.type.Type

class Initial : Type() {

    override val settings: List<Setting> = listOf(
            Battery(),
            Location(),
            Acc(),
            Lat(),
            Lng(),
            CellTowers(),
            WifiAccessPoints()
    )

}