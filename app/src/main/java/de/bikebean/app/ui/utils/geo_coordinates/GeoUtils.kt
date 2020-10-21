package de.bikebean.app.ui.utils.geo_coordinates

import de.bikebean.app.db.state.State

object GeoUtils {

    @JvmStatic
    fun getGeoCoordinates(state: State): String {
        return if (State.KEY.getValue(state) == State.KEY.LAT)
            GeoCoordinates(state.value, true).toString()
        else
            GeoCoordinates(state.value, false).toString()
    }

    @JvmStatic
    fun getAccuracy(state: State): String {
        return "+/- %.1f m".format(state.value)
    }
}