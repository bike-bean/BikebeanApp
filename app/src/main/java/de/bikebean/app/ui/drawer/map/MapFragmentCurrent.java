package de.bikebean.app.ui.drawer.map;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.google.android.gms.maps.GoogleMap;

import java.util.List;

import de.bikebean.app.db.state.State;

public class MapFragmentCurrent extends MapFragment {

    @Override
    public void onMapReady(final @NonNull GoogleMap googleMap) {
        super.onMapReady(googleMap);

        /*
         Set up observers to adjust changes to the view
         */
        final @NonNull LifecycleOwner l = getViewLifecycleOwner();
        final @NonNull Observer<List<State>> mapObserver = mapFragmentHelper::setMapElements;

        mapFragmentViewModel.getConfirmedLocationLat().observe(l, mapObserver);
        mapFragmentViewModel.getConfirmedLocationLng().observe(l, mapObserver);
        mapFragmentViewModel.getConfirmedLocationAcc().observe(l, mapObserver);
    }
}
