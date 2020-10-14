package de.bikebean.app.ui.main.map;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bikebean.app.R;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.main.status.menu.log.LogViewModel;

public class MapFragmentHelper {

    public interface ButtonsVisibleSetter {
        void setButtonsVisible();
    }

    private final @NonNull GoogleMap googleMap;
    private final LogViewModel logViewModel;
    private final @NonNull ButtonsVisibleSetter buttonsVisibleSetter;
    private final @NonNull MapElements mapElements;

    public MapFragmentHelper(final @NonNull GoogleMap googleMap,
                             final @NonNull MapFragment mapFragment) {
        this.googleMap = googleMap;
        this.logViewModel = mapFragment.logViewModel;
        this.buttonsVisibleSetter = mapFragment::setButtonsVisible;
        this.mapElements = new MapElements(googleMap, mapFragment, this);

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);  /* disable map toolbar */

        if (mapFragment.args != null)  /* otherwise the camera is moved by the observers */
            googleMap.setOnMapLoadedCallback(this::setCamera);
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

    public void setCamera() {
        try {
            googleMap.animateCamera(mapElements.getCameraUpdate());
            buttonsVisibleSetter.setButtonsVisible();
        } catch (IllegalStateException e) {
            final @Nullable String errMsg = e.getLocalizedMessage();
            if (errMsg != null)
                logViewModel.e(errMsg);
            else logViewModel.e("Map is not ready yet!");
        }
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

    public boolean setMapType(final @NonNull MenuItem type) {
        final @Nullable Integer mapType = mapTypeMap.get(type.getItemId());

        if (mapType == null)
            return false;

        googleMap.setMapType(mapType);
        mapElements.setColor(type);
        return true;
    }

    public @NonNull Uri getUri() {
        return mapElements.getUri();
    }
}
