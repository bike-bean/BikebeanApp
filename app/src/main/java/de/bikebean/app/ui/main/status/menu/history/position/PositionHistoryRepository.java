package de.bikebean.app.ui.main.status.menu.history.position;

import android.app.Application;

import androidx.annotation.NonNull;

import java.util.List;

import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.main.status.menu.history.HistoryRepository;

class PositionHistoryRepository extends HistoryRepository {

    PositionHistoryRepository(Application application) {
        super(application);
    }

    @NonNull List<State> getAllLocationByIdSync(int smsId) {
        return mStateDao.getAllByIdSync(smsId);
    }
}
