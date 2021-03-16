package de.bikebean.app.ui.drawer.map

import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.Fill
import com.mapbox.mapboxsdk.plugins.annotation.Symbol
import de.bikebean.app.MainActivity
import de.bikebean.app.R
import de.bikebean.app.ui.drawer.preferences.SettingsFragment
import de.bikebean.app.ui.utils.preferences.PreferencesUtils.isInitDone

abstract class MapFragment : Fragment() {

    lateinit var mapFragmentViewModel: MapFragmentViewModel

    private val sharedPreferences get() =
        PreferenceManager.getDefaultSharedPreferences(requireContext())

    private var mMapView: MapView? = null

    private var mapTypeFab: FloatingActionButton? = null
    private var shareFab: FloatingActionButton? = null
    private var bikeFab: FloatingActionButton? = null

    private var popup: PopupMenu? = null

    private var mapboxMap: MapboxMap? = null
    private var style: Style? = null
    private var bikeSymbolManager: BikeSymbolManager? = null
    private var bikeCircleManager: BikeCircleManager? = null

    abstract var currentPosition: Point
    abstract var daysSinceLastState: Double
    abstract var meterRadius: Double


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        Mapbox.getInstance(requireContext(), getString(R.string.mapbox_access_token))
        val v = inflater.inflate(R.layout.fragment_map, container, false)

        bikeFab = v.findViewById(R.id.bikeFab)
        shareFab = v.findViewById(R.id.shareFab)
        mapTypeFab = v.findViewById(R.id.mapTypeFab)

        mMapView = v.findViewById<MapView>(R.id.mapview)?.apply {
            onCreate(savedInstanceState)
            getMapAsync(::onMapReady)
        }

        setHasOptionsMenu(true)

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mapFragmentViewModel = ViewModelProvider(this).get(MapFragmentViewModel::class.java)

        mapTypeFab?.let {
            popup = PopupMenu(requireContext(), it).apply {
                setOnMenuItemClickListener(::setMapStyle)
                inflate(R.menu.map_type_menu)
            }
        }

        PorterDuffColorFilter(
                ContextCompat.getColor(requireContext(), R.color.grey),
                PorterDuff.Mode.SRC_IN
        ).let(::setFabColorFilters)

        mapTypeFab?.setOnClickListener { popup?.show() }
        shareFab?.setOnClickListener { showShare() }
        bikeFab?.setOnClickListener { setButtonsVisible(visible = true, onlyTheSheet = false) }
    }

    private fun setFabColorFilters(colorFilter: ColorFilter) =
            listOf(mapTypeFab, shareFab).forEach { it?.drawable?.colorFilter = colorFilter }

    private fun createBikeSymbolIcon(
            bikeSymbolColor: BikeSymbolColor
    ) : BikeSymbolIcon = BikeSymbolIcon(
            requireContext(),
            bikeSymbolColor,
            mapFragmentViewModel,
            style
    )

    private fun createBikeSymbolColor() : BikeSymbolColor = BikeSymbolColor(
            requireContext(),
            daysSinceLastState,
            style
    )

    private fun onMapReady(map: MapboxMap) {
        mapboxMap = map.apply {
                setStyle(getMapStyleFromPrefs(), ::onStyleLoaded)

                cameraPosition = CameraPosition
                        .Builder()
                        .target(CENTER_GERMANY)
                        .zoom(6.0)
                        .build()
                // Not used yet! See NOTE below for explanation
                // addOnCameraMoveStartedListener(::onCameraMove)
        }

        /*  A NOTE ABOUT OnClickListeners in MAPBox
        *
        *  I noticed onClick-Stuff was NOT working as expected when more than one
        *  onClickListener was registered. First of all,
        *
        *   mapboxMap.addOnMapClickListener()
        *
        *  and
        *
        *   mapboxMap.addOnCameraMoveStartedListener()
        *
        *  don't work together. When both are registered, the OnMapClickListener() is
        *  called first, thus abandoning the addOnCameraMoveStartedListener().
        *  Secondly (also worse), the onClickListeners of the Marker/Symbol as well as
        *  the Circle are not consumed if an OnMapClickListener() is registered!
        *  Removing that made (at least one) of the Marker/Circle-ClickListeners work.
        *
        *  Turns out ALL ClickListeners (MapClick, Marker, Circle, ...) are somehow
        *  added to one big list of ClickListeners, which are then called one after the other.
        *  If someone of the Listeners returns true, indicating the Click was consumed by him,
        *  the other Listeners consequently don't get called.
        *  On the other hand, returning true from any Listener makes a lot of sense, if
        *  that Listener actually consumed the event! (I mean, that's what that
        *  return value is for, anyway...).
        *
        *  Obviously, the MAPBox-internal filtering of WHERE the Click happened on the map,
        *  routing the click towards the right candidate ClickListener(s) seems to be either
        *  buggy or not implemented at all.
        *
        *  This leads to a situation where the order of definition of the ClickListeners() matters.
        *  Apparently, the first ClickListener added to the list is also "served" first (FIFO).
        *
        *  It seems, though, the MAPBox team is working on at least a mitigation of this;
        *  Very much code as well as PRs related to the ClickListener-Stuff and Annotations
        *  is changing recently (~03/21). Also, apparently, the code previously shipped as
        *  "Annotation plugin" is coming to the main SDK, which may make some things better.
        *
        *  Bottom Line, for the time being I will work around this issue by DELIBERATELY
        *  adding the OnMapClickListener() lastly.
        *
        * */
    }

    private fun onStyleLoaded(style: Style) {
        this.style = style

        val bikeSymbolColor = createBikeSymbolColor()
        val bikeSymbolIcon = createBikeSymbolIcon(bikeSymbolColor)

        mapboxMap?.apply {
            mMapView?.let { mapView ->
                bikeCircleManager = BikeCircleManager(
                        mapView,
                        this,
                        style,
                        ::onCircleClick,
                        bikeSymbolColor,
                        currentPosition,
                        meterRadius
                ).apply {
                    val cameraUpdate = getCameraUpdate(LatLngBounds.Builder()
                            .includes(getLatLng())
                            .build()
                    )
                    if (firstTimeClicked) {
                        animateCamera(cameraUpdate)
                        firstTimeClicked = false
                    } else moveCamera(cameraUpdate)
                }
                bikeSymbolManager = BikeSymbolManager(
                        mapView,
                        this,
                        style,
                        ::onSymbolClick,
                        bikeSymbolIcon,
                        currentPosition)
            }

            // OnMapClickListener should be added lastly! See NOTE above for explanation
            addOnMapClickListener { onMapClick() }
        }
    }

    private fun onStyleUpdated(style: Style) {
        this.style = style
        val bikeSymbolColor = createBikeSymbolColor()
        val bikeSymbolIcon = createBikeSymbolIcon(bikeSymbolColor)

        bikeCircleManager?.updateColor(bikeSymbolColor)
        bikeSymbolManager?.updateIcon(bikeSymbolIcon)
    }

    private fun setButtonsVisible(visible: Boolean, onlyTheSheet: Boolean) {
        with(requireActivity() as MainActivity) {
            when {
                visible -> setBottomSheetBehaviorState(BottomSheetBehavior.STATE_COLLAPSED)
                else -> resumeToolbarAndBottomSheet()
            }
        }

        if (!onlyTheSheet) setShareButtonVisible(visible)
    }

    fun setShareButtonVisible(visible: Boolean) = when {
        visible -> View.VISIBLE
        else -> View.GONE
    }.let { shareFab?.visibility = it }

    private fun showShare() {
        mapFragmentViewModel.startShareIntent(this)
    }

    fun updateMarkerPosition() = bikeSymbolManager?.updatePosition(currentPosition, ::setCamera)

    fun updateCircle() = bikeCircleManager?.updateCircle(currentPosition, meterRadius, ::setCamera)

    fun updateColors() {
        val bikeSymbolColor = createBikeSymbolColor()
        val bikeSymbolIcon = createBikeSymbolIcon(bikeSymbolColor)

        bikeSymbolManager?.updateIcon(bikeSymbolIcon)
        bikeCircleManager?.updateColor(bikeSymbolColor)
    }

    private fun setCamera() {}

    /*
    private fun onCameraMove(reason: Int) = when (reason) {
        MapboxMap.OnCameraMoveStartedListener.REASON_API_GESTURE ->
            setButtonsVisible(visible = false, onlyTheSheet = true)
        else -> Unit
    }
    */

    private fun onCircleClick(circle: Fill): Boolean {
        setButtonsVisible(visible = true, onlyTheSheet = false)

        mapboxMap?.animateCamera(getCameraUpdate(LatLngBounds.Builder()
                .includes(circle.latLngs.first())
                .build()
        ))

        return true
    }

    private fun onSymbolClick(symbol: Symbol): Boolean {
        setButtonsVisible(visible = true, onlyTheSheet = false)

        mapboxMap?.animateCamera(getCameraUpdate(symbol.latLng)) ?: return false

        return true
    }

    private fun getCameraUpdate(latLng: LatLng) =
            CameraUpdateFactory.newLatLng(LatLng(
                    latLng.latitude - offset, latLng.longitude
            ))

    private fun getCameraUpdate(latLngBounds: LatLngBounds) =
            CameraUpdateFactory.newLatLngBounds(
                    latLngBounds,
                    100,
                    50,
                    100,
                    700)

    private val offset: Double
    get() = mapboxMap?.projection?.visibleRegion?.latLngBounds?.let {
        (it.northEast.latitude - it.southWest.latitude) / 6
    } ?: 0.0

    private fun onMapClick(): Boolean {
        setButtonsVisible(visible = false, onlyTheSheet = false)
        return true
    }

    private fun setMapStyle(menuItem: MenuItem): Boolean {
        mapTypeMap[menuItem.itemId]?.let {
            sharedPreferences.edit()
                    .putInt(SettingsFragment.MAP_TYPE_PREFERENCE, menuItem.itemId)
                    .apply()

            when (it) {
                MAP_STYLE_NORMAL -> darkOrLightStyle
                else -> it
            }.let { mapStyle ->
                mapboxMap?.setStyle(mapStyle, ::onStyleUpdated)
            }
            return true
        } ?: return false
    }

    private fun getMapStyleFromPrefs(): String =
            sharedPreferences.getInt(
                    SettingsFragment.MAP_TYPE_PREFERENCE,
                    R.id.menu_normal
            ).let { pref ->
                mapTypeMap[pref].let {
                    when (it) {
                        null, MAP_STYLE_NORMAL -> darkOrLightStyle
                        else -> it
                    }
                }
            }

    private val darkOrLightStyle: String get() = when {
        (requireActivity() as MainActivity).isLightTheme -> MAP_STYLE_NORMAL
        else -> MAP_STYLE_NIGHT
    }

    override fun onStart() {
        super.onStart()
        mMapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mMapView?.onResume()

        val activity = requireActivity() as MainActivity

        if (isInitDone(requireContext()))
            bikeFab?.visibility = View.VISIBLE
        else bikeFab?.visibility = View.GONE

        activity.resumeToolbarAndBottomSheet()
        activity.setToolbarScrollEnabled(false)
    }

    override fun onPause() {
        super.onPause()
        mMapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mMapView?.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mMapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mMapView?.onSaveInstanceState(outState)
    }

    companion object {
        var firstTimeClicked = true

        const val MAP_STYLE_NORMAL = Style.OUTDOORS
        const val MAP_STYLE_NIGHT = Style.DARK
        const val MAP_STYLE_SATELLITE = Style.SATELLITE
        const val MAP_STYLE_HYBRID = Style.SATELLITE_STREETS

        val CENTER_GERMANY = LatLng(51.1, 10.4)

        private val mapTypeMap = mapOf(
                R.id.menu_normal to MAP_STYLE_NORMAL,
                R.id.menu_satellite to MAP_STYLE_SATELLITE,
                R.id.menu_hybrid to MAP_STYLE_HYBRID
        )
    }

}
