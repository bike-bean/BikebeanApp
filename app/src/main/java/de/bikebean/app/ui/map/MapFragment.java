package de.bikebean.app.ui.map;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import de.bikebean.app.MainActivity;
import de.bikebean.app.R;
import de.bikebean.app.Utils;
import de.bikebean.app.ui.status.StateViewModel;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private StateViewModel stateViewModel;
    private static final int REQUEST_PERMISSION_KEY = 1;

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private FloatingActionButton fab;

    // Map elements
    private Marker marker;
    private Circle circle;
    private MutableLatLng currentPositionBike;
    private Snippet snippet;

    private String bikeName;
    private boolean initializationDone;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = v.findViewById(R.id.mapview);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this); // this is important

        fab = v.findViewById(R.id.fab);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                Objects.requireNonNull(getActivity())
        );
        bikeName = sharedPreferences.getString("name", "bike");

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        stateViewModel = new ViewModelProvider(this).get(StateViewModel.class);

        // hide the toolbar for this fragment
        AppCompatActivity act = (AppCompatActivity) getActivity();
        ActionBar actionbar = Objects.requireNonNull(act).getSupportActionBar();
        Objects.requireNonNull(actionbar).hide();
    }

    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        String[] permissions = {
                android.Manifest.permission.ACCESS_FINE_LOCATION
        };

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false); // disable map toolbar
        // googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (Utils.hasNoPermissions(getActivity(), permissions)) {
            Toast.makeText(getActivity(),
                    "Berechtigung für Standort vergeben",
                    Toast.LENGTH_LONG
            ).show();
            ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_PERMISSION_KEY);
        } else
            googleMap.setMyLocationEnabled(true);

        currentPositionBike = new MutableLatLng();
        snippet = new Snippet();

        // Set a marker
        marker = googleMap.addMarker(new MarkerOptions()
                .position(currentPositionBike.get())
                .title(bikeName)
                .snippet(snippet.toString())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
        );

        // Set a circle
        circle = googleMap.addCircle(new CircleOptions()
                .center(currentPositionBike.get())
                .radius(0.0)
                .strokeWidth(10)
                .strokeColor(Color.BLACK)
        );

        fab.setOnClickListener(v -> {
            String uriString = "geo:0,0?q=" + currentPositionBike.getLat() +
                    "," + currentPositionBike.getLng() + "( " + bikeName + ")";
            Log.d(MainActivity.TAG, uriString);
            Uri gmmIntentUri = Uri.parse(uriString);
            // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            // Make the Intent explicit by setting the Google Maps package
            mapIntent.setPackage("com.google.android.apps.maps");
            // Attempt to start an activity that can handle the Intent
            startActivity(mapIntent);
        });

        initializationDone = false;

        // Set up observer to adjust changes to the view
        stateViewModel.getStatusLocationLat().observe(getViewLifecycleOwner(), statuses -> {
            if (statuses.size() == 0)
                return;
            marker.setPosition(currentPositionBike.setLat(statuses.get(0).getValue()));
            circle.setCenter(currentPositionBike.get());
            setCamera();
        });
        stateViewModel.getStatusLocationLng().observe(getViewLifecycleOwner(), statuses -> {
            if (statuses.size() == 0)
                return;
            marker.setPosition(currentPositionBike.setLng(statuses.get(0).getValue()));
            circle.setCenter(currentPositionBike.get());
            setCamera();
        });
        stateViewModel.getStatusLocationAcc().observe(getViewLifecycleOwner(), statuses -> {
            if (statuses.size() == 0)
                return;
            circle.setRadius(statuses.get(0).getValue());
            setCamera();
        });
        stateViewModel.getStatusNumberCellTowers().observe(getViewLifecycleOwner(), statuses -> {
            if (statuses.size() == 0)
                return;
            marker.setSnippet(snippet.setNumberCellTowers(statuses.get(0).getValue().intValue()));
        });
        stateViewModel.getStatusNumberWifiAccessPoints().observe(getViewLifecycleOwner(), statuses -> {
            if (statuses.size() == 0)
                return;
            marker.setSnippet(snippet.setNumberWifiAccessPoints(statuses.get(0).getValue().intValue()));
        });
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

    private void setCamera() {
        if (initializationDone) {
            try {
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(getLatLngBounds(), 0));
            } catch (IllegalStateException e) {
                Log.d(MainActivity.TAG, "premature (skipped)");
            }
        } else {
            try {
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getLatLngBounds(), 0));
                initializationDone = true;
                fab.show();
            } catch (IllegalStateException e) {
                Log.d(MainActivity.TAG, "permature (skipped)");
            }
        }
    }

    private LatLngBounds getLatLngBounds() throws IllegalStateException {
        // convert accuracy from m to geo lat/lng
        double geoLength = circle.getRadius() / (1852.0 * 60.0);

        if (currentPositionBike.getLat() == 0.0 ||
                currentPositionBike.getLng() == 0.0 ||
                geoLength == 0.0)
            throw new IllegalStateException("You must wait until coordinates are set.");

        return new LatLngBounds(
                new LatLng(currentPositionBike.getLat() - (geoLength * 3.0), currentPositionBike.getLng()),
                new LatLng(currentPositionBike.getLat() + (geoLength * 3.0), currentPositionBike.getLng())
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_KEY)
            if (permissions.length == 1 &&
                    permissions[0].equals(android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED)
                mGoogleMap.setMyLocationEnabled(true);
            else
                Toast.makeText(getActivity(),
                        "Eigener Standort nicht verfügbar",
                        Toast.LENGTH_LONG
                ).show();
    }

    class Snippet {
        private int numberCellTowers;
        private int numberWifiAccessPoints;

        Snippet() {
            numberCellTowers = 0;
            numberWifiAccessPoints = 0;
        }

        @NonNull
        public String toString() {
            String descCellTowers = "Anzahl Funkmasten: ";
            String descWifiAccessPoints = ", Anzahl WAPs: ";

            return descCellTowers + numberCellTowers +
                    descWifiAccessPoints + numberWifiAccessPoints;
        }

        String setNumberCellTowers(int a) {
            numberCellTowers = a;
            return toString();
        }

        String setNumberWifiAccessPoints(int b) {
            numberWifiAccessPoints = b;
            return toString();
        }
    }

    class MutableLatLng {

        private LatLng latLng;

        MutableLatLng() {
            latLng = new LatLng(0.0, 0.0);
        }

        double getLat() {
            return latLng.latitude;
        }

        double getLng() {
            return latLng.longitude;
        }

        LatLng setLat(double lat) {
            latLng = new LatLng(lat, latLng.longitude);
            return latLng;
        }

        LatLng setLng(double lng) {
            latLng = new LatLng(latLng.latitude, lng);
            return latLng;
        }

        LatLng get() {
            return latLng;
        }
    }
}
