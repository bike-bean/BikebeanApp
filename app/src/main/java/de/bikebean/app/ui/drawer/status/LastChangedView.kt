package de.bikebean.app.ui.drawer.status

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import de.bikebean.app.R
import de.bikebean.app.db.state.State
import de.bikebean.app.ui.drawer.map.MapFragmentViewModel
import de.bikebean.app.ui.drawer.map.MapMarkerCache
import de.bikebean.app.ui.utils.date.DateUtils.getDaysSinceState
import de.bikebean.app.ui.utils.date.DateUtils.getLastChanged
import de.bikebean.app.ui.utils.resource.ResourceUtils.getMarkerColor

class LastChangedView(private val text: TextView, private val image: ImageView) {

    fun set(state: State?, context: SubStatusFragment) {
        state ?: run {
            text.setText(R.string.no_data)
            image.setImageBitmap(getLastChangedBitmap(
                    null, context.requireContext(), context.mf)
            )
            return
        }

        text.text = getLastChanged(state)
        image.setImageBitmap(getLastChangedBitmap(state, context.requireContext(), context.mf))
    }

    companion object {

        @JvmStatic
        private fun getLastChangedBitmap(
                state: State?,
                context: Context,
                mf: MapFragmentViewModel): Bitmap? {
            @ColorInt val color = getMarkerColor(context, getDaysSinceState(state))
            return mf.getMapMarkerBitmap(MapMarkerCache.DRAWABLES.lastChangedIndicator, color)
        }

        @JvmStatic
        fun getMarkerBitmap(
                state: State?,
                context: Context,
                mf: MapFragmentViewModel): Bitmap? {
            @ColorInt val color = getMarkerColor(context, getDaysSinceState(state))
            return mf.getMapMarkerBitmap(MapMarkerCache.DRAWABLES.mapMarker, color)
        }
    }
}
