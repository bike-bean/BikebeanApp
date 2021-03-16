package de.bikebean.app.ui.utils.geo_coordinates

import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng
import de.bikebean.app.db.state.State
import java.lang.Math.toRadians
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object GeoUtils {

    @JvmStatic
    fun getGeoCoordinates(state: State): String = when (State.KEY.getValue(state)) {
        State.KEY.LAT -> true
        else -> false
    }.let { isLat ->
        GeoCoordinates(state.value, isLat).toString()
    }

    @JvmStatic
    fun getAccuracy(state: State): String = "+/- %.1f m".format(state.value)

    fun getGeoPointByDistance(base: Point, distanceMeter: Double, alpha: Double): LatLng = LatLng(
            base.latitude() + meterToDegreeLat(distanceMeter * sin(alpha)),
            base.longitude() + meterToDegreeLng(
                    distanceMeter * cos(alpha),
                    base.latitude()
            )
    )

    private fun meterToDegreeLat(meter: Double): Double =
            meter * 360 / EARTH_CIRCUMFERENCE_METERS

    private fun meterToDegreeLng(meter: Double, lat: Double): Double =
            meter * 360 / (cos(toRadians(lat)) * EARTH_CIRCUMFERENCE_METERS)

    private const val EARTH_CIRCUMFERENCE_METERS = 6378137 * 2 * PI

}