package de.bikebean.app.ui.status.history.battery;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.status.history.HistoryViewModel;

public class BatteryHistoryViewModel extends HistoryViewModel {

    private final LiveData<List<State>> mBatteryConfirmed;

    public BatteryHistoryViewModel(Application application) {
        super(application);

        BatteryHistoryRepository mRepository = new BatteryHistoryRepository(application);
        mBatteryConfirmed = mRepository.getBatteryConfirmed();
    }

    LiveData<List<State>> getBatteryConfirmed() {
        return mBatteryConfirmed;
    }
}
