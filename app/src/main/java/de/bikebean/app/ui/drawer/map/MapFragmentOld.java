package de.bikebean.app.ui.drawer.map;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import de.bikebean.app.ui.utils.permissions.PermissionUtils;
import de.bikebean.app.ui.utils.preferences.PreferencesUtils;

public abstract class MapFragmentOld extends Fragment {

    public static final @NonNull String[] mapsPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    MapFragmentHelper mapFragmentHelper;

    public void onMapReady() {
        if (PreferencesUtils.isInitDone(requireContext())
                && PermissionUtils.hasMapsPermissions(this))
             mapFragmentHelper.setLocationEnabled();
    }

    /*
     * TODO: Request this permission ONLY if the user clicks
     *       on a fab (also t0do) to show devices location
     */
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
