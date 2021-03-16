package de.bikebean.app.ui.drawer.history;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.bikebean.app.MainActivity;
import de.bikebean.app.R;
import de.bikebean.app.db.DatabaseEntity;

public abstract class HistoryFragment extends Fragment {

    protected HistoryViewModel st;

    private HistoryAdapter adapter;

    // UI Elements
    protected RecyclerView recyclerView;
    protected TextView noDataText;
    protected Button positionHistoryButton, batteryHistoryButton;

    protected abstract @NonNull HistoryViewModel getNewStateViewModel();

    protected abstract void setupListeners();

    protected abstract @NonNull HistoryAdapter getNewAdapter(final @NonNull Context ctx);

    @Override
    public void onActivityCreated(final @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        st = getNewStateViewModel();

        setupListeners();
        initRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();

        final @NonNull MainActivity activity = (MainActivity) requireActivity();
        activity.setToolbarScrollEnabled(true);
        activity.resumeToolbarAndBottomSheet();
    }

    private void initRecyclerView() {
        final @NonNull Context ctx = requireContext();

        adapter = getNewAdapter(ctx);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
    }

    protected void setStatesToAdapter(@NonNull List<? extends DatabaseEntity> ls) {
        if (ls.size() != 0) {
            adapter.setStates(ls);
            noDataText.setVisibility(View.GONE);
        } else
            noDataText.setVisibility(View.VISIBLE);
    }

    public void navigateToTab(final @NonNull View v) {
        MainActivity act = (MainActivity) requireActivity();
        if (v.getId() == R.id.positionButton)
            act.navigateTo(R.id.position_action, null);
        else
            act.navigateTo(R.id.battery_action, null);
    }
}
