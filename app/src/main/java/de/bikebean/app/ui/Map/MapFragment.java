package de.bikebean.app.ui.Map;

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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import de.bikebean.app.MainActivity;
import de.bikebean.app.R;
import de.bikebean.app.ui.SMS_commands.StatusFragment;

public class MapFragment extends Fragment implements  OnMapReadyCallback {

//    private DashboardViewModel dashboardViewModel;

    MapView mMapView;
    GoogleMap mGoogleMap;

    public MapFragment() throws JSONException {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView) v.findViewById(R.id.mapview1);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this); //this is important

        return v;
    }
    


    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

        //TODO: Zurücksetzen auf Normal = einfach diesen Code entfernen
//        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);


        LatLng bikebean = new LatLng(52.401911,13.575564);


        //TODO: auskommentieren, wenn API aktiviert wird
        LatLng current_position_bike = null;
        try {
            current_position_bike = new LatLng(StatusFragment.getCurrent_position_Lat(), StatusFragment.getCurrent_position_Lng());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(MainActivity.TAG, "Error.Lat- & LngGetter: " + e.getMessage());
        }





//        int zufallszahl1 = (int)((Math.random()) * 6 + 1);
//        int zufallszahl2 = (int)((Math.random()) * 15 + 1);



        //Marker auf der Karte
        mGoogleMap.addMarker(new MarkerOptions()
                //TODO: Scharf stellen mit echten Koordinaten aus API
                .position(current_position_bike)
//                .position(bikebean)
                //TODO: Name des Fahrrads einfügbar machen / Info für Snippet
                .title("Mein Fahrrad")
//                .snippet("∑ Funkmasten: "+StatusFragment.getNumber_of_celltowers()+",   ∑ WAP: "+StatusFragment.getNumber_of_wifiaccesspoints())
                .snippet("Anzahl Funkmasten: "+StatusFragment.getNumber_of_celltowers()+", Anzahl WAPs: "+StatusFragment.getNumber_of_wifiaccesspoints())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)

        ));

        //TODO: Scharf stellen mit echten Koordinaten aus API
//        Zoom am Anfang einstellen
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current_position_bike, 12));
//        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bikebean, 16));



//        //TODO: Scharf stellen mit echten Koordinaten aus API
//        CircleOptions circleOptions = new CircleOptions()
//                .center(bikebean)
//                .radius(100)
//                .strokeWidth(10)
//                .strokeColor(Color.BLACK)
//        ;


                // Instantiates a new CircleOptions object and defines the center and radius
        CircleOptions circleOptions = null;
        try {
            circleOptions = new CircleOptions()
                    .center(current_position_bike)
                    .radius(StatusFragment.getCurrent_position_Accuracy())
                    .strokeWidth(10)
                    .strokeColor(Color.BLACK)
            ;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(MainActivity.TAG, "Error.AccurancyGetter "+e.getMessage());
        }






        // Get back the mutable Circle
        Circle circle = mGoogleMap.addCircle(circleOptions);
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
