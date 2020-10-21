package de.bikebean.app.ui.drawer.status.battery;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.drawer.status.StateRepository;

class BatteryStateRepository extends StateRepository {

    private final LiveData<List<State>> mStatusBattery;
    private final LiveData<List<State>> mStatusInterval;
    private final LiveData<List<State>> mStatusWifi;
    private final LiveData<List<State>> mCellTowers;

    BatteryStateRepository(final @NonNull Application application) {
        super(application);

        mStatusBattery = mStateDao.getAllByKey(State.KEY.BATTERY.get());
        mStatusInterval = mStateDao.getAllByKey(State.KEY.INTERVAL.get());
        mStatusWifi = mStateDao.getAllByKey(State.KEY.WIFI.get());
        mCellTowers = mStateDao.getAllByKey(State.KEY.CELL_TOWERS.get());
    }

    LiveData<List<State>> getStatusBattery() {
        return mStatusBattery;
    }

    LiveData<List<State>> getStatusInterval() {
        return mStatusInterval;
    }

    LiveData<List<State>> getStatusWifi() {
        return mStatusWifi;
    }

    LiveData<List<State>> getCellTowers() {
        return mCellTowers;
    }
}
