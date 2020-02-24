package de.bikebean.app.ui.status.menu.history.position;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import de.bikebean.app.db.state.LocationState;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.status.menu.history.HistoryViewModel;

public class PositionHistoryViewModel extends HistoryViewModel {

    private final PositionHistoryRepository mRepository;

    private final MutableLiveData<List<LocationState>> mLocationStates;

    public PositionHistoryViewModel(Application application) {
        super(application);

        mRepository = new PositionHistoryRepository(application);
        mLocationStates = new MutableLiveData<>();
    }

    List<State> getAllLocation(int smsId) {
        return mRepository.getAllLocationByIdSync(smsId);
    }

    void setLocationsState(List<LocationState> locationStates) {
        mLocationStates.postValue(locationStates);
    }

    LiveData<List<LocationState>> getLocationStates() {
        return mLocationStates;
    }
}
