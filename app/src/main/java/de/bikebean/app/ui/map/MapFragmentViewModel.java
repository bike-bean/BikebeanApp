package de.bikebean.app.ui.map;

import android.app.Application;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.status.StateViewModel;
import de.bikebean.app.ui.status.location.LocationUrl;

public class MapFragmentViewModel extends StateViewModel {

    private final LiveData<List<State>> mStatusNumberCellTowers;
    private final LiveData<List<State>> mStatusNumberWifiAccessPoints;
    private final LiveData<List<State>> mConfirmedLocationLat;
    private final LiveData<List<State>> mConfirmedLocationLng;
    private final LiveData<List<State>> mConfirmedLocationAcc;

    public MapFragmentViewModel(Application application) {
        super(application);

        MapStateRepository mRepository = new MapStateRepository(application);

        mStatusNumberCellTowers = mRepository.getStatusNumberCellTowers();
        mStatusNumberWifiAccessPoints = mRepository.getStatusNumberWifiAccessPoints();
        mConfirmedLocationLat = mRepository.getConfirmedLocationLat();
        mConfirmedLocationLng = mRepository.getConfirmedLocationLng();
        mConfirmedLocationAcc = mRepository.getConfirmedLocationAcc();
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

    LiveData<List<State>> getStatusNumberWifiAccessPoints() {
        return mStatusNumberWifiAccessPoints;
    }

    LiveData<List<State>> getStatusNumberCellTowers() {
        return mStatusNumberCellTowers;
    }

    public interface Sharer {
        void share(String url);
    }

    public void newShareIntent(Fragment fragment, Sharer sharer) {
        LocationUrl locationUrl = new LocationUrl();

        getConfirmedLocationLat().observe(fragment.getViewLifecycleOwner(), locationUrl::setLat);
        getConfirmedLocationLng().observe(fragment.getViewLifecycleOwner(), locationUrl::setLng);
        locationUrl.getUrl().observe(fragment.getViewLifecycleOwner(), sharer::share);
    }
}