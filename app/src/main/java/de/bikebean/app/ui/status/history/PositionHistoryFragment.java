package de.bikebean.app.ui.status.history;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.bikebean.app.R;
import de.bikebean.app.db.state.LocationState;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.status.sms.SmsViewModel;

public class PositionHistoryFragment extends Fragment {

    private HistoryStateViewModel stateViewModel;
    private Context ctx;

    private PositionHistoryAdapter adapter;

    // UI Elements
    private RecyclerView recyclerView;
    private TextView noDataText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_position_history, container, false);

        recyclerView = v.findViewById(R.id.recyclerView2);
        noDataText = v.findViewById(R.id.noDataText);

        setHasOptionsMenu(false);
        
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        stateViewModel = new ViewModelProvider(this).get(HistoryStateViewModel.class);
        SmsViewModel smsViewModel = new ViewModelProvider(this).get(SmsViewModel.class);
        smsViewModel.getAllIds().observe(getViewLifecycleOwner(), this::updateStates);

        FragmentActivity act = Objects.requireNonNull(getActivity());
        ctx = Objects.requireNonNull(act).getApplicationContext();

        initRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();

        // show the toolbar for this fragment
        AppCompatActivity act = (AppCompatActivity) getActivity();
        ActionBar actionbar = Objects.requireNonNull(act).getSupportActionBar();
        Objects.requireNonNull(actionbar).show();
    }

    private void initRecyclerView() {
        adapter = new PositionHistoryAdapter(ctx, stateViewModel.getLocationStates().getValue());
        recyclerView.setAdapter(adapter);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ctx);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void updateStates(List<Integer> smsIdList) {
        final List<LocationState> locationStates = new ArrayList<>();

        new Thread(() -> {
            for (int smsId : smsIdList) {
                LocationState locationState =
                        updateLocationStates(stateViewModel.getAllLocation(smsId));
                if (locationState != null)
                    locationStates.add(locationState);
            }

            stateViewModel.setLocationsState(locationStates);
        }).start();

        stateViewModel.getLocationStates().removeObservers(this);
        stateViewModel.getLocationStates().observe(this, this::setStatesToAdapter);
    }

    private LocationState updateLocationStates(List<State> states) {
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

    private void setStatesToAdapter(List<LocationState> ls) {
        if (ls.size() != 0) {
            adapter.setStates(ls);
            noDataText.setVisibility(View.GONE);
        } else
            noDataText.setVisibility(View.VISIBLE);
    }
}
