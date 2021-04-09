package de.bikebean.app.ui.drawer.history.position;

import android.app.Application;

import androidx.annotation.NonNull;

import java.util.List;

import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.drawer.history.HistoryRepository;

class PositionHistoryRepository extends HistoryRepository {

    PositionHistoryRepository(final @NonNull Application application) {
        super(application);
    }

    @NonNull List<State> getAllLocationByIdSync(int smsId) {
        return mStateDao.getAllByIdSync(smsId);
    }
}