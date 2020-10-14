package de.bikebean.app.ui.main.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import de.bikebean.app.R;
import de.bikebean.app.ui.main.status.menu.log.LogViewModel;
import de.bikebean.app.ui.utils.PermissionsRationaleDialog;
import de.bikebean.app.ui.utils.Utils;

public abstract class MapFragment extends Fragment {

    public static final @NonNull String[] mapsPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    AppCompatActivity act;
    @Nullable Bundle args;

    LogViewModel logViewModel;
    MapFragmentViewModel mapFragmentViewModel;

    MapFragmentHelper mapFragmentHelper;

    private MapView mMapView;
    private FloatingActionButton launchGmNavigationFab, mapTypeFab, shareFab;

    @Override
    public @NonNull View onCreateView(final @NonNull LayoutInflater inflater,
                                      final @Nullable ViewGroup container,
                                      final @Nullable Bundle savedInstanceState) {
        final @NonNull View v = inflater.inflate(R.layout.fragment_map, container, false);

        args = getArguments();

        mMapView = v.findViewById(R.id.mapview);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this::onMapReady);

        launchGmNavigationFab = v.findViewById(R.id.fab);
        mapTypeFab = v.findViewById(R.id.fab2);
        shareFab = v.findViewById(R.id.fab3);

        return v;
    }

    @Override
    public void onActivityCreated(final @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mapFragmentViewModel = new ViewModelProvider(this).get(MapFragmentViewModel.class);
        logViewModel = new ViewModelProvider(this).get(LogViewModel.class);

        act = (AppCompatActivity) requireActivity();

        /*
         Hide the toolbar for this fragment
         */
        final @Nullable ActionBar actionbar = act.getSupportActionBar();
        if (actionbar != null)
            actionbar.hide();

        /*
         Init the buttons
         */
        int color = ContextCompat.getColor(requireContext(), R.color.grey);
        mapTypeFab.getDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        shareFab.getDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        launchGmNavigationFab.setOnClickListener(this::startNavigation);
        mapTypeFab.setOnClickListener(this::showPopup);
        shareFab.setOnClickListener(this::showShare);
    }

    public void onMapReady(final @NonNull GoogleMap googleMap) {
        mapFragmentHelper = new MapFragmentHelper(googleMap, this);

        if (Utils.getPermissions(act, Utils.PERMISSION_KEY.MAPS, () ->
                new PermissionsRationaleDialog(act, Utils.PERMISSION_KEY.MAPS).show(
                        act.getSupportFragmentManager(),
                        "mapsRationaleDialog"
                )
        ))
            mapFragmentHelper.setLocationEnabled();
    }

    void setButtonsVisible() {
        launchGmNavigationFab.show();
        shareFab.show();
    }

    void startNavigation(final @NonNull View v) {
        final @NonNull Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapFragmentHelper.getUri());
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(act.getPackageManager()) != null)
            startActivity(mapIntent);
    }

    void showPopup(final @NonNull View v) {
        final @NonNull PopupMenu popup = new PopupMenu(requireContext(), v);

        popup.setOnMenuItemClickListener(mapFragmentHelper::setMapType);
        popup.inflate(R.menu.map_type_menu);
        popup.show();
    }

    void showShare(final @NonNull View v) {
        mapFragmentViewModel.newShareIntent(this);
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode == Utils.PERMISSION_KEY.MAPS.ordinal()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                mapFragmentHelper.setLocationEnabled();
            else
                Snackbar.make(requireView(),
                        "Eigener Standort nicht verfügbar",
                        Snackbar.LENGTH_LONG
                ).show();
        }
    }
}
