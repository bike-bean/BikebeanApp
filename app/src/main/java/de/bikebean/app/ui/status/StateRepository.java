package de.bikebean.app.ui.status;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.BikeBeanRoomDatabase;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateDao;

class StateRepository {

    private final StateDao mStateDao;

    private final LiveData<List<State>> mStatus;
    private final LiveData<List<State>> mStatusBattery;
    private final LiveData<List<State>> mStatusWarningNumber;
    private final LiveData<List<State>> mStatusInterval;
    private final LiveData<List<State>> mStatusWifi;
    private final LiveData<List<State>> mStatusLocationLat;
    private final LiveData<List<State>> mStatusLocationLng;
    private final LiveData<List<State>> mStatusLocationAcc;
    private final LiveData<List<State>> mStatusNumberCellTowers;
    private final LiveData<List<State>> mStatusNumberWifiAccessPoints;
    private final LiveData<List<State>> mWapp;
    private final LiveData<List<State>> mLocation;

    private final LiveData<List<State>> mConfirmedLocationLat;
    private final LiveData<List<State>> mConfirmedLocationLng;
    private final LiveData<List<State>> mConfirmedLocationAcc;

    private final LiveData<List<State>> mCellTowers;
    private final LiveData<List<State>> mWifiAccessPoints;

    StateRepository(Application application) {
        BikeBeanRoomDatabase db = BikeBeanRoomDatabase.getDatabase(application);
        mStateDao = db.stateDao();

        mStatus = mStateDao.getAllByKey(State.KEY_STATUS);
        mStatusBattery = mStateDao.getAllByKey(State.KEY_BATTERY);
        mStatusWarningNumber = mStateDao.getAllByKey(State.KEY_WARNING_NUMBER);
        mStatusInterval = mStateDao.getAllByKey(State.KEY_INTERVAL);
        mStatusWifi = mStateDao.getAllByKey(State.KEY_WIFI);
        mStatusLocationLat = mStateDao.getAllByKey(State.KEY_LAT);
        mStatusLocationLng = mStateDao.getAllByKey(State.KEY_LNG);
        mStatusLocationAcc = mStateDao.getAllByKey(State.KEY_ACC);
        mStatusNumberCellTowers = mStateDao.getAllByKey(State.KEY_NO_CELL_TOWERS);
        mStatusNumberWifiAccessPoints = mStateDao.getAllByKey(State.KEY_NO_WIFI_ACCESS_POINTS);
        mWapp = mStateDao.getByKeyAndState(State.KEY_WAPP, State.STATUS_PENDING);
        mLocation = mStateDao.getAllByKey(State.KEY_LOCATION);

        mConfirmedLocationLat = mStateDao.getByKeyAndState(State.KEY_LAT, State.STATUS_CONFIRMED);
        mConfirmedLocationLng = mStateDao.getByKeyAndState(State.KEY_LNG, State.STATUS_CONFIRMED);
        mConfirmedLocationAcc = mStateDao.getByKeyAndState(State.KEY_ACC, State.STATUS_CONFIRMED);

        mCellTowers = mStateDao.getAllByKey(State.KEY_CELL_TOWERS);
        mWifiAccessPoints = mStateDao.getAllByKey(State.KEY_WIFI_ACCESS_POINTS);
    }

    LiveData<List<State>> getStatus() {
        return mStatus;
    }


    LiveData<List<State>> getStatusBattery() {
        return mStatusBattery;
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

    LiveData<List<State>> getStatusLocationLat() {
        return mStatusLocationLat;
    }

    LiveData<List<State>> getStatusLocationLng() {
        return mStatusLocationLng;
    }

    LiveData<List<State>> getStatusLocationAcc() {
        return mStatusLocationAcc;
    }

    LiveData<List<State>> getStatusNumberCellTowers() {
        return mStatusNumberCellTowers;
    }

    LiveData<List<State>> getStatusNumberWifiAccessPoints() {
        return mStatusNumberWifiAccessPoints;
    }

    LiveData<List<State>> getWapp() {
        return mWapp;
    }

    LiveData<List<State>> getLocation() {
        return mLocation;
    }

    LiveData<List<State>> getCellTowers() {
        return mCellTowers;
    }

    LiveData<List<State>> getWifiAccessPoints() {
        return mWifiAccessPoints;
    }

    LiveData<List<State>> getConfirmedLocationLat() {
        return mConfirmedLocationLat;
    }

    LiveData<List<State>> getConfirmedLocationLng() {
        return mConfirmedLocationLng;
    }

    LiveData<List<State>> getConfirmedLocationAcc() {
        return mConfirmedLocationAcc;
    }


    List<State> getConfirmedByKeySync(String key) {
        return mStateDao.getByKeyAndStateSync(key, State.STATUS_CONFIRMED);
    }

    List<State> getByKeyAndIdSync(String key, int smsId) {
        return mStateDao.getByKeyAndIdSync(key, smsId);
    }

    List<State> getByKeySync(String key) {
        return mStateDao.getByKey(key);
    }

    List<State> getAllLocationByIdSync(int smsId) {
        return mStateDao.getAllById(smsId);
    }

    void insert(final State state) {
        BikeBeanRoomDatabase.databaseWriteExecutor.execute(() -> mStateDao.insert(state));
    }

    void confirmWapp(State s1, State s2) {
        confirmWapp(s1.getSmsId(), (double) s2.getSmsId());
        confirmWapp(s2.getSmsId(), (double) s1.getSmsId());
    }

    private void confirmWapp(int smsId, double value) {
        BikeBeanRoomDatabase.databaseWriteExecutor.execute(() ->
                mStateDao.updateStateByKeyAndSmsId(State.STATUS_CONFIRMED, value, State.KEY_WAPP, smsId));
    }
}
