package de.bikebean.app.ui.drawer.status.battery.history;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.drawer.status.history.HistoryViewModel;

public class BatteryHistoryViewModel extends HistoryViewModel {

    private final LiveData<List<State>> mBatteryConfirmed;

    public BatteryHistoryViewModel(final @NonNull Application application) {
        super(application);

        final @NonNull BatteryHistoryRepository mRepository = new BatteryHistoryRepository(application);
        mBatteryConfirmed = mRepository.getBatteryConfirmed();
    }

    LiveData<List<State>> getBatteryConfirmed() {
        return mBatteryConfirmed;
    }
}
