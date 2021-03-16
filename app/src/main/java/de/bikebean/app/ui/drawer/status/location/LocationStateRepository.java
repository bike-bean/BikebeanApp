package de.bikebean.app.ui.drawer.status.location;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.BikeBeanRoomDatabase;
import de.bikebean.app.db.settings.settings.add_to_list_settings.WappState;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.drawer.status.StateRepository;

class LocationStateRepository extends StateRepository {

    private final LiveData<List<State>> mStatusLocationLat;
    private final LiveData<List<State>> mStatusLocationLng;
    private final LiveData<List<State>> mStatusLocationAcc;

    private final LiveData<List<State>> mWapp;
    private final LiveData<List<State>> mLocation;
    private final LiveData<List<State>> mCellTowers;
    private final LiveData<List<State>> mWifiAccessPoints;
    private final LiveData<List<State>> mStatusNumberCellTowers;
    private final LiveData<List<State>> mStatusNumberWifiAccessPoints;

    LocationStateRepository(final @NonNull Application application) {
        super(application);

        mStatusLocationLat = mStateDao.getAllByKey(State.KEY.LAT.get());
        mStatusLocationLng = mStateDao.getAllByKey(State.KEY.LNG.get());
        mStatusLocationAcc = mStateDao.getAllByKey(State.KEY.ACC.get());
        mWapp = mStateDao.getByKeyAndState(State.KEY.WAPP.get(), State.STATUS.PENDING.ordinal());
        mLocation = mStateDao.getAllByKey(State.KEY.LOCATION.get());
        mCellTowers = mStateDao.getAllByKey(State.KEY.CELL_TOWERS.get());
        mWifiAccessPoints = mStateDao.getAllByKey(State.KEY.WIFI_ACCESS_POINTS.get());
        mStatusNumberCellTowers = mStateDao.getAllByKey(State.KEY.NO_CELL_TOWERS.get());
        mStatusNumberWifiAccessPoints = mStateDao.getAllByKey(State.KEY.NO_WIFI_ACCESS_POINTS.get());
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

    LiveData<List<State>> getStatusNumberCellTowers() {
        return mStatusNumberCellTowers;
    }

    LiveData<List<State>> getStatusNumberWifiAccessPoints() {
        return mStatusNumberWifiAccessPoints;
    }

    void confirmWapp(final @NonNull State state) {
        confirmWapp(state.getSmsId(), state.getSmsId());
    }

    void confirmWapp(final @NonNull WappState wappState) {
        confirmWapp(wappState.getCellTowers().getSmsId(), wappState.getWifiAccessPoints().getSmsId());
        confirmWapp(wappState.getWifiAccessPoints().getSmsId(), wappState.getCellTowers().getSmsId());
    }

    private void confirmWapp(int smsId, double value) {
        BikeBeanRoomDatabase.databaseWriteExecutor.execute(() ->
                mStateDao.updateStateByKeyAndSmsId(
                        State.STATUS.CONFIRMED.ordinal(), value, State.KEY.WAPP.get(), smsId
                )
        );
    }
}
