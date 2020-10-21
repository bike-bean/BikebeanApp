package de.bikebean.app.ui.drawer.history.battery;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;


import de.bikebean.app.R;
import de.bikebean.app.ui.drawer.history.HistoryAdapter;
import de.bikebean.app.ui.drawer.history.HistoryFragment;
import de.bikebean.app.ui.drawer.history.HistoryViewModel;

public class BatteryHistoryFragment extends HistoryFragment {

    @NonNull
    @Override
    public View onCreateView(final @NonNull LayoutInflater inflater,
                             final @Nullable ViewGroup container,
                             final @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final @NonNull View v = inflater.inflate(R.layout.fragment_history_battery, container, false);

        positionHistoryButton = v.findViewById(R.id.positionButton);
        batteryHistoryButton = v.findViewById(R.id.batteryButton);

        recyclerView = v.findViewById(R.id.recyclerView3);
        noDataText = v.findViewById(R.id.noDataText3);

        positionHistoryButton.setOnClickListener(super::navigateToTab);
        batteryHistoryButton.setOnClickListener(super::navigateToTab);

        return v;
    }

    @Override
    protected @NonNull HistoryViewModel getNewStateViewModel() {
        return new ViewModelProvider(this).get(BatteryHistoryViewModel.class);
    }

    @Override
    protected void setupListeners() {
        ((BatteryHistoryViewModel) st)
                .getBatteryConfirmed().observe(this, this::setStatesToAdapter);
    }

    @Override
    protected @NonNull HistoryAdapter getNewAdapter(final @NonNull Context ctx) {
        return new BatteryHistoryAdapter(ctx,
                ((BatteryHistoryViewModel) st).getBatteryConfirmed().getValue());
    }
}
