package de.bikebean.app.ui.main.status.menu.history;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.bikebean.app.db.DatabaseEntity;

public abstract class HistoryFragment extends Fragment {

    protected HistoryViewModel st;

    private HistoryAdapter adapter;

    // UI Elements
    protected RecyclerView recyclerView;
    protected TextView noDataText;

    @Nullable
    @Override
    public View onCreateView(final @NonNull LayoutInflater inflater,
                             final @Nullable ViewGroup container,
                             final @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(false);

        return null;
    }

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

        /* show the toolbar for this fragment */
        final @NonNull AppCompatActivity act = (AppCompatActivity) requireActivity();
        final @Nullable ActionBar actionbar = act.getSupportActionBar();

        if (actionbar != null)
            actionbar.show();
    }

    @Override
    public void onStart() {
        super.onStart();

        /* show the tab area */
        final @NonNull HistoryActivity historyActivity = (HistoryActivity) requireActivity();
        historyActivity.showButtons(true);
    }

    protected abstract @NonNull HistoryViewModel getNewStateViewModel();

    protected abstract void setupListeners();

    private void initRecyclerView() {
        final @NonNull Context ctx = requireContext();

        adapter = getNewAdapter(ctx);
        recyclerView.setAdapter(adapter);

        final @NonNull LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ctx);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    protected abstract @NonNull HistoryAdapter getNewAdapter(final @NonNull Context ctx);

    protected void setStatesToAdapter(@NonNull List<? extends DatabaseEntity> ls) {
        if (ls.size() != 0) {
            adapter.setStates(ls);
            noDataText.setVisibility(View.GONE);
        } else
            noDataText.setVisibility(View.VISIBLE);
    }
}
