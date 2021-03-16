package de.bikebean.app.ui.drawer.map

import android.os.Bundle
import com.mapbox.geojson.Point
import de.bikebean.app.db.state.State
import de.bikebean.app.ui.utils.date.DateUtils.getDaysSinceState

class MapFragmentCurrent : MapFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        listOf(
                mapFragmentViewModel.confirmedLocationLat,
                mapFragmentViewModel.confirmedLocationLng,
                mapFragmentViewModel.confirmedLocationAcc
        ).forEach { it.observe(viewLifecycleOwner, ::setMapElements) }
    }

    private fun setMapElements(statuses: List<State>) {
        statuses.firstOrNull()?.run {
            (setterMap[State.KEY.getValue(this)] ?: return)(this)
        }
    }

    private val setterMap = mapOf(
            State.KEY.LAT to ::setPosition,
            State.KEY.LNG to ::setPosition,
            State.KEY.ACC to ::setRadius
    )

    private fun setPosition(state: State) {
        setPointFromState(state)
        updateMarkerPosition()
        updateCircle()

        daysSinceLastState = getDaysSinceState(state)
        updateColors()
    }

    private fun setRadius(state: State) {
        meterRadius = state.value
        updateCircle()
    }

    private fun setPointFromState(state: State) : Point = when (State.KEY.getValue(state)) {
        State.KEY.LAT -> Point.fromLngLat(currentPosition.longitude(), state.value)
        State.KEY.LNG -> Point.fromLngLat(state.value, currentPosition.latitude())
        else -> currentPosition
    }.also { currentPosition = it }

    override var currentPosition: Point =
            Point.fromLngLat(CENTER_GERMANY.longitude, CENTER_GERMANY.latitude)

    override var daysSinceLastState: Double = 0.0

    override var meterRadius: Double = 0.0

}