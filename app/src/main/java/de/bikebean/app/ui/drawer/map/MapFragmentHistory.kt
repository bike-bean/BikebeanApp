package de.bikebean.app.ui.drawer.map

import android.os.Bundle
import com.mapbox.geojson.Point
import de.bikebean.app.db.state.State
import de.bikebean.app.ui.utils.date.DateUtils.getDaysSinceState

class MapFragmentHistory : MapFragment() {

    private var args: Bundle? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        args = arguments

        args?.let {
            Point.fromLngLat(
                    it.getDouble(State.KEY.LNG.get()),
                    it.getDouble(State.KEY.LAT.get())
            )
        }?.also {
            currentPosition = it
        }

        daysSinceLastState = getDaysSinceState(args)
        meterRadius = args?.getDouble(State.KEY.ACC.get()) ?: 0.0
    }

    override var currentPosition: Point =
            Point.fromLngLat(CENTER_GERMANY.longitude, CENTER_GERMANY.latitude)

    override var daysSinceLastState: Double = 0.0

    override var meterRadius: Double = 0.0

}