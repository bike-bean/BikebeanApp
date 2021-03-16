package de.bikebean.app.ui.utils.geo_coordinates

class GeoCoordinates(decimal: Double, private val isLat: Boolean) {

    private val degrees: Int = decimal.toInt()
    private val minutes: Int = (60 * (decimal - degrees)).toInt()
    private val seconds: Double = 3600 * (decimal - degrees) - 60 * minutes

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

    override fun toString(): String =
            "$degrees°$minutes'%.3f''$direction$delimiter".format(seconds)
    
}