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

import java.util.List;
import java.util.Objects;

import de.bikebean.app.R;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.status.battery.BatteryStateViewModel;

public class BatteryHistoryFragment extends Fragment {

    private BatteryStateViewModel stateViewModel;
    private Context ctx;

    private BatteryHistoryAdapter adapter;

    // UI Elements
    private RecyclerView recyclerView;
    private TextView noDataText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_battery_history, container, false);

        recyclerView = v.findViewById(R.id.recyclerView3);
        noDataText = v.findViewById(R.id.noDataText3);

        setHasOptionsMenu(false);
        
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        stateViewModel = new ViewModelProvider(this).get(BatteryStateViewModel.class);
        stateViewModel.getStatusBattery().observe(getViewLifecycleOwner(), this::setStatesToAdapter);

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
        adapter = new BatteryHistoryAdapter(ctx, stateViewModel.getStatusBattery().getValue());
        recyclerView.setAdapter(adapter);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ctx);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void setStatesToAdapter(List<State> ls) {
        if (ls.size() != 0) {
            adapter.setStates(ls);
            noDataText.setVisibility(View.GONE);
        } else
            noDataText.setVisibility(View.VISIBLE);
    }
}
