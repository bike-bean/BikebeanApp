package de.bikebean.app.ui.status.battery;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.status.StateRepository;

class BatteryStateRepository extends StateRepository {

    private final LiveData<List<State>> mStatusBattery;

    BatteryStateRepository(Application application) {
        super(application);

        mStatusBattery = mStateDao.getAllByKey(State.KEY.BATTERY.get());
    }

    LiveData<List<State>> getStatusBattery() {
        return mStatusBattery;
    }
}
