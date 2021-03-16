package de.bikebean.app.ui.drawer.map

import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.Symbol
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions

class BikeSymbolManager(
        mapView: MapView,
        mapboxMap: MapboxMap,
        style: Style,
        onClickListener: (Symbol) -> Boolean,
        bikeSymbolIcon: BikeSymbolIcon,
        currentPosition: Point
) : SymbolManager(mapView, mapboxMap, style) {

    private val bikeMarkers = List(2) {
        addMarker(currentPosition, bikeSymbolIcon.iconIds[it])
    }

    init {
        iconAllowOverlap = true
        addClickListener(onClickListener)
    }

    private fun addMarker(currentPosition: Point, iconId: String) : Symbol = create(
            SymbolOptions()
                    .withGeometry(currentPosition)
                    .withIconImage(iconId)
    )

    fun updatePosition(point: Point, setCamera: () -> Unit) {
        bikeMarkers.let { them ->
            them.forEach {
                it.geometry = point
                update(them)
            }
        }

        setCamera()
    }

    fun updateIcon(bikeSymbolIcon: BikeSymbolIcon) {
        delete(bikeMarkers[0])
        addMarker(bikeMarkers[1].geometry, bikeSymbolIcon.iconIds[0])
    }

}