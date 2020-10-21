package de.bikebean.app.ui.drawer.status.settings;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.drawer.status.StateViewModel;

import static de.bikebean.app.db.settings.settings.replace_if_newer_settings.Interval.INITIAL_INTERVAL;
import static de.bikebean.app.db.settings.settings.replace_if_newer_settings.Wifi.INITIAL_WIFI;

public class SettingsStateViewModel extends StateViewModel {

    private final LiveData<List<State>> mStatus;
    private final LiveData<List<State>> mStatusWarningNumber;
    private final LiveData<List<State>> mStatusInterval;
    private final LiveData<List<State>> mStatusWifi;

    public SettingsStateViewModel(final @NonNull Application application) {
        super(application);

        final @NonNull SettingsStateRepository mRepository = new SettingsStateRepository(application);

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

    boolean getWifiStatusSync() {
        final @Nullable State wifi = getLastStateSync(State.KEY.WIFI);

        if (wifi != null)
            return wifi.getValue() > 0;

        return INITIAL_WIFI;
    }

    int getIntervalStatusSync() {
        final @Nullable State intervalState = getLastStateSync(State.KEY.INTERVAL);

        if (intervalState != null)
            return intervalState.getValue().intValue();

        return INITIAL_INTERVAL;
    }
}
