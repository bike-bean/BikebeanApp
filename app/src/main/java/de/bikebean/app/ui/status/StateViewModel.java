package de.bikebean.app.ui.status;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import de.bikebean.app.R;
import de.bikebean.app.db.state.LocationState;
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
    private final LiveData<List<State>> mCellTowers;
    private final LiveData<List<State>> mWifiAccessPoints;
    private final LiveData<List<State>> mWapp;
    private final LiveData<List<State>> mLocation;

    private final LiveData<List<State>> mConfirmedLocationLat;
    private final LiveData<List<State>> mConfirmedLocationLng;
    private final LiveData<List<State>> mConfirmedLocationAcc;

    // Other
    private final MutableLiveData<Boolean> mIntervalAborted;
    private final MutableLiveData<List<LocationState>> mLocationStates;

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
        mCellTowers = mRepository.getCellTowers();
        mWifiAccessPoints = mRepository.getWifiAccessPoints();
        mWapp = mRepository.getWapp();
        mLocation = mRepository.getLocation();

        mConfirmedLocationLat = mRepository.getConfirmedLocationLat();
        mConfirmedLocationLng = mRepository.getConfirmedLocationLng();
        mConfirmedLocationAcc = mRepository.getConfirmedLocationAcc();

        mIntervalAborted = new MutableLiveData<>();
        mLocationStates = new MutableLiveData<>();
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

    public LiveData<List<State>> getCellTowers() {
        return mCellTowers;
    }

    public LiveData<List<State>> getWifiAccessPoints() {
        return mWifiAccessPoints;
    }

    public LiveData<List<State>> getWapp() {
        return mWapp;
    }

    public LiveData<List<State>> getLocation() {
        return mLocation;
    }

    public LiveData<List<State>> getConfirmedLocationLat() {
        return mConfirmedLocationLat;
    }

    public LiveData<List<State>> getConfirmedLocationLng() {
        return mConfirmedLocationLng;
    }

    public LiveData<List<State>> getConfirmedLocationAcc() {
        return mConfirmedLocationAcc;
    }

    public List<State> getAllLocation(int smsId) {
        return mRepository.getAllLocationByIdSync(smsId);
    }

    public void insert(State state) {
        if (state != null)
            mRepository.insert(state);
    }

    public void confirmWapp(State cellTowerState, State wifiAccessPointsState) {
        mRepository.confirmWapp(cellTowerState, wifiAccessPointsState);
    }



    public void notifyIntervalAbort(boolean b) {
        mIntervalAborted.postValue(b);
    }

    public LiveData<Boolean> getIntervalAborted() {
        return mIntervalAborted;
    }

    public void setLocationsState(List<LocationState> locationStates) {
        mLocationStates.postValue(locationStates);
    }

    public LiveData<List<LocationState>> getLocationStates() {
        return mLocationStates;
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

        insert(new State(
                1, State.KEY_LOCATION,
                0.0, "",
                State.STATUS_UNSET, 0)
        );

        insert(new State(
                1, State.KEY_CELL_TOWERS,
                0.0, "",
                State.STATUS_UNSET, 0)
        );

        insert(new State(
                1, State.KEY_WIFI_ACCESS_POINTS,
                0.0, "",
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

    public State getConfirmedLocationSync(State state) {
        return getConfirmedStateSync(state.getKey());
    }

    public String getWifiAccessPointsSync() {
        State wifiAccessPoints = getConfirmedStateSync(State.KEY_WIFI_ACCESS_POINTS);

        if (wifiAccessPoints != null)
            return wifiAccessPoints.getLongValue();
        else
            return "";
    }

    public boolean getLocationByIdSync(int smsId) {
        return getStateByIdSync(State.KEY_LAT, smsId) != null;
    }

    public State getWifiAccessPointsBySmsIdSync(int smsId) {
        return getStateByIdSync(State.KEY_WIFI_ACCESS_POINTS, smsId);
    }

    public State getCellTowersBySmsIdSync(int smsId) {
        return getStateByIdSync(State.KEY_CELL_TOWERS, smsId);
    }

    private State getStateByIdSync(String key, int smsId) {
        final MutableState idState = new MutableState();

        new Thread(() -> {
            List<State> stateList = mRepository.getByKeyAndIdSync(key, smsId);

            if (stateList.size() > 0)
                idState.set(stateList.get(0));
            else
                idState.set(null);
        }).start();

        while (idState.get() == idState.getNullState()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return idState.get();
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
