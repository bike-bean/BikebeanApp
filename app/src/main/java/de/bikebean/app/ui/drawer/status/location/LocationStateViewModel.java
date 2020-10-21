package de.bikebean.app.ui.drawer.status.location;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.settings.settings.WappState;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.drawer.status.StateViewModel;

public class LocationStateViewModel extends StateViewModel {

    private final @NonNull LocationStateRepository mRepository;

    private final LiveData<List<State>> mStatusLocationLat;
    private final LiveData<List<State>> mStatusLocationLng;
    private final LiveData<List<State>> mStatusLocationAcc;
    private final LiveData<List<State>> mCellTowers;
    private final LiveData<List<State>> mWifiAccessPoints;
    private final LiveData<List<State>> mStatusNumberCellTowers;
    private final LiveData<List<State>> mStatusNumberWifiAccessPoints;
    private final LiveData<List<State>> mWapp;
    private final LiveData<List<State>> mLocation;

    public LocationStateViewModel(final @NonNull Application application) {
        super(application);

        mRepository = new LocationStateRepository(application);

        mStatusLocationLat = mRepository.getStatusLocationLat();
        mStatusLocationLng = mRepository.getStatusLocationLng();
        mStatusLocationAcc = mRepository.getStatusLocationAcc();
        mCellTowers = mRepository.getCellTowers();
        mWifiAccessPoints = mRepository.getWifiAccessPoints();
        mStatusNumberCellTowers = mRepository.getStatusNumberCellTowers();
        mStatusNumberWifiAccessPoints = mRepository.getStatusNumberWifiAccessPoints();
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

    LiveData<List<State>> getStatusNumberWifiAccessPoints() {
        return mStatusNumberWifiAccessPoints;
    }

    LiveData<List<State>> getStatusNumberCellTowers() {
        return mStatusNumberCellTowers;
    }

    LiveData<List<State>> getWapp() {
        return mWapp;
    }

    LiveData<List<State>> getLocation() {
        return mLocation;
    }

    public void confirmWapp(final @NonNull State state) {
        mRepository.confirmWapp(state);
    }

    public void confirmWapp(final @NonNull WappState wappState) {
        mRepository.confirmWapp(wappState);
    }

    public @Nullable State getConfirmedLocationSync(final @NonNull State state) {
        return getConfirmedStateSync(State.KEY.getValue(state));
    }

    boolean getLocationByIdSync(final @NonNull WappState wappState) {
        return getStateByIdSync(State.KEY.LOCATION, wappState.getSmsId()) != null;
    }

    public @Nullable State getWifiAccessPointsByWappSync(final @NonNull State wappState) {
        return getStateByIdSync(State.KEY.WIFI_ACCESS_POINTS, wappState.getSmsId());
    }

    public @Nullable State getCellTowersByWappSync(final @NonNull State wappState) {
        return getStateByIdSync(State.KEY.CELL_TOWERS, wappState.getSmsId());
    }
}
