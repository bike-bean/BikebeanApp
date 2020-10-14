package de.bikebean.app.ui.main.status.menu.history.battery;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;


import de.bikebean.app.R;
import de.bikebean.app.ui.main.status.menu.history.HistoryAdapter;
import de.bikebean.app.ui.main.status.menu.history.HistoryFragment;
import de.bikebean.app.ui.main.status.menu.history.HistoryViewModel;

public class BatteryHistoryFragment extends HistoryFragment {

    @NonNull
    @Override
    public View onCreateView(final @NonNull LayoutInflater inflater,
                             final @Nullable ViewGroup container,
                             final @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final @NonNull View v = inflater.inflate(R.layout.fragment_battery_history, container, false);

        recyclerView = v.findViewById(R.id.recyclerView3);
        noDataText = v.findViewById(R.id.noDataText3);

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
