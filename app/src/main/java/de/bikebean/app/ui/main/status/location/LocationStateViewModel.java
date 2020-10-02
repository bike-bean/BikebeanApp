package de.bikebean.app.ui.main.status.location;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.settings.settings.WappState;
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

    void confirmWapp(WappState wappState) {
        mRepository.confirmWapp(wappState);
    }

    public @Nullable State getConfirmedLocationSync(@NonNull State state) {
        return getConfirmedStateSync(State.KEY.getValue(state.getKey()));
    }

    boolean getLocationByIdSync(@NonNull WappState wappState) {
        return getStateByIdSync(State.KEY.LOCATION, wappState.getSmsId()) != null;
    }

    public State getWifiAccessPointsByWappSync(@NonNull State wappState) {
        return getStateByIdSync(State.KEY.WIFI_ACCESS_POINTS, wappState.getSmsId());
    }

    public State getCellTowersByWappSync(@NonNull State wappState) {
        return getStateByIdSync(State.KEY.CELL_TOWERS, wappState.getSmsId());
    }
}
