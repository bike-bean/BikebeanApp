package de.bikebean.app.ui.main.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bikebean.app.R;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.main.status.menu.log.LogViewModel;

import static de.bikebean.app.ui.main.status.menu.preferences.PreferencesActivity.MAP_TYPE_PREFERENCE;

public class MapFragmentHelper {

    public interface ButtonsVisibleSetter {
        void setButtonsVisible(boolean visible);
    }

    private static final @NonNull LatLng CENTER_GERMANY = new LatLng(51.1, 10.4);

    static final @NonNull GoogleMapOptions googleMapOptions = new GoogleMapOptions()
            .mapToolbarEnabled(false)  /* disable map toolbar */
            .zoomControlsEnabled(true)
            .camera(CameraPosition.builder()
                    .target(CENTER_GERMANY)
                    .zoom(6)
                    .build()
            );

    private final @NonNull GoogleMap googleMap;
    private final LogViewModel logViewModel;
    private final @NonNull ButtonsVisibleSetter buttonsVisibleSetter;
    private final @NonNull SharedPreferences sharedPreferences;
    private final @NonNull MapElements mapElements;

    public MapFragmentHelper(final @NonNull GoogleMap googleMap,
                             final @NonNull MapFragment mapFragment) {
        this.googleMap = googleMap;
        this.logViewModel = mapFragment.logViewModel;
        this.buttonsVisibleSetter = mapFragment::setButtonsVisible;
        this.sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(mapFragment.requireContext());
        this.mapElements = new MapElements(googleMap, mapFragment, this);

        googleMap.setOnMarkerClickListener(this::onMarkerClick);
        googleMap.setOnCircleClickListener(this::onCircleClick);
        googleMap.setOnMapClickListener(this::onMapClick);
        googleMap.setOnMapLoadedCallback(this::onMapLoaded);
    }

    public interface MapElementsSetter {
        void setMapElements(final @NonNull State state);
    }

    public void setMapElements(@NonNull List<State> statuses) {
        if (statuses.size() == 0)
            return;

        final @NonNull State state = statuses.get(0);
        final @Nullable MapElementsSetter mapElementsSetter =
                mapElements.setterMap.get(State.KEY.getValue(state));

        if (mapElementsSetter == null)
            return;

        mapElementsSetter.setMapElements(state);
    }

    public void onMapLoaded() {
        setCamera();
    }

    public void setCamera() {
        try {
            if (MapFragment.firstTimeClicked) {
                googleMap.animateCamera(mapElements.getCameraUpdate());
                MapFragment.firstTimeClicked = false;
            } else
                googleMap.moveCamera(mapElements.getCameraUpdate());
        } catch (IllegalStateException e) {
            final @Nullable String errMsg = e.getLocalizedMessage();
            if (errMsg != null)
                logViewModel.e(errMsg);
            else logViewModel.e("Map is not ready yet!");
        }
    }

    public boolean onMarkerClick(final @NonNull Marker marker) {
        logViewModel.d("Clicked a Marker!");
        googleMap.animateCamera(mapElements.getCameraUpdate(marker));
        buttonsVisibleSetter.setButtonsVisible(true);
        return true;
    }

    public void onCircleClick(final @NonNull Circle circle) {
        /* funktioniert noch nicht */
        logViewModel.d("Clicked a Circle!");
        googleMap.animateCamera(mapElements.getCameraUpdate(circle));
        buttonsVisibleSetter.setButtonsVisible(true);
    }

    public void onMapClick(final @NonNull LatLng point) {
        buttonsVisibleSetter.setButtonsVisible(false);
    }

    @SuppressLint("MissingPermission")
    public void setLocationEnabled() {
        googleMap.setMyLocationEnabled(true);
    }

    private static final @NonNull Map<Integer, Integer> mapTypeMap =
            new HashMap<Integer, Integer>() {{
        put(R.id.menu_normal, GoogleMap.MAP_TYPE_NORMAL);
        put(R.id.menu_satellite, GoogleMap.MAP_TYPE_SATELLITE);
        put(R.id.menu_hybrid, GoogleMap.MAP_TYPE_HYBRID);
    }};

    public static int getMapType(final @NonNull Context context) {
        int mapTypeMenu = PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(MAP_TYPE_PREFERENCE, R.id.menu_normal);
        final @Nullable Integer mapType = mapTypeMap.get(mapTypeMenu);

        if (mapType == null)
            return GoogleMap.MAP_TYPE_NORMAL;

        return mapType;
    }

    public boolean setMapType(final @NonNull MenuItem menuItem) {
        final @Nullable Integer mapType = mapTypeMap.get(menuItem.getItemId());

        if (mapType == null)
            return false;

        sharedPreferences.edit()
                .putInt(MAP_TYPE_PREFERENCE, menuItem.getItemId())
                .apply();
        googleMap.setMapType(mapType);
        mapElements.setColor(menuItem);
        return true;
    }

    public @NonNull Uri getUri() {
        return mapElements.getUri();
    }
}
