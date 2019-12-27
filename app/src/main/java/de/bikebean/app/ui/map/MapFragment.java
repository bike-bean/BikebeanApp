package de.bikebean.app.ui.map;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import java.util.Objects;

import de.bikebean.app.MainActivity;
import de.bikebean.app.R;
import de.bikebean.app.ui.status.StatusFragment;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    // private DashboardViewModel dashboardViewModel;

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


    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        //TODO: Zurücksetzen auf Normal = einfach diesen Code entfernen
//        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //TODO: auskommentieren, wenn API aktiviert wird
        LatLng current_position_bike = null;
        try {
            current_position_bike = new LatLng(
                    StatusFragment.getCurrentPositionLat(),
                    StatusFragment.getCurrentPositionLng());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(MainActivity.TAG, "Error.Lat- & LngGetter: " + e.getMessage());
        }

        //Marker auf der Karte
        googleMap.addMarker(new MarkerOptions()
                //TODO: Scharf stellen mit echten Koordinaten aus API
                .position(Objects.requireNonNull(current_position_bike))
                //TODO: Name des Fahrrads einfügbar machen / Info für Snippet
                .title("Mein Fahrrad")
                .snippet("Anzahl Funkmasten: "+StatusFragment.getNumberOfCelltowers()+", Anzahl WAPs: "+StatusFragment.getNumberOfWifiaccesspoints())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)

        ));

        //TODO: Scharf stellen mit echten Koordinaten aus API
//        Zoom am Anfang einstellen
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current_position_bike, 12));
//        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bikebean, 16));

        CircleOptions circleOptions = null;
        try {
            circleOptions = new CircleOptions()
                    .center(current_position_bike)
                    .radius(StatusFragment.getCurrentPositionAccuracy())
                    .strokeWidth(10)
                    .strokeColor(Color.BLACK)
            ;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(MainActivity.TAG, "Error.AccurancyGetter "+e.getMessage());
        }

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
