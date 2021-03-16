package de.bikebean.app.ui.drawer.status.battery;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.drawer.status.StateViewModel;
import de.bikebean.app.ui.utils.date.BatteryBehaviour;

import static de.bikebean.app.db.settings.settings.replace_if_newer_settings.Wifi.INITIAL_WIFI;
import static de.bikebean.app.ui.drawer.status.StateViewModelExtKt.getConfirmedStateSync;

public class BatteryStateViewModel extends StateViewModel {

    private final LiveData<List<State>> mStatusBattery;
    private final LiveData<List<State>> mStatusInterval;
    private final LiveData<List<State>> mStatusWifi;
    private final LiveData<List<State>> mCellTowers;

    public BatteryStateViewModel(final @NonNull Application application) {
        super(application);

        final @NonNull BatteryStateRepository mRepository = new BatteryStateRepository(application);
        mStatusBattery = mRepository.getStatusBattery();
        mStatusInterval = mRepository.getStatusInterval();
        mStatusWifi = mRepository.getStatusWifi();
        mCellTowers = mRepository.getCellTowers();
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

    @Nullable State getConfirmedBatterySync() {
        return getConfirmedStateSync(this, State.KEY.BATTERY);
    }

    @Nullable BatteryBehaviour getBatteryBehaviour() {
        final @Nullable State lastBatteryState = getConfirmedBatterySync();
        boolean isWifiOn = getConfirmedWifiSync();
        int interval = getConfirmedIntervalSync();

        if (lastBatteryState != null)
            return new BatteryBehaviour(isWifiOn, interval, lastBatteryState);
        else return null;
    }

    private boolean getConfirmedWifiSync() {
        final @Nullable State wifiConfirmed =
                getConfirmedStateSync(this, State.KEY.WIFI);

        if (wifiConfirmed != null)
            return wifiConfirmed.getValue() > 0;

        return INITIAL_WIFI;
    }
}
