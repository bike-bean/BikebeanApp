package de.bikebean.app.ui.drawer.history.position;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import de.bikebean.app.db.state.LocationState;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.drawer.history.HistoryViewModel;

public class PositionHistoryViewModel extends HistoryViewModel {

    private final @NonNull PositionHistoryRepository mRepository;

    private final MutableLiveData<List<LocationState>> mLocationStates;

    public PositionHistoryViewModel(final @NonNull Application application) {
        super(application);

        mRepository = new PositionHistoryRepository(application);
        mLocationStates = new MutableLiveData<>();
    }

    @NonNull List<State> getAllLocation(int smsId) {
        return mRepository.getAllLocationByIdSync(smsId);
    }

    void setLocationsState(final @NonNull List<LocationState> locationStates) {
        mLocationStates.postValue(locationStates);
    }

    LiveData<List<LocationState>> getLocationStates() {
        return mLocationStates;
    }
}
