package de.bikebean.app.ui.drawer.status.location

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import de.bikebean.app.R
import de.bikebean.app.db.state.State
import de.bikebean.app.ui.drawer.status.LastChangedView.Companion.getMarkerBitmap
import de.bikebean.app.ui.drawer.status.SubStatusFragmentSmall
import de.bikebean.app.ui.utils.geo_coordinates.GeoUtils.getAccuracy
import de.bikebean.app.ui.utils.geo_coordinates.GeoUtils.getGeoCoordinates

open class LocationInformationViewSmall(
        private val lat: TextView,
        private val lng: TextView,
        private val acc: TextView,
        private val bikeMarker: ImageView,
        private val noData: TextView) {

    fun setMarker(lastLocationState: State?, sf: SubStatusFragmentSmall) {
        bikeMarker.setImageBitmap(getMarkerBitmap(lastLocationState, sf.requireContext(), sf.mf))
    }

    fun setLat(state: State) {
        lat.text = getGeoCoordinates(state)
    }

    fun setLng(state: State) {
        lng.text = getGeoCoordinates(state)
    }

    fun setAcc(state: State) {
        acc.text = getAccuracy(state)
    }

    open fun setVisible(visible: Boolean) {
        if (visible) {
            lat.visibility = View.VISIBLE
            lng.visibility = View.VISIBLE
            acc.visibility = View.VISIBLE

            noData.text = ""
            noData.visibility = View.GONE
        } else {
            lat.visibility = View.GONE
            lng.visibility = View.GONE
            acc.visibility = View.GONE

            noData.visibility = View.VISIBLE
            noData.setText(R.string.text_no_data)
        }
    }
}