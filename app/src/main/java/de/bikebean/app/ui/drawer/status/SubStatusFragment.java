package de.bikebean.app.ui.drawer.status;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import de.bikebean.app.ui.drawer.status.settings.LiveDataTimerViewModel;

public abstract class SubStatusFragment extends SubStatusFragmentSmall {

    public LiveDataTimerViewModel tv;

    protected LastChangedView lastChangedView;

    @Override
    public void onActivityCreated(final @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tv = new ViewModelProvider(this).get(LiveDataTimerViewModel.class);
    }

    public void updatePendingText(final @NonNull ProgressView progressView,
                                     long startTime, long stopTime, long residualSeconds) {
        if (residualSeconds > 0)
            progressView.setProgress(requireContext(), startTime, stopTime, residualSeconds);
        else
            progressView.setIndeterminateProgress(requireContext(), stopTime);
    }
}
