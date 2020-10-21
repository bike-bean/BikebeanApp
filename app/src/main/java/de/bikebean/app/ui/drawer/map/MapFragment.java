package de.bikebean.app.ui.drawer.map;

import android.Manifest;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import de.bikebean.app.MainActivity;
import de.bikebean.app.R;
import de.bikebean.app.ui.drawer.log.LogViewModel;
import de.bikebean.app.ui.utils.permissions.PermissionUtils;
import de.bikebean.app.ui.utils.preferences.PreferencesUtils;

public abstract class MapFragment extends Fragment {

    public static final @NonNull String[] mapsPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    static boolean firstTimeClicked = true;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    AppCompatActivity act;
    @Nullable Bundle args;

    LogViewModel logViewModel;
    MapFragmentViewModel mapFragmentViewModel;

    MapFragmentHelper mapFragmentHelper;

    private MapView mMapView;
    private FloatingActionButton mapTypeFab, shareFab;

    @Override
    public @NonNull View onCreateView(final @NonNull LayoutInflater inflater,
                                      final @Nullable ViewGroup container,
                                      final @Nullable Bundle savedInstanceState) {
        final @NonNull View v = inflater.inflate(R.layout.fragment_map, container, false);

        args = getArguments();

        @Nullable Bundle mapViewBundle = null;
        if (savedInstanceState != null)
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);

        /*
         Crude part START

         This code basically does the same as
         > mMapView = v.findViewById(R.id.mapview);

         except that code can't pass the GoogleMapOptions to the map,
         which is why I use the constructor for MapView myself
         and integrate it into the ConstraintLayout manually.

         Thanks to strangeluck: https://stackoverflow.com/a/16193642

         Very crude, but it works.
         */
        final @NonNull ConstraintLayout constraintLayout = v.findViewById(R.id.map_constraint_layout);
        final @NonNull ViewGroup.LayoutParams mapParams = v.findViewById(R.id.mapview).getLayoutParams();

        MapFragmentHelper.googleMapOptions.mapType(MapFragmentHelper.getMapType(requireContext()));
        mMapView = new MapView(requireContext(), MapFragmentHelper.googleMapOptions);
        constraintLayout.addView(mMapView, mapParams);
        /* crude part END */

        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this::onMapReady);

        mapTypeFab = v.findViewById(R.id.fab2);
        shareFab = v.findViewById(R.id.fab3);

        setHasOptionsMenu(true);

        return v;
    }

    @Override
    public void onActivityCreated(final @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mapFragmentViewModel = new ViewModelProvider(this).get(MapFragmentViewModel.class);
        logViewModel = new ViewModelProvider(this).get(LogViewModel.class);

        act = (AppCompatActivity) requireActivity();

        /*
         Init the buttons
         */
        final @NonNull ColorFilter colorFilter = new PorterDuffColorFilter(
                ContextCompat.getColor(requireContext(), R.color.grey),
                PorterDuff.Mode.SRC_IN
        );
        mapTypeFab.getDrawable().setColorFilter(colorFilter);
        shareFab.getDrawable().setColorFilter(colorFilter);

        mapTypeFab.setOnClickListener(this::showPopup);
        shareFab.setOnClickListener(this::showShare);
    }

    @Override
    public void onSaveInstanceState(final @NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        @Nullable Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    public void onMapReady(final @NonNull GoogleMap googleMap) {
        mapFragmentHelper = new MapFragmentHelper(googleMap, this);

        if (PreferencesUtils.isInitDone(requireContext())
                && PermissionUtils.hasMapsPermissions(act))
            mapFragmentHelper.setLocationEnabled();
    }

    void setButtonsVisible(boolean visible, boolean onlyTheSheet) {
        final @NonNull MainActivity activity = (MainActivity) act;

        if (visible) {
            if (!onlyTheSheet)
                shareFab.show();
            activity.setBottomSheetBehaviorState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            if (!onlyTheSheet)
                shareFab.hide();
            activity.resumeToolbarAndBottomSheet();
        }
    }

    void showPopup(final @NonNull View v) {
        final @NonNull PopupMenu popup = new PopupMenu(requireContext(), v);

        popup.setOnMenuItemClickListener(mapFragmentHelper::setMapType);
        popup.inflate(R.menu.map_type_menu);
        popup.show();
    }

    void showShare(final @NonNull View v) {
        mapFragmentViewModel.startShareIntent(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();

        final @NonNull MainActivity activity = (MainActivity) requireActivity();

        mapFragmentViewModel.hasPosition(getViewLifecycleOwner(), hasPosition -> {
            if (hasPosition) {
                activity.setBottomSheetBehaviorState(BottomSheetBehavior.STATE_HIDDEN);
                activity.setBottomSheetHideable(true);
                setMapLite(MapFragmentHelper.MAP_STYLE_NORMAL, false);
            } else {
                activity.setBottomSheetBehaviorState(BottomSheetBehavior.STATE_EXPANDED);
                activity.setBottomSheetHideable(false);
                setMapLite(MapFragmentHelper.MAP_STYLE_SILVER, true);
            }
        });

        activity.setToolbarVisible();
        activity.setToolbarScrollEnabled(true);
    }

    private void setMapLite(int mapStyleNormal, boolean lite) {
        if (mapFragmentHelper != null) {
            mapFragmentHelper.setMapStyle(
                    requireContext(),
                    mapStyleNormal
            );
            mapFragmentHelper.setMapLite(lite);
        }

        if (lite)
            mapTypeFab.setVisibility(View.GONE);
        else
            mapTypeFab.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mMapView != null)
            mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            final @NonNull String[] permissions,
            final @NonNull int[] grantResults) {
        if (requestCode == PermissionUtils.KEYS.MAPS.ordinal()) {
            if (PermissionUtils.checkResult(grantResults))
                mapFragmentHelper.setLocationEnabled();
            else
                Snackbar.make(requireView(),
                        "Eigener Standort nicht verfügbar",
                        Snackbar.LENGTH_LONG
                ).show();
        }
    }
}
