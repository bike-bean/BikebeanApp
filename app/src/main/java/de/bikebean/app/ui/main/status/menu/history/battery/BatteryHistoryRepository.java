package de.bikebean.app.ui.main.status.menu.history.battery;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.main.status.menu.history.HistoryRepository;

class BatteryHistoryRepository extends HistoryRepository {

    private final LiveData<List<State>> mBatteryConfirmed;

    BatteryHistoryRepository(Application application) {
        super(application);

        mBatteryConfirmed = mStateDao.getByKeyAndState(
                State.KEY.BATTERY.get(),
                State.STATUS.CONFIRMED.ordinal()
        );
    }

    LiveData<List<State>> getBatteryConfirmed() {
        return mBatteryConfirmed;
    }
}
