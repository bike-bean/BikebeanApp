package de.bikebean.app.ui.drawer.map

import android.content.Context
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.utils.ColorUtils
import de.bikebean.app.R
import de.bikebean.app.ui.drawer.map.MapFragment.Companion.MAP_STYLE_HYBRID
import de.bikebean.app.ui.drawer.map.MapFragment.Companion.MAP_STYLE_NIGHT
import de.bikebean.app.ui.drawer.map.MapFragment.Companion.MAP_STYLE_NORMAL
import de.bikebean.app.ui.drawer.map.MapFragment.Companion.MAP_STYLE_SATELLITE
import de.bikebean.app.ui.utils.resource.ResourceUtils

class BikeSymbolColor(context: Context, daysSinceLastState: Double, style: Style?) {

    private val scene = when (style?.uri) {
        MAP_STYLE_SATELLITE, MAP_STYLE_HYBRID, MAP_STYLE_NIGHT -> Scene.NIGHT
        MAP_STYLE_NORMAL -> Scene.NORMAL
        else -> Scene.NORMAL
    }

    @ColorInt
    private val colorLastChange = ResourceUtils.getMarkerColor(context, daysSinceLastState)

    @ColorInt
    val colorMarker = colorLastChange

    @ColorInt
    val colorCircle = when(scene) {
        Scene.NORMAL -> colorLastChange
        Scene.NIGHT -> ContextCompat.getColor(context, R.color.white)
    }

    val colorCircleString: String = ColorUtils.colorToRgbaString(colorCircle)

    enum class Scene {
        NORMAL,     /* The Marker AND the Circle should be green-ish              */
        NIGHT       /* The Marker should be green-ish, the Circle should be white */
    }

}