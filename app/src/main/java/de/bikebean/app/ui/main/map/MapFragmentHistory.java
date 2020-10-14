package de.bikebean.app.ui.main.map;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import de.bikebean.app.R;
import de.bikebean.app.ui.main.status.menu.history.HistoryActivity;

public class MapFragmentHistory extends MapFragment {

    @Override
    public void onActivityCreated(final @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        act.getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        Navigation.findNavController(requireView())
                                .navigate(R.id.history_action);
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();

        final @NonNull HistoryActivity historyActivity = (HistoryActivity) requireActivity();
        historyActivity.showButtons(false);
    }
}
