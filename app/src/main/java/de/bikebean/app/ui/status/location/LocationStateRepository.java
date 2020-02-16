package de.bikebean.app.ui.status.location;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.BikeBeanRoomDatabase;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.status.StateRepository;

class LocationStateRepository extends StateRepository {

    private final LiveData<List<State>> mStatusLocationLat;
    private final LiveData<List<State>> mStatusLocationLng;
    private final LiveData<List<State>> mStatusLocationAcc;

    private final LiveData<List<State>> mWapp;
    private final LiveData<List<State>> mLocation;
    private final LiveData<List<State>> mCellTowers;
    private final LiveData<List<State>> mWifiAccessPoints;

    LocationStateRepository(Application application) {
        super(application);

        mStatusLocationLat = mStateDao.getAllByKey(State.KEY_LAT);
        mStatusLocationLng = mStateDao.getAllByKey(State.KEY_LNG);
        mStatusLocationAcc = mStateDao.getAllByKey(State.KEY_ACC);
        mWapp = mStateDao.getByKeyAndState(State.KEY_WAPP, State.STATUS_PENDING);
        mLocation = mStateDao.getAllByKey(State.KEY_LOCATION);
        mCellTowers = mStateDao.getAllByKey(State.KEY_CELL_TOWERS);
        mWifiAccessPoints = mStateDao.getAllByKey(State.KEY_WIFI_ACCESS_POINTS);
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

    void confirmWapp(State s1, State s2) {
        confirmWapp(s1.getSmsId(), (double) s2.getSmsId());
        confirmWapp(s2.getSmsId(), (double) s1.getSmsId());
    }

    private void confirmWapp(int smsId, double value) {
        BikeBeanRoomDatabase.databaseWriteExecutor.execute(() ->
                mStateDao.updateStateByKeyAndSmsId(State.STATUS_CONFIRMED, value, State.KEY_WAPP, smsId));
    }
}
