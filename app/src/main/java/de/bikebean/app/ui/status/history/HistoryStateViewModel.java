package de.bikebean.app.ui.status.history;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import de.bikebean.app.db.state.LocationState;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.status.StateViewModel;

public class HistoryStateViewModel extends StateViewModel {

    private final HistoryStateRepository mRepository;

    private final MutableLiveData<List<LocationState>> mLocationStates;

    public HistoryStateViewModel(Application application) {
        super(application);

        mRepository = new HistoryStateRepository(application);

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
