package de.bikebean.app.ui.main.status.menu.history.battery;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.main.status.menu.history.HistoryViewModel;

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
