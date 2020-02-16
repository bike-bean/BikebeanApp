package de.bikebean.app.ui.map;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.status.StateRepository;

class MapStateRepository extends StateRepository {

    private final LiveData<List<State>> mStatusNumberCellTowers;
    private final LiveData<List<State>> mStatusNumberWifiAccessPoints;
    private final LiveData<List<State>> mConfirmedLocationLat;
    private final LiveData<List<State>> mConfirmedLocationLng;
    private final LiveData<List<State>> mConfirmedLocationAcc;

    MapStateRepository(Application application) {
        super(application);

        mStatusNumberCellTowers = mStateDao.getAllByKey(State.KEY_NO_CELL_TOWERS);
        mStatusNumberWifiAccessPoints = mStateDao.getAllByKey(State.KEY_NO_WIFI_ACCESS_POINTS);

        mConfirmedLocationLat = mStateDao.getByKeyAndState(State.KEY_LAT, State.STATUS_CONFIRMED);
        mConfirmedLocationLng = mStateDao.getByKeyAndState(State.KEY_LNG, State.STATUS_CONFIRMED);
        mConfirmedLocationAcc = mStateDao.getByKeyAndState(State.KEY_ACC, State.STATUS_CONFIRMED);
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

    LiveData<List<State>> getStatusNumberCellTowers() {
        return mStatusNumberCellTowers;
    }

    LiveData<List<State>> getStatusNumberWifiAccessPoints() {
        return mStatusNumberWifiAccessPoints;
    }
}
