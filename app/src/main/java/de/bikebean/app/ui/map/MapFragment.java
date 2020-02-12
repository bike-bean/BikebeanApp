package de.bikebean.app.ui.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
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

import java.util.List;
import java.util.Objects;

import de.bikebean.app.MainActivity;
import de.bikebean.app.R;
import de.bikebean.app.Utils;
import de.bikebean.app.db.state.State;
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

    private Bundle args;
    private boolean showCurrentPosition;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        args = getArguments();
        showCurrentPosition = (args == null || args.isEmpty());

        mMapView = v.findViewById(R.id.mapview);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this); // this is important

        fab = v.findViewById(R.id.fab);

        bikeName = PreferenceManager.getDefaultSharedPreferences(
                Objects.requireNonNull(getActivity())
        ).getString("name", "bike");

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

        act.getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!showCurrentPosition)
                    Navigation.findNavController(Objects.requireNonNull(getView()))
                            .navigate(R.id.history_action);
                else
                    Navigation.findNavController(Objects.requireNonNull(getView()))
                            .navigate(R.id.back_action);
            }
        });
    }

    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION
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

        double radius = 0.0;

        if (showCurrentPosition) {
            currentPositionBike = new MutableLatLng();
            snippet = new Snippet();
        } else {
            currentPositionBike = new MutableLatLng(
                    args.getDouble("lat"),
                    args.getDouble("lng")
            );
            snippet = new Snippet(
                    args.getInt("noCellTowers"),
                    args.getInt("noWifiAccessPoints")
            );
            radius = args.getDouble("acc");
        }

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
                .radius(radius)
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

        if (showCurrentPosition)
            setupObservers();
        else
            setCamera(false);
    }

    private void setupObservers() {
        // Set up observer to adjust changes to the view
        LifecycleOwner l = getViewLifecycleOwner();

        initializationDone = false;

        stateViewModel.getConfirmedLocationLat().observe(l, this::setMapElements);
        stateViewModel.getConfirmedLocationLng().observe(l, this::setMapElements);
        stateViewModel.getConfirmedLocationAcc().observe(l, this::setMapElements);
        stateViewModel.getStatusNumberCellTowers().observe(l, this::setMapElements);
        stateViewModel.getStatusNumberWifiAccessPoints().observe(l, this::setMapElements);
    }

    private void setMapElements(List<State> statuses) {
        if (statuses.size() == 0)
            return;

        switch (statuses.get(0).getKey()) {
            case State.KEY_NO_CELL_TOWERS:
                marker.setSnippet(snippet.setNumberCellTowers(statuses.get(0).getValue().intValue()));
                break;
            case State.KEY_NO_WIFI_ACCESS_POINTS:
                marker.setSnippet(snippet.setNumberWifiAccessPoints(statuses.get(0).getValue().intValue()));
                break;
            case State.KEY_LAT: // And:
            case State.KEY_LNG:
                marker.setPosition(currentPositionBike.set(statuses.get(0)));
                circle.setCenter(currentPositionBike.get());
                setCamera(true);
                break;
            case State.KEY_ACC:
                circle.setRadius(statuses.get(0).getValue());
                setCamera(true);
                break;
        }
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

    private void setCamera(boolean showCurrentPosition) {
        if (showCurrentPosition) {
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
        } else
            try {
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(getLatLngBounds(), 0));
            } catch (IllegalStateException e) {
                Log.w(MainActivity.TAG, "Map is not ready yet!");
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

        Snippet(int noCellTowers, int noWifiAccessPoints) {
            numberCellTowers = noCellTowers;
            numberWifiAccessPoints = noWifiAccessPoints;
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

        MutableLatLng(double lat, double lng) {
            latLng = new LatLng(lat, lng);
        }

        double getLat() {
            return latLng.latitude;
        }

        double getLng() {
            return latLng.longitude;
        }

        LatLng set(State state) {
            switch (state.getKey()) {
                case State.KEY_LAT:
                    latLng = new LatLng(state.getValue(), this.latLng.longitude);
                    break;
                case State.KEY_LNG:
                    latLng = new LatLng(this.latLng.latitude, state.getValue());
                    break;
            }

            return latLng;
        }

        LatLng get() {
            return latLng;
        }
    }
}
