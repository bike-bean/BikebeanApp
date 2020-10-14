package de.bikebean.app.ui.main.status.battery;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.main.status.StateRepository;

class BatteryStateRepository extends StateRepository {

    private final LiveData<List<State>> mStatusBattery;

    BatteryStateRepository(final @NonNull Application application) {
        super(application);

        mStatusBattery = mStateDao.getAllByKey(State.KEY.BATTERY.get());
    }

    LiveData<List<State>> getStatusBattery() {
        return mStatusBattery;
    }
}
