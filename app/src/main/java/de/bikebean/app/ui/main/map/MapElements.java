package de.bikebean.app.ui.main.map;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.bikebean.app.R;
import de.bikebean.app.db.state.State;

import static de.bikebean.app.db.state.State.KEY.ACC;
import static de.bikebean.app.db.state.State.KEY.LAT;
import static de.bikebean.app.db.state.State.KEY.LNG;
import static de.bikebean.app.db.state.State.KEY.NO_CELL_TOWERS;
import static de.bikebean.app.db.state.State.KEY.NO_WIFI_ACCESS_POINTS;
import static de.bikebean.app.ui.main.status.menu.preferences.PreferencesActivity.NAME_PREFERENCE;

public class MapElements {

    private final @NonNull MapFragmentHelper mapFragmentHelper;

    /*
     Map elements
     */
    private final @NonNull BikeMarker marker;
    private final @NonNull BikeCircle circle;
    private final @NonNull MutableLatLng currentPositionBike;
    private final @NonNull Snippet snippet;

    private final @NonNull String bikeName;

    public MapElements(final @NonNull GoogleMap googleMap,
                       final @NonNull MapFragment mapFragment,
                       final @NonNull MapFragmentHelper mapFragmentHelper) {
        this.mapFragmentHelper = mapFragmentHelper;

        final @NonNull Context context = mapFragment.requireContext();
        bikeName = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(NAME_PREFERENCE, "bike");

        currentPositionBike = new MutableLatLng(mapFragment.args);
        snippet = new Snippet(mapFragment.args);
        circle = new BikeCircle(googleMap, currentPositionBike, mapFragment.args, context);
        marker = new BikeMarker(googleMap, currentPositionBike, bikeName, snippet);
    }

    final @NonNull Map<State.KEY, MapFragmentHelper.MapElementsSetter> setterMap =
            new HashMap<State.KEY, MapFragmentHelper.MapElementsSetter>() {{
                put(NO_CELL_TOWERS, MapElements.this::setNoCellTowers);
                put(NO_WIFI_ACCESS_POINTS, MapElements.this::setNoWifiAccessPoints);
                put(LAT, MapElements.this::setPosition);
                put(LNG, MapElements.this::setPosition);
                put(ACC, MapElements.this::setRadius);
            }};

    public void setNoCellTowers(final @NonNull State state) {
        snippet.setNumberCellTowers(state);
        marker.setSnippet(snippet);
    }

    public void setNoWifiAccessPoints(final @NonNull State state) {
        snippet.setNumberWifiAccessPoints(state);
        marker.setSnippet(snippet);
    }

    public void setPosition(final @NonNull State state) {
        currentPositionBike.set(state);
        marker.setPosition(currentPositionBike);
        circle.setCenter(currentPositionBike);
        mapFragmentHelper.setCamera();
    }

    public void setRadius(final @NonNull State state) {
        circle.setRadius(state);
        mapFragmentHelper.setCamera();
    }

    public void setColor(final @NonNull MenuItem menuItem) {
        circle.setColor(menuItem);
    }

    @NonNull CameraUpdate getCameraUpdate() throws IllegalStateException {
        /*
         convert accuracy from m to geo lat/lng
         */
        double geoLength = circle.getRadius() / (1852.0 * 60.0);

        if (currentPositionBike.getLat() == 0.0 || currentPositionBike.getLng() == 0.0 ||
                geoLength == 0.0)
            throw new IllegalStateException("You must wait until coordinates are set.");

        final @NonNull LatLng northeast = new LatLng(
                currentPositionBike.getLat() + (geoLength * 3.0),
                currentPositionBike.getLng()
        );
        final @NonNull LatLng southwest = new LatLng(
                currentPositionBike.getLat() - (geoLength * 3.0),
                currentPositionBike.getLng()
        );

        return CameraUpdateFactory.newLatLngBounds(new LatLngBounds(southwest, northeast),0);
    }

    public @NonNull Uri getUri() {
        return Uri.parse(String.format(Locale.GERMANY,"geo:0,0?q=%s,%s(%s)",
                currentPositionBike.getLat(),
                currentPositionBike.getLng(),
                bikeName.replace(" ", "+"))
        );
    }

    static class MutableLatLng {

        private @NonNull LatLng latLng;

        MutableLatLng(final @Nullable Bundle args) {
            if (args != null)
                latLng = new LatLng(
                        args.getDouble(LAT.get()),
                        args.getDouble(LNG.get())
                );
            else latLng = new LatLng(0.0, 0.0);
        }

        double getLat() {
            return latLng.latitude;
        }

        double getLng() {
            return latLng.longitude;
        }

        void set(final @NonNull State state) {
            switch (State.KEY.getValue(state)) {
                case LAT:
                    latLng = new LatLng(state.getValue(), latLng.longitude);
                    break;
                case LNG:
                    latLng = new LatLng(latLng.latitude, state.getValue());
                    break;
            }
        }

        LatLng get() {
            return latLng;
        }
    }

    static class Snippet {
        private int numberCellTowers;
        private int numberWifiAccessPoints;

        Snippet(final @Nullable Bundle args) {
            if (args != null) {
                numberCellTowers = args.getInt(State.KEY.NO_CELL_TOWERS.get());
                numberWifiAccessPoints = args.getInt(State.KEY.NO_WIFI_ACCESS_POINTS.get());
            } else {
                numberCellTowers = 0;
                numberWifiAccessPoints = 0;
            }
        }

        public @NonNull String toString() {
            final @NonNull String descCellTowers = "Anzahl Funkmasten: ";
            final @NonNull String descWifiAccessPoints = ", Anzahl WAPs: ";

            return descCellTowers + numberCellTowers +
                    descWifiAccessPoints + numberWifiAccessPoints;
        }

        void setNumberCellTowers(final @NonNull State state) {
            numberCellTowers = state.getValue().intValue();
        }

        void setNumberWifiAccessPoints(final @NonNull State state) {
            numberWifiAccessPoints = state.getValue().intValue();
        }
    }

    static class BikeCircle {
        /*
         A wrapper class for gms circle
         */

        private final @NonNull Circle circle;
        private final @NonNull Context context;

        private static final @NonNull Map<Integer, Integer> colorMap =
                new HashMap<Integer, Integer>() {{
                    put(R.id.menu_normal, R.color.grey);
                    put(R.id.menu_satellite, R.color.white);
                    put(R.id.menu_hybrid, R.color.white);
                }};

        public BikeCircle(final @NonNull GoogleMap googleMap,
                          final @NonNull MutableLatLng pos,
                          final @Nullable Bundle args,
                          final @NonNull Context context) {
            this.context = context;
            circle = googleMap.addCircle(new CircleOptions()
                    .center(pos.get())
                    .radius(getRadius(args))
                    .strokeWidth(10)
                    .strokePattern(new ArrayList<PatternItem>() {{
                        add(new Dot());
                    }})
                    .strokeColor(ContextCompat.getColor(context, R.color.grey))
            );
        }

        private double getRadius(final @Nullable Bundle args) {
            if (args != null)
                return args.getDouble(ACC.get());
            else return 0.0;
        }

        double getRadius() {
            return circle.getRadius();
        }

        public void setCenter(final @NonNull MutableLatLng latLng) {
            circle.setCenter(latLng.get());
        }

        public void setRadius(final @NonNull State state) {
            circle.setRadius(state.getValue());
        }

        public void setColor(final @NonNull MenuItem menuItem) {
            final @Nullable Integer color = colorMap.get(menuItem.getItemId());

            if (color != null)
                circle.setStrokeColor(ContextCompat.getColor(context, color));
        }
    }

    static class BikeMarker {
        /*
         A wrapper class for gms marker
         */

        private final @NonNull Marker marker;

        public BikeMarker(final @NonNull GoogleMap googleMap,
                          final @NonNull MutableLatLng pos,
                          final @NonNull String bikeName,
                          final @NonNull Snippet snippet) {
            marker = googleMap.addMarker(new MarkerOptions()
                    .position(pos.get())
                    .title(bikeName)
                    .snippet(snippet.toString())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_name))
            );
        }

        public void setSnippet(final @NonNull Snippet snippet) {
            marker.setSnippet(snippet.toString());
        }

        public void setPosition(final @NonNull MutableLatLng position) {
            marker.setPosition(position.get());
        }
    }
}
