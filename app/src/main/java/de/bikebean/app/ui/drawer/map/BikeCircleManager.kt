package de.bikebean.app.ui.drawer.map

import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.*
import de.bikebean.app.ui.utils.geo_coordinates.GeoUtils
import kotlin.math.PI

class BikeCircleManager(
        mapView: MapView,
        mapboxMap: MapboxMap,
        style: Style,
        onClickListener: (Fill) -> Boolean,
        bikeSymbolColor: BikeSymbolColor,
        currentPosition: Point,
        meterRadius: Double
) : FillManager(mapView, mapboxMap, style) {

    private val bikeCircleFill: Fill
    private val bikeCircleLine: Line
    private val lineManager: LineManager

    private fun calculateCircleCoordinates(
            currentPosition: Point,
            meterRadius: Double
    ): List<LatLng> = CIRCLE_INT_POINTS.let { size ->
        List(size + 1) {
            GeoUtils.getGeoPointByDistance(
                    currentPosition,
                    meterRadius,
                    2 * PI * (it / (size.toDouble()))
            )
        }
    }

    init {
        addClickListener(onClickListener)
        val circleCoordinates = calculateCircleCoordinates(currentPosition, meterRadius)
        bikeCircleFill = create(FillOptions()
                .withLatLngs(listOf(circleCoordinates))
                .withFillOpacity(CIRCLE_OPACITY)
                .withFillColor(bikeSymbolColor.colorCircleString)
        )
        lineManager = LineManager(mapView, mapboxMap, style)
        bikeCircleLine = lineManager.create(LineOptions()
                .withLatLngs(circleCoordinates)
                .withLineColor(bikeSymbolColor.colorCircleString)
                .withLineWidth(LINE_WIDTH)
                //.withLineGapWidth(LINE_GAP_WIDTH)  // Gap Pattern not supported yet??!
        )
    }

    fun updateColor(bikeSymbolColor: BikeSymbolColor) {
        bikeSymbolColor.colorCircleString.let {
            bikeCircleFill.fillColor = it
            bikeCircleLine.lineColor = it
        }

        updateManagers()
    }

    fun updateCircle(currentPosition: Point, meterRadius: Double, setCamera: () -> Unit) {
        val circleCoordinates = calculateCircleCoordinates(currentPosition, meterRadius)
        bikeCircleFill.latLngs = listOf(circleCoordinates)
        bikeCircleLine.latLngs = circleCoordinates

        updateManagers()
        setCamera()
    }

    private fun updateManagers() {
        update(bikeCircleFill)
        lineManager.update(bikeCircleLine)
    }

    fun getLatLng(): List<LatLng> {
        return bikeCircleFill.latLngs.first()
    }

    companion object {
        private const val CIRCLE_INT_POINTS = 200
        private const val CIRCLE_OPACITY = 0.5f
        // private const val LINE_GAP_WIDTH = .8f
        private const val LINE_WIDTH = 1.5f
    }

}