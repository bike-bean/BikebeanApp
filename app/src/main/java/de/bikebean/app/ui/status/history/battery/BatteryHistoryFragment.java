package de.bikebean.app.ui.status.history.battery;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;


import de.bikebean.app.R;
import de.bikebean.app.ui.status.history.HistoryAdapter;
import de.bikebean.app.ui.status.history.HistoryFragment;
import de.bikebean.app.ui.status.history.HistoryViewModel;

public class BatteryHistoryFragment extends HistoryFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_battery_history, container, false);

        recyclerView = v.findViewById(R.id.recyclerView3);
        noDataText = v.findViewById(R.id.noDataText3);

        return v;
    }

    @Override
    protected HistoryViewModel getNewStateViewModel() {
        return new ViewModelProvider(this).get(BatteryHistoryViewModel.class);
    }

    @Override
    protected void setupListeners() {
        ((BatteryHistoryViewModel) st)
                .getBatteryConfirmed().observe(this, this::setStatesToAdapter);
    }

    @Override
    protected HistoryAdapter getNewAdapter(Context ctx) {
        return new BatteryHistoryAdapter(ctx,
                ((BatteryHistoryViewModel) st).getBatteryConfirmed().getValue());
    }
}
