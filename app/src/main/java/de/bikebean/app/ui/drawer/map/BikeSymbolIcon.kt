package de.bikebean.app.ui.drawer.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.utils.BitmapUtils
import de.bikebean.app.R

class BikeSymbolIcon(
        context: Context,
        bikeSymbolColor: BikeSymbolColor,
        mapFragmentViewModel: MapFragmentViewModel,
        style: Style?
) {

    private val icons = Pair(
            R.drawable.ic_bike_map_marker_bg,
            R.drawable.ic_bike_map_marker_fg
    )

    private val drawables = List(2) {
        ContextCompat.getDrawable(context, icons.toList()[it])
    }

    val iconIds = listOf("bike_icon" + bikeSymbolColor.colorMarker, "bike_icon_back")

    private val bitmapBg = drawableToBitmap(context, drawables[0])

    private val bitmapFg = mapFragmentViewModel.getMapMarkerBitmap(
            drawables[1], bikeSymbolColor.colorMarker
    )

    init {
        style?.apply {
            addImage(iconIds[0], bitmapFg, false)
            addImage(iconIds[1], bitmapBg, false)
        }
    }

    private fun drawableToBitmap(context: Context, source: Drawable?) : Bitmap =
            BitmapUtils.getBitmapFromDrawable(source) ?: BitmapFactory.decodeResource(
                    context.resources, R.drawable.mapbox_marker_icon_default
            )

}