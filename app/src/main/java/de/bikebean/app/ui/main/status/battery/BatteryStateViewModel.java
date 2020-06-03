package de.bikebean.app.ui.main.status.battery;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.settings.settings.replace_if_newer_settings.Wifi;
import de.bikebean.app.ui.utils.Utils;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.main.status.StateViewModel;

public class BatteryStateViewModel extends StateViewModel {

    private final LiveData<List<State>> mStatusBattery;

    public BatteryStateViewModel(Application application) {
        super(application);

        BatteryStateRepository mRepository = new BatteryStateRepository(application);
        mStatusBattery = mRepository.getStatusBattery();
    }

    LiveData<List<State>> getStatusBattery() {
        return mStatusBattery;
    }

    State getConfirmedBatterySync() {
        return getConfirmedStateSync(State.KEY.BATTERY);
    }

    static String getEstimatedDaysText(BatteryStateViewModel st) {
        State lastBatteryState = st.getConfirmedBatterySync();
        boolean isWifiOn = st.getConfirmedWifiSync();
        int interval = st.getConfirmedIntervalSync();

        return Utils.estimateBatteryDays(lastBatteryState, isWifiOn, interval);
    }

    private boolean getConfirmedWifiSync() {
        State wifiConfirmed = getConfirmedStateSync(State.KEY.WIFI);

        if (wifiConfirmed != null)
            return wifiConfirmed.getValue() > 0;

        return new Wifi().getRaw();
    }
}
