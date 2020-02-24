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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import de.bikebean.app.db.DatabaseEntity;

public abstract class HistoryFragment extends Fragment {

    protected HistoryViewModel st;

    private HistoryAdapter adapter;

    // UI Elements
    protected RecyclerView recyclerView;
    protected TextView noDataText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(false);

        return null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        st = getNewStateViewModel();

        setupListeners();
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

    @Override
    public void onStart() {
        super.onStart();

        // show the tab area
        HistoryActivity historyActivity = (HistoryActivity) getActivity();
        if (historyActivity != null) {
            historyActivity.showButtons(true);
        }
    }

    protected abstract HistoryViewModel getNewStateViewModel();

    protected abstract void setupListeners();

    private void initRecyclerView() {
        Context ctx = requireContext();

        adapter = getNewAdapter(ctx);
        recyclerView.setAdapter(adapter);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ctx);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    protected abstract HistoryAdapter getNewAdapter(Context ctx);

    protected void setStatesToAdapter(List<? extends DatabaseEntity> ls) {
        if (ls.size() != 0) {
            adapter.setStates(ls);
            noDataText.setVisibility(View.GONE);
        } else
            noDataText.setVisibility(View.VISIBLE);
    }
}
