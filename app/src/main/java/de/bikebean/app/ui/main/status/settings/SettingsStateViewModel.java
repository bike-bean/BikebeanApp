package de.bikebean.app.ui.main.status.settings;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.settings.settings.Interval;
import de.bikebean.app.db.settings.settings.Wifi;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.main.status.StateViewModel;

public class SettingsStateViewModel extends StateViewModel {

    private final LiveData<List<State>> mStatus;
    private final LiveData<List<State>> mStatusWarningNumber;
    private final LiveData<List<State>> mStatusInterval;
    private final LiveData<List<State>> mStatusWifi;

    public SettingsStateViewModel(Application application) {
        super(application);

        SettingsStateRepository mRepository = new SettingsStateRepository(application);

        mStatus = mRepository.getStatus();
        mStatusWarningNumber = mRepository.getStatusWarningNumber();
        mStatusInterval = mRepository.getStatusInterval();
        mStatusWifi = mRepository.getStatusWifi();
    }

    LiveData<List<State>> getStatus() {
        return mStatus;
    }

    LiveData<List<State>> getStatusWarningNumber() {
        return mStatusWarningNumber;
    }

    LiveData<List<State>> getStatusInterval() {
        return mStatusInterval;
    }

    LiveData<List<State>> getStatusWifi() {
        return mStatusWifi;
    }

    long getIntervalLastChangeDate() {
        State intervalConfirmed = getConfirmedStateSync(State.KEY.INTERVAL);

        if (intervalConfirmed != null)
            return intervalConfirmed.getTimestamp();

        return 0;
    }

    boolean getConfirmedWifiSync() {
        State wifiConfirmed = getConfirmedStateSync(State.KEY.WIFI);

        if (wifiConfirmed != null)
            return wifiConfirmed.getValue() > 0;

        return new Wifi().getRaw();
    }

    boolean getWifiStatusSync() {
        State wifi = getLastStateSync(State.KEY.WIFI);

        if (wifi != null)
            return wifi.getValue() > 0;

        return new Wifi().getRaw();
    }

    int getIntervalStatusSync() {
        State intervalState = getLastStateSync(State.KEY.INTERVAL);

        if (intervalState != null)
            return intervalState.getValue().intValue();

        return new Interval().get().intValue();
    }
}
