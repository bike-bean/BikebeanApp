package de.bikebean.app.ui.status;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import de.bikebean.app.R;
import de.bikebean.app.db.state.State;

public class StateViewModel extends AndroidViewModel {

    private static final double INITIAL_INTERVAL = 1.0;
    private static final double INITIAL_WIFI = 0.0;

    private final StateRepository mRepository;

    /*
    LiveData
     */
    // Battery
    private final LiveData<List<State>> mStatusBattery;

    // Status
    private final LiveData<List<State>> mStatus;
    private final LiveData<List<State>> mStatusWarningNumber;
    private final LiveData<List<State>> mStatusInterval;
    private final LiveData<List<State>> mStatusWifi;

    // Location
    private final LiveData<List<State>> mStatusLocationLat;
    private final LiveData<List<State>> mStatusLocationLng;
    private final LiveData<List<State>> mStatusLocationAcc;
    private final LiveData<List<State>> mStatusNumberCellTowers;
    private final LiveData<List<State>> mStatusNumberWifiAccessPoints;

    private final LiveData<List<State>> mPendingCellTowers;
    private final LiveData<List<State>> mPendingWifiAccessPoints;

    // Other
    private final MutableLiveData<Boolean> mIntervalAborted;

    public StateViewModel(Application application) {
        super(application);

        mRepository = new StateRepository(application);

        mStatusBattery = mRepository.getStatusBattery();

        mStatus = mRepository.getStatus();
        mStatusWarningNumber = mRepository.getStatusWarningNumber();
        mStatusInterval = mRepository.getStatusInterval();
        mStatusWifi = mRepository.getStatusWifi();

        mStatusLocationLat = mRepository.getStatusLocationLat();
        mStatusLocationLng = mRepository.getStatusLocationLng();
        mStatusLocationAcc = mRepository.getStatusLocationAcc();
        mStatusNumberCellTowers = mRepository.getStatusNumberCellTowers();
        mStatusNumberWifiAccessPoints = mRepository.getStatusNumberWifiAccessPoints();

        mPendingCellTowers = mRepository.getPendingCellTowers();
        mPendingWifiAccessPoints = mRepository.getPendingWifiAccessPoints();

        mIntervalAborted =  new MutableLiveData<>();
    }

    public LiveData<List<State>> getStatus() {
        return mStatus;
    }

    public LiveData<List<State>> getStatusBattery() {
        return mStatusBattery;
    }

    public LiveData<List<State>> getStatusWarningNumber() {
        return mStatusWarningNumber;
    }

    public LiveData<List<State>> getStatusInterval() {
        return mStatusInterval;
    }

    public LiveData<List<State>> getStatusWifi() {
        return mStatusWifi;
    }

    public LiveData<List<State>> getStatusLocationLat() {
        return mStatusLocationLat;
    }

    public LiveData<List<State>> getStatusLocationLng() {
        return mStatusLocationLng;
    }

    public LiveData<List<State>> getStatusLocationAcc() {
        return mStatusLocationAcc;
    }

    public LiveData<List<State>> getStatusNumberWifiAccessPoints() {
        return mStatusNumberWifiAccessPoints;
    }

    public LiveData<List<State>> getStatusNumberCellTowers() {
        return mStatusNumberCellTowers;
    }



    public LiveData<List<State>> getPendingCellTowers() {
        return mPendingCellTowers;
    }

    public LiveData<List<State>> getPendingWifiAccessPoints() {
        return mPendingWifiAccessPoints;
    }


    public void insert(State state) {
        if (state != null)
            mRepository.insert(state);
    }


    public void confirmLocationKeys() {
        mRepository.confirmLocationKeys();
    }



    public void notifyIntervalAbort(boolean b) {
        mIntervalAborted.postValue(b);
    }



    public LiveData<Boolean> getIntervalAborted() {
        return mIntervalAborted;
    }

    public void insertInitialStates(Context ctx) {
        insert(new State(
                1, State.KEY_WARNING_NUMBER,
                0.0, ctx.getString(R.string.warning_number_default),
                State.STATUS_UNSET, 0)
        );

        insert(new State(
                1, State.KEY_INTERVAL,
                INITIAL_INTERVAL, "",
                State.STATUS_CONFIRMED, 0)
        );

        insert(new State(
                1, State.KEY_WIFI,
                INITIAL_WIFI, "",
                State.STATUS_CONFIRMED, 0)
        );

        insert(new State(
                1, State.KEY_STATUS,
                0.0, "",
                State.STATUS_UNSET, 0)
        );

        insert(new State(
                1, State.KEY_BATTERY,
                -1.0, "",
                State.STATUS_UNSET, 0)
        );
    }



    public boolean getWifiStatusSync() {
        State wifiConfirmed = getStateSync(State.KEY_WIFI);

        if (wifiConfirmed != null)
            return wifiConfirmed.getValue() > 0;

        return Boolean.valueOf(String.valueOf(INITIAL_WIFI));
    }

    public int getIntervalStatusSync() {
        State intervalState = getStateSync(State.KEY_INTERVAL);

        if (intervalState != null)
            return intervalState.getValue().intValue();

        return (int) INITIAL_INTERVAL;
    }

    private State getStateSync(String key) {
        final MutableState state = new MutableState();

        new Thread(() -> {
            List<State> stateList = mRepository.getByKeySync(key);

            if (stateList.size() > 0)
                state.set(stateList.get(0));
            else
                state.set(null);
        }).start();

        while (state.get() == state.getNullState()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return state.get();
    }

    public boolean getConfirmedWifiSync() {
        State wifiConfirmed = getConfirmedStateSync(State.KEY_WIFI);

        if (wifiConfirmed != null)
            return wifiConfirmed.getValue() > 0;

        return Boolean.valueOf(String.valueOf(INITIAL_WIFI));
    }

    public int getConfirmedIntervalSync() {
        State intervalConfirmed = getConfirmedStateSync(State.KEY_INTERVAL);

        if (intervalConfirmed != null)
            return intervalConfirmed.getValue().intValue();

        return (int) INITIAL_INTERVAL;
    }

    public State getConfirmedBatterySync() {
        return getConfirmedStateSync(State.KEY_BATTERY);
    }

    private State getConfirmedStateSync(String key) {
        final MutableState confirmedState = new MutableState();

        new Thread(() -> {
            List<State> stateList = mRepository.getConfirmedByKeySync(key);

            if (stateList.size() > 0)
                confirmedState.set(stateList.get(0));
            else
                confirmedState.set(null);
        }).start();

        while (confirmedState.get() == confirmedState.getNullState()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return confirmedState.get();
    }

    public static class MutableState {

        private State state;
        private volatile boolean is_set = false;
        private final State nullState = new State(State.KEY_BATTERY, 0.0);

        void set(State i) {
            this.state = i;
            is_set = true;
        }

        State get() {
            if (is_set)
                return state;
            else
                return nullState;
        }

        State getNullState() {
            return nullState;
        }
    }
}
