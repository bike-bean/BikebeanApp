package de.bikebean.app.ui.map;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.bikebean.app.R;
import de.bikebean.app.ui.status.StatusViewModel;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private StatusViewModel statusViewModel;

    private MapView mMapView;

    // Map elements
    private Marker marker;
    private Circle circle;
    private MutableLatLng currentPositionBike;
    private Snippet snippet;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = v.findViewById(R.id.mapview1);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this); // this is important

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        statusViewModel = new ViewModelProvider(this).get(StatusViewModel.class);
    }

    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        // googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        currentPositionBike = new MutableLatLng(0.0, 0.0);
        snippet = new Snippet(0, 0);

        // Set a marker
        marker = googleMap.addMarker(new MarkerOptions()
                .position(currentPositionBike.get())
                .title("Mein Fahrrad") //TODO: Name des Fahrrads einfügbar machen
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

        // Set up observer to adjust changes to the view
        statusViewModel.getStatusLocationLat().observe(getViewLifecycleOwner(), statuses -> {
            if (statuses.size() == 0)
                return;

            marker.setPosition(currentPositionBike.setLat(statuses.get(0).getValue()));
            circle.setCenter(currentPositionBike.get());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPositionBike.get(), 14));
        });
        statusViewModel.getStatusLocationLng().observe(getViewLifecycleOwner(), statuses -> {
            if (statuses.size() == 0)
                return;
            marker.setPosition(currentPositionBike.setLng(statuses.get(0).getValue()));
            circle.setCenter(currentPositionBike.get());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPositionBike.get(), 14));
        });
        statusViewModel.getStatusLocationAcc().observe(getViewLifecycleOwner(), statuses -> {
            if (statuses.size() == 0)
                return;
            circle.setRadius(statuses.get(0).getValue());
        });
        statusViewModel.getStatusNumberCellTowers().observe(getViewLifecycleOwner(), statuses -> {
            if (statuses.size() == 0)
                return;
            marker.setSnippet(snippet.setNumberCellTowers(statuses.get(0).getValue().intValue()));
        });
        statusViewModel.getStatusNumberWifiAccessPoints().observe(getViewLifecycleOwner(), statuses -> {
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

    class Snippet {
        private int numberCellTowers;
        private int numberWifiAccessPoints;

        Snippet(int a, int b) {
            numberCellTowers = a;
            numberWifiAccessPoints = b;
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

        MutableLatLng(double lat, double lng) {
            latLng = new LatLng(lat, lng);
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
