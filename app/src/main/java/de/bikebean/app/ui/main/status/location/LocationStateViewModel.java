package de.bikebean.app.ui.main.status.location;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.main.status.StateViewModel;

public class LocationStateViewModel extends StateViewModel {

    private final LocationStateRepository mRepository;

    private final LiveData<List<State>> mStatusLocationLat;
    private final LiveData<List<State>> mStatusLocationLng;
    private final LiveData<List<State>> mStatusLocationAcc;
    private final LiveData<List<State>> mCellTowers;
    private final LiveData<List<State>> mWifiAccessPoints;
    private final LiveData<List<State>> mWapp;
    private final LiveData<List<State>> mLocation;

    public LocationStateViewModel(Application application) {
        super(application);

        mRepository = new LocationStateRepository(application);

        mStatusLocationLat = mRepository.getStatusLocationLat();
        mStatusLocationLng = mRepository.getStatusLocationLng();
        mStatusLocationAcc = mRepository.getStatusLocationAcc();
        mCellTowers = mRepository.getCellTowers();
        mWifiAccessPoints = mRepository.getWifiAccessPoints();
        mWapp = mRepository.getWapp();
        mLocation = mRepository.getLocation();
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

    LiveData<List<State>> getCellTowers() {
        return mCellTowers;
    }

    LiveData<List<State>> getWifiAccessPoints() {
        return mWifiAccessPoints;
    }

    LiveData<List<State>> getWapp() {
        return mWapp;
    }

    LiveData<List<State>> getLocation() {
        return mLocation;
    }

    void confirmWapp(State cellTowerState, State wifiAccessPointsState) {
        mRepository.confirmWapp(cellTowerState, wifiAccessPointsState);
    }

    State getConfirmedLocationSync(State state) {
        return getConfirmedStateSync(State.KEY.getValue(state.getKey()));
    }

    boolean getLocationByIdSync(int smsId) {
        return getStateByIdSync(State.KEY.LAT, smsId) != null;
    }

    State getWifiAccessPointsBySmsIdSync(int smsId) {
        return getStateByIdSync(State.KEY.WIFI_ACCESS_POINTS, smsId);
    }

    State getCellTowersBySmsIdSync(int smsId) {
        return getStateByIdSync(State.KEY.CELL_TOWERS, smsId);
    }
}
