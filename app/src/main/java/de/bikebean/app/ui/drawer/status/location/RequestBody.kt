package de.bikebean.app.ui.drawer.status.location

import de.bikebean.app.db.settings.settings.add_to_list_settings.WappState
import de.bikebean.app.db.settings.settings.add_to_list_settings.number_settings.CellTowers
import de.bikebean.app.db.settings.settings.add_to_list_settings.number_settings.WifiAccessPoints
import de.bikebean.app.ui.drawer.log.LogViewModel

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class RequestBody(
        val cellTowers: List<CellTowers.CellTower>,
        val wifiAccessPoints: List<WifiAccessPoints.WifiAccessPoint>)

@Serializable
data class ResponseBody(
        val location: LatLng,
        val accuracy: Int
)

@Serializable
data class LatLng(
        val lat: Double,
        val lng: Double
)

internal class LocationApiBodyCreator(
        wappState: WappState,
        private val lv: LogViewModel) {

    private val cellTowers: List<CellTowers.CellTower>
    private val wifiAccessPoints: List<WifiAccessPoints.WifiAccessPoint>

    init {
        with (WifiAccessPoints(wappState)) {
            wifiAccessPoints = list
            lv.d("numberWifiAccessPoints: $number")
        }

        with (CellTowers(wappState)) {
            cellTowers = list
            lv.d("numberCellTowers: $number")
        }
    }

    fun create(): String {
        return Json.encodeToString(
                RequestBody(cellTowers, wifiAccessPoints)
        ).also { lv.d(it) }
    }

}