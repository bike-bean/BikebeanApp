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

    private final LiveData<List<State>> mPendingCellTowers;
    private final LiveData<List<State>> mPendingWifiAccessPoints;
    private final LiveData<List<State>> mPendingInterval;
    private final LiveData<List<State>> mPendingWifi;

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

        mPendingCellTowers = mStateDao.getByKeyAndState(
                State.KEY_CELL_TOWERS, State.STATUS_PENDING);
        mPendingWifiAccessPoints = mStateDao.getByKeyAndState(
                State.KEY_WIFI_ACCESS_POINTS, State.STATUS_PENDING);
        mPendingInterval = mStateDao.getByKeyAndState(
                State.KEY_INTERVAL, State.STATUS_PENDING);
        mPendingWifi = mStateDao.getByKeyAndState(
                State.KEY_WIFI, State.STATUS_PENDING);
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



    LiveData<List<State>> getPendingCellTowers() {
        return mPendingCellTowers;
    }

    LiveData<List<State>> getPendingWifiAccessPoints() {
        return mPendingWifiAccessPoints;
    }

    LiveData<List<State>> getPendingInterval() {
        return mPendingInterval;
    }

    LiveData<List<State>> getPendingWifi() {
        return mPendingWifi;
    }



    List<State> getConfirmedWifi() {
        return mStateDao.getByKeyAndStateSync(State.KEY_WIFI, State.STATUS_CONFIRMED);
    }

    List<State> getConfirmedInterval() {
        return mStateDao.getByKeyAndStateSync(State.KEY_INTERVAL, State.STATUS_CONFIRMED);
    }



    List<State> getWarningNumber() {
        return mStateDao.getByKey(State.KEY_WARNING_NUMBER);
    }

    List<State> getInterval() {
        return mStateDao.getByKey(State.KEY_INTERVAL);
    }

    List<State> getWifi() {
        return mStateDao.getByKey(State.KEY_WIFI);
    }


    void insert(final State state) {
        BikeBeanRoomDatabase.databaseWriteExecutor.execute(() -> mStateDao.insert(state));
    }

    void deleteUnsetByKey(String key) {
        BikeBeanRoomDatabase.databaseWriteExecutor.execute(() ->
                mStateDao.deleteByKeyAndState(key, State.STATUS_UNSET));
    }

    /*
    void deletePendingByKey(final String key) {
        BikeBeanRoomDatabase.databaseWriteExecutor.execute(() ->
                mStateDao.deleteByKeyAndState(key, State.STATUS_PENDING));
    }
    */

    /*
    void confirmBySmsId(final int smsId) {
        BikeBeanRoomDatabase.databaseWriteExecutor.execute(() ->
                mStateDao.updateStateBySmsId(State.STATUS_CONFIRMED, smsId));
    }
    */

    void confirmLocationKeys() {
        BikeBeanRoomDatabase.databaseWriteExecutor.execute(() ->
                mStateDao.updateStateByKey(State.STATUS_CONFIRMED, State.KEY_CELL_TOWERS));
        BikeBeanRoomDatabase.databaseWriteExecutor.execute(() ->
                mStateDao.updateStateByKey(State.STATUS_CONFIRMED, State.KEY_WIFI_ACCESS_POINTS));
    }

    void confirmInterval() {
        BikeBeanRoomDatabase.databaseWriteExecutor.execute(() ->
                mStateDao.updateStateByKey(State.STATUS_CONFIRMED, State.KEY_INTERVAL));
    }

    void confirmWifi() {
        BikeBeanRoomDatabase.databaseWriteExecutor.execute(() ->
                mStateDao.updateStateByKey(State.STATUS_CONFIRMED, State.KEY_WIFI));
    }

    void pendWifi(int smsId) {
        BikeBeanRoomDatabase.databaseWriteExecutor.execute(() ->
                mStateDao.updateStateBySmsId(State.STATUS_PENDING, smsId));
    }
}
