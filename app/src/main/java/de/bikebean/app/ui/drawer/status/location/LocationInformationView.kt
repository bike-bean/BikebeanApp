package de.bikebean.app.ui.drawer.status.location

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import de.bikebean.app.db.state.State

class LocationInformationView(
        lat: TextView, lng: TextView, acc: TextView,
        bikeMarker: ImageView, noData: TextView,
        private val openMapButton: Button,
        private val routeButton: Button,
        private val shareButton: Button,
        private val noCellTowersText: TextView,
        private val noWifiAccessPointsText: TextView)
    : LocationInformationViewSmall(lat, lng, acc, bikeMarker, noData) {

    fun setOnClickListeners(
            onOpenMapClick: View.OnClickListener,
            onRouteClick: View.OnClickListener,
            onShareClick: View.OnClickListener) {
        openMapButton.setOnClickListener(onOpenMapClick)
        routeButton.setOnClickListener(onRouteClick)
        shareButton.setOnClickListener(onShareClick)
    }

    fun setNumbers(state: State) {
        val cellTowersString = "Diese Informationen basieren auf ${state.value.toInt()} Funktürmen"
        val wifiAccessPointsString = "und ${state.value.toInt()} WLAN-Hotspots."

        if (State.KEY.getValue(state) == State.KEY.NO_CELL_TOWERS)
            noCellTowersText.text = cellTowersString
        else if (State.KEY.getValue(state) == State.KEY.NO_WIFI_ACCESS_POINTS)
            noWifiAccessPointsText.text = wifiAccessPointsString
    }

    override fun setVisible(visible: Boolean) {
        super.setVisible(visible)

        if (visible) {
            openMapButton.visibility = View.VISIBLE
            routeButton.visibility = View.VISIBLE
            shareButton.visibility = View.VISIBLE
            noCellTowersText.visibility = View.VISIBLE
            noWifiAccessPointsText.visibility = View.VISIBLE
        } else {
            openMapButton.visibility = View.GONE
            routeButton.visibility = View.GONE
            shareButton.visibility = View.GONE
            noCellTowersText.visibility = View.GONE
            noWifiAccessPointsText.visibility = View.GONE
        }
    }
}