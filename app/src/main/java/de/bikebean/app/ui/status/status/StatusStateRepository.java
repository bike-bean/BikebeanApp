package de.bikebean.app.ui.status.status;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.status.StateRepository;

class StatusStateRepository extends StateRepository {

    private final LiveData<List<State>> mStatus;
    private final LiveData<List<State>> mStatusWarningNumber;
    private final LiveData<List<State>> mStatusInterval;
    private final LiveData<List<State>> mStatusWifi;

    StatusStateRepository(Application application) {
        super(application);

        mStatus = mStateDao.getAllByKey(State.KEY_STATUS);
        mStatusWarningNumber = mStateDao.getAllByKey(State.KEY_WARNING_NUMBER);
        mStatusInterval = mStateDao.getAllByKey(State.KEY_INTERVAL);
        mStatusWifi = mStateDao.getAllByKey(State.KEY_WIFI);
    }

    LiveData<List<State>> getStatus() {
        return mStatus;
    }

    LiveData<List<State>> getStatusWarningNumber() {
        return mStatusWarningNumber;
    }

    LiveData<List<State>> getStatusInterval() {
        return mStatusInterval;
    }

    LiveData<List<State>> getStatusWifi() {
        return mStatusWifi;
    }
}
