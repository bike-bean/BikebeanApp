package de.bikebean.app.ui.main.status.location;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.BikeBeanRoomDatabase;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.main.status.StateRepository;

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

        mStatusLocationLat = mStateDao.getAllByKey(State.KEY.LAT.get());
        mStatusLocationLng = mStateDao.getAllByKey(State.KEY.LNG.get());
        mStatusLocationAcc = mStateDao.getAllByKey(State.KEY.ACC.get());
        mWapp = mStateDao.getByKeyAndState(State.KEY.WAPP.get(), State.STATUS.PENDING.ordinal());
        mLocation = mStateDao.getAllByKey(State.KEY.LOCATION.get());
        mCellTowers = mStateDao.getAllByKey(State.KEY.CELL_TOWERS.get());
        mWifiAccessPoints = mStateDao.getAllByKey(State.KEY.WIFI_ACCESS_POINTS.get());
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
                mStateDao.updateStateByKeyAndSmsId(
                        State.STATUS.CONFIRMED.ordinal(), value, State.KEY.WAPP.get(), smsId
                )
        );
    }
}
