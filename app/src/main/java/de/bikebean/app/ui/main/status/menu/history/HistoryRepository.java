package de.bikebean.app.ui.main.status.menu.history;

import android.app.Application;

import androidx.annotation.NonNull;

import de.bikebean.app.ui.main.status.StateRepository;

public class HistoryRepository extends StateRepository {

    protected HistoryRepository(final @NonNull Application application) {
        super(application);
    }
}
