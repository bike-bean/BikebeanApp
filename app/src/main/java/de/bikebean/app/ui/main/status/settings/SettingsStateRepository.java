package de.bikebean.app.ui.main.status.settings;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.main.status.StateRepository;

class SettingsStateRepository extends StateRepository {

    private final LiveData<List<State>> mStatus;
    private final LiveData<List<State>> mStatusWarningNumber;
    private final LiveData<List<State>> mStatusInterval;
    private final LiveData<List<State>> mStatusWifi;

    SettingsStateRepository(Application application) {
        super(application);

        mStatus = mStateDao.getAllByKey(State.KEY._STATUS.get());
        mStatusWarningNumber = mStateDao.getAllByKey(State.KEY.WARNING_NUMBER.get());
        mStatusInterval = mStateDao.getAllByKey(State.KEY.INTERVAL.get());
        mStatusWifi = mStateDao.getAllByKey(State.KEY.WIFI.get());
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