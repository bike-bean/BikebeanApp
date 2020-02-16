package de.bikebean.app.ui.status.history;

import android.app.Application;

import java.util.List;

import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.status.StateRepository;

class HistoryStateRepository extends StateRepository {

    HistoryStateRepository(Application application) {
        super(application);
    }

    List<State> getAllLocationByIdSync(int smsId) {
        return mStateDao.getAllByIdSync(smsId);
    }
}
