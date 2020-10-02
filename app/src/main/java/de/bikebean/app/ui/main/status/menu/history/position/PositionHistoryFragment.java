package de.bikebean.app.ui.main.status.menu.history.position;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import de.bikebean.app.R;
import de.bikebean.app.db.state.LocationState;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.main.status.menu.history.HistoryAdapter;
import de.bikebean.app.ui.main.status.menu.history.HistoryFragment;
import de.bikebean.app.ui.main.status.menu.history.HistoryViewModel;
import de.bikebean.app.ui.main.status.menu.sms_history.SmsViewModel;

public class PositionHistoryFragment extends HistoryFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_position_history, container, false);

        recyclerView = v.findViewById(R.id.recyclerView2);
        noDataText = v.findViewById(R.id.noDataText);

        return v;
    }

    @Override
    protected HistoryViewModel getNewStateViewModel() {
        return new ViewModelProvider(this).get(PositionHistoryViewModel.class);
    }

    @Override
    protected void setupListeners() {
        SmsViewModel smsViewModel = new ViewModelProvider(this).get(SmsViewModel.class);
        smsViewModel.getAllIds().observe(this, this::updateStates);
    }

    @Override
    protected HistoryAdapter getNewAdapter(Context ctx) {
        return new PositionHistoryAdapter(ctx,
                ((PositionHistoryViewModel) st).getLocationStates().getValue());
    }

    private void updateStates(List<Integer> smsIdList) {
        final @NonNull List<LocationState> locationStates = new ArrayList<>();

        new Thread(() -> {
            for (int smsId : smsIdList) {
                @Nullable LocationState locationState =
                        updateLocationStates(((PositionHistoryViewModel) st).getAllLocation(smsId));
                if (locationState != null)
                    locationStates.add(locationState);
            }

            ((PositionHistoryViewModel) st).setLocationsState(locationStates);
        }).start();

        ((PositionHistoryViewModel) st).getLocationStates().removeObservers(this);
        ((PositionHistoryViewModel) st).getLocationStates().observe(this, this::setStatesToAdapter);
    }

    private @Nullable LocationState updateLocationStates(@NonNull List<State> states) {
        State latState = null;
        State lngState = null;
        State accState = null;
        State noCellTowersState = null;
        State noWifiAccessPointsState = null;

        if (states.size() < 5)
            return null;

        for (State s : states)
            switch (State.KEY.getValue(s.getKey())) {
                case LAT:
                    latState = s;
                    break;
                case LNG:
                    lngState = s;
                    break;
                case ACC:
                    accState = s;
                    break;
                case NO_CELL_TOWERS:
                    noCellTowersState = s;
                    break;
                case NO_WIFI_ACCESS_POINTS:
                    noWifiAccessPointsState = s;
                    break;
            }

        if (latState != null && lngState != null && accState != null
                && noCellTowersState != null && noWifiAccessPointsState != null)
            return new LocationState(
                    latState, lngState, accState,
                    noCellTowersState, noWifiAccessPointsState
            );

        return null;
    }
}
