package de.bikebean.app.ui.main.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
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
import de.bikebean.app.ui.utils.Utils;

import static de.bikebean.app.db.state.State.KEY.ACC;
import static de.bikebean.app.db.state.State.KEY.LAT;
import static de.bikebean.app.db.state.State.KEY.LNG;
import static de.bikebean.app.db.state.State.KEY.NO_CELL_TOWERS;
import static de.bikebean.app.db.state.State.KEY.NO_WIFI_ACCESS_POINTS;
import static de.bikebean.app.ui.main.status.menu.preferences.PreferencesActivity.MAP_TYPE_PREFERENCE;
import static de.bikebean.app.ui.main.status.menu.preferences.PreferencesActivity.NAME_PREFERENCE;

public class MapElements {

    private final @NonNull MapFragmentHelper mapFragmentHelper;

    /*
     Map elements
     */
    private final @NonNull MutableLatLng currentPositionBike;
    private final @NonNull Snippet snippet;
    private final @NonNull AreaColor areaColor;
    private final @NonNull BikeMarker marker;
    private final @NonNull BikeCircle circle;

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
        areaColor = new AreaColor(mapFragment.mapFragmentViewModel, context, mapFragment.args);
        circle = new BikeCircle(googleMap, currentPositionBike, mapFragment.args, areaColor);
        marker = new BikeMarker(googleMap, currentPositionBike, bikeName, snippet, areaColor);
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

        areaColor.refreshColor(state);
        marker.setIcon(areaColor);
        circle.setColor(areaColor);

        mapFragmentHelper.setCamera();
    }

    public void setRadius(final @NonNull State state) {
        circle.setRadius(state);
        mapFragmentHelper.setCamera();
    }

    public void setColor(final @NonNull MenuItem menuItem) {
        areaColor.refreshColor(menuItem.getItemId());
        circle.setColor(areaColor);
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

    @NonNull CameraUpdate getCameraUpdate(final @NonNull Marker marker) {
        return CameraUpdateFactory.newLatLng(marker.getPosition());
    }

    @NonNull CameraUpdate getCameraUpdate(final @NonNull Circle circle) {
        return CameraUpdateFactory.newLatLng(circle.getCenter());
    }

    public @NonNull Uri getUri() {
        return Uri.parse(String.format(Locale.GERMANY,"geo:0,0?q=%s,%s(%s)",
                currentPositionBike.getLat(),
                currentPositionBike.getLng(),
                bikeName.replace(" ", "+"))
        );
    }

    static class AreaColor {

        private static final int AREA_ALPHA = 50;

        private final @NonNull MapFragmentViewModel mf;
        private final @NonNull Context context;

        private @NonNull @ColorInt Integer mainColor;  /* the color of the marker */
        private @NonNull @ColorInt Integer secondaryColor;  /* the color of the circle */
        private @NonNull Double daysSinceLastState;

        AreaColor(final @NonNull MapFragmentViewModel mf, final @NonNull Context context,
                  final @Nullable Bundle args) {
            this.mf = mf;
            this.context = context;

            daysSinceLastState = Utils.getDaysSinceState(args);

            int menuItemId = PreferenceManager.getDefaultSharedPreferences(context)
                    .getInt(MAP_TYPE_PREFERENCE, 0);

            if (menuItemId == R.id.menu_satellite || menuItemId == R.id.menu_hybrid) {
                mainColor = Utils.getMarkerColor(context, daysSinceLastState);
                secondaryColor = ContextCompat.getColor(context, R.color.white);
            } else if (menuItemId == R.id.menu_normal)
                mainColor = secondaryColor =
                        Utils.getMarkerColor(context, daysSinceLastState);
            else
                mainColor = secondaryColor =
                        ContextCompat.getColor(context, R.color.secondaryColor);
        }

        @NonNull BitmapDescriptor getMarkerIcon() {
            final @Nullable Bitmap bitmap =  mf.getMapMarkerBitmap(mainColor);
            return BitmapDescriptorFactory.fromBitmap(bitmap);
        }

        @ColorInt int getCircleStrokeColor() {
            return secondaryColor;
        }

        @ColorInt int getCircleFillColor() {
            return Color.argb(
                    AREA_ALPHA, Color.red(secondaryColor),
                    Color.green(secondaryColor), Color.blue(secondaryColor)
            );
        }

        void refreshColor(final @NonNull State state) {
            daysSinceLastState = Utils.getDaysSinceState(state);

            refreshColor(PreferenceManager.getDefaultSharedPreferences(context)
                    .getInt(MAP_TYPE_PREFERENCE, 0)
            );
        }

        void refreshColor(int menuItemId) {
            if (menuItemId == R.id.menu_satellite || menuItemId == R.id.menu_hybrid)
                secondaryColor = ContextCompat.getColor(context, R.color.white);
            else if (menuItemId == R.id.menu_normal)
                secondaryColor = Utils.getMarkerColor(context, daysSinceLastState);
            else
                secondaryColor = ContextCompat.getColor(context, R.color.secondaryColor);
        }
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

        BikeCircle(final @NonNull GoogleMap googleMap,
                   final @NonNull MutableLatLng pos,
                   final @Nullable Bundle args,
                   final @NonNull AreaColor areaColor) {
            circle = googleMap.addCircle(new CircleOptions()
                    .center(pos.get())
                    .radius(getRadius(args))
                    .fillColor(areaColor.getCircleFillColor())
                    .clickable(true)
                    .strokeWidth(10)
                    .strokePattern(new ArrayList<PatternItem>() {{
                        add(new Dot());
                        add(new Gap(8));
                    }})
                    .strokeColor(areaColor.getCircleStrokeColor())
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

        void setCenter(final @NonNull MutableLatLng latLng) {
            circle.setCenter(latLng.get());
        }

        void setRadius(final @NonNull State state) {
            circle.setRadius(state.getValue());
        }

        void setColor(final @NonNull AreaColor areaColor) {
            circle.setStrokeColor(areaColor.getCircleStrokeColor());
            circle.setFillColor(areaColor.getCircleFillColor());
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
                          final @NonNull Snippet snippet,
                          final @NonNull AreaColor areaColor) {
            marker = googleMap.addMarker(new MarkerOptions()
                    .position(pos.get())
                    .title(bikeName)
                    .snippet(snippet.toString())
                    .icon(areaColor.getMarkerIcon())
            );
        }

        public void setSnippet(final @NonNull Snippet snippet) {
            marker.setSnippet(snippet.toString());
        }

        public void setPosition(final @NonNull MutableLatLng position) {
            marker.setPosition(position.get());
        }

        public void setIcon(final @NonNull AreaColor areaColor) {
            marker.setIcon(areaColor.getMarkerIcon());
        }
    }
}
