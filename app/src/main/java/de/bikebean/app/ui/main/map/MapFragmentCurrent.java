package de.bikebean.app.ui.main.map;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.GoogleMap;

import java.util.List;

import de.bikebean.app.R;
import de.bikebean.app.db.state.State;

public class MapFragmentCurrent extends MapFragment {

    private final @NonNull OnBackPressedCallback backPressedCallback =
            new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    Navigation.findNavController(requireView()).navigate(R.id.back_action);
                }
            };

    @Override
    public void onActivityCreated(final @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        act.getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), backPressedCallback);
    }

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
        mapFragmentViewModel.getStatusNumberCellTowers().observe(l, mapObserver);
        mapFragmentViewModel.getStatusNumberWifiAccessPoints().observe(l, mapObserver);
    }
}
