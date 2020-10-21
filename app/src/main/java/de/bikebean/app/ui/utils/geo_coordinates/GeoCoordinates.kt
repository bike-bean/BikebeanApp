package de.bikebean.app.ui.utils.geo_coordinates

class GeoCoordinates(decimal: Double, lat: Boolean) {

    private val degrees: Int = decimal.toInt()
    private val minutes: Int
    private val seconds: Double
    private val isLat: Boolean

    override fun toString(): String {
        return "$degrees°$minutes'%.3f''$direction$delimiter".format(seconds)
    }

    private val direction: String
        get() = when {
            isLat && degrees >= 0 -> "N"
            isLat -> "S"
            degrees >= 0 -> "E"
            else -> "W"
        }

    private val delimiter: String
        get() = when {
            isLat -> ","
            else -> ""
        }

    init {
        minutes = (60 * (decimal - degrees)).toInt()
        seconds = 3600 * (decimal - degrees) - 60 * minutes
        isLat = lat
    }
}