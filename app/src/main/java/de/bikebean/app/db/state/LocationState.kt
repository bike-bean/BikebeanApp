package de.bikebean.app.db.state

import android.os.Bundle
import de.bikebean.app.db.DatabaseEntity
import de.bikebean.app.db.state.State.KEY
import de.bikebean.app.ui.utils.date.DateUtils
import de.bikebean.app.ui.utils.date.DateUtils.getDaysSinceState

class LocationState(
        latState: State,
        lngState: State,
        accState: State,
        noCellTowersState: State,
        noWifiAccessPointsState: State
) : DatabaseEntity() {

    val smsId = latState.smsId
    val timestamp = latState.timestamp

    val lat = latState.value
    val lng = lngState.value
    val acc = accState.value

    private val daysSinceLastState = getDaysSinceState(latState)
    private val noCellTowers = noCellTowersState.value.toInt()
    private val noWifiAccessPoints = noWifiAccessPointsState.value.toInt()

    val args: Bundle
        get() = LocationBundle()
                .putDouble(KEY.LAT, lat)
                .putDouble(KEY.LNG, lng)
                .putDouble(KEY.ACC, acc)
                .putDaysSinceLastState(daysSinceLastState)
                .putInt(KEY.NO_CELL_TOWERS, noCellTowers)
                .putInt(KEY.NO_WIFI_ACCESS_POINTS, noWifiAccessPoints)
                .get()

    internal class LocationBundle {

        private val bundle = Bundle()

        fun get(): Bundle = bundle

        fun putDouble(key: KEY, value: Double): LocationBundle = apply {
            bundle.putDouble(key.get(), value)
        }

        fun putDaysSinceLastState(value: Double): LocationBundle = apply {
            bundle.putDouble(DateUtils.DAYS_SINCE_LAST_STATE, value)
        }

        fun putInt(key: KEY, value: Int): LocationBundle = apply {
            bundle.putInt(key.get(), value)
        }
    }

    override val nullType: DatabaseEntity?
        get() = null

    override fun createReportTitle(): String? = null

    override fun createReport(): String? = null

}