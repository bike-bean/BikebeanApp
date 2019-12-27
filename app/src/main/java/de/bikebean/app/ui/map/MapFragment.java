package de.bikebean.app.ui.map;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

import de.bikebean.app.R;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    // private DashboardViewModel dashboardViewModel;
    private SharedPreferences sharedPreferences;

    private MapView mMapView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = v.findViewById(R.id.mapview1);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this); //this is important

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FragmentActivity act = Objects.requireNonNull(getActivity());
        Context ctx = act.getApplicationContext();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        //TODO: Zurücksetzen auf Normal = einfach diesen Code entfernen
//        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //TODO: auskommentieren, wenn API aktiviert wird
        LatLng current_position_bike = new LatLng(
                sharedPreferences.getFloat("lat", (float) 0.0),
                sharedPreferences.getFloat("lng", (float) 0.0));

        //Marker auf der Karte
        googleMap.addMarker(new MarkerOptions()
                //TODO: Scharf stellen mit echten Koordinaten aus API
                .position(Objects.requireNonNull(current_position_bike))
                //TODO: Name des Fahrrads einfügbar machen / Info für Snippet
                .title("Mein Fahrrad")
                .snippet("Anzahl Funkmasten: " +
                        sharedPreferences.getInt("numberCellTowers", 0) +
                        ", Anzahl WAPs: " +
                        sharedPreferences.getInt("numberWifiAccessPoints", 0))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)

        ));

        //TODO: Scharf stellen mit echten Koordinaten aus API
//        Zoom am Anfang einstellen
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current_position_bike, 12));
//        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bikebean, 16));

        // Instantiates a new CircleOptions object and defines the center and radius
        CircleOptions circleOptions = new CircleOptions()
                .center(current_position_bike)
                .radius(sharedPreferences.getFloat("acc", (float) 0.0))
                .strokeWidth(10)
                .strokeColor(Color.BLACK);

        // Get back the mutable Circle
        googleMap.addCircle(circleOptions);
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


//Ursprünglicher Code:
//        dashboardViewModel =
//                ViewModelProviders.of(this).get(DashboardViewModel.class);
//        View root = inflater.inflate(R.layout.fragment_map, container, false);
//        final TextView textView = root.findViewById(R.id.text_dashboard);
//        dashboardViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
}
