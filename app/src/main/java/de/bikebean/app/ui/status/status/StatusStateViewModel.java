package de.bikebean.app.ui.status.status;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.status.StateViewModel;

public class StatusStateViewModel extends StateViewModel {

    private final LiveData<List<State>> mStatus;
    private final LiveData<List<State>> mStatusWarningNumber;
    private final LiveData<List<State>> mStatusInterval;
    private final LiveData<List<State>> mStatusWifi;

    public StatusStateViewModel(Application application) {
        super(application);

        StatusStateRepository mRepository = new StatusStateRepository(application);

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

    LiveData<Boolean> getIntervalAborted() {
        return mIntervalAborted;
    }

    long getIntervalLastChangeDate() {
        State intervalConfirmed = getConfirmedStateSync(State.KEY.INTERVAL);

        if (intervalConfirmed != null)
            return intervalConfirmed.getTimestamp();

        return 0;
    }

    boolean getWifiStatusSync() {
        // TODO: really not the confirmed, but any state???
        State wifiConfirmed = getLastStateSync(State.KEY.WIFI);

        if (wifiConfirmed != null)
            return wifiConfirmed.getValue() > 0;

        return Boolean.valueOf(String.valueOf(INITIAL_WIFI));
    }

    int getIntervalStatusSync() {
        // TODO: really not the confirmed, but any state???
        State intervalState = getLastStateSync(State.KEY.INTERVAL);

        if (intervalState != null)
            return intervalState.getValue().intValue();

        return (int) INITIAL_INTERVAL;
    }
}
