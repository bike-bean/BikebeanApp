package de.bikebean.app.ui.drawer.status.battery.history;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.drawer.status.history.HistoryRepository;

class BatteryHistoryRepository extends HistoryRepository {

    private final LiveData<List<State>> mBatteryConfirmed;

    BatteryHistoryRepository(final @NonNull Application application) {
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
