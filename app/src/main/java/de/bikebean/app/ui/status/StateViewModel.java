package de.bikebean.app.ui.status;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import de.bikebean.app.db.state.State;

public class StateViewModel extends AndroidViewModel {

    private final StateRepository mRepository;

    /*
    LiveData
     */
    // Battery
    private final LiveData<List<State>> mStatusBattery;

    // Status
    private final LiveData<List<State>> mStatus;
    private final LiveData<List<State>> mStatusWarningNumber;
    private final LiveData<List<State>> mStatusInterval;
    private final LiveData<List<State>> mStatusWifi;

    // Location
    private final LiveData<List<State>> mStatusLocationLat;
    private final LiveData<List<State>> mStatusLocationLng;
    private final LiveData<List<State>> mStatusLocationAcc;
    private final LiveData<List<State>> mStatusNumberCellTowers;
    private final LiveData<List<State>> mStatusNumberWifiAccessPoints;

    private final LiveData<List<State>> mPendingCellTowers;
    private final LiveData<List<State>> mPendingWifiAccessPoints;

    // Other
    private final MutableLiveData<Boolean> mIntervalAborted;

    public StateViewModel(Application application) {
        super(application);

        mRepository = new StateRepository(application);

        mStatusBattery = mRepository.getStatusBattery();

        mStatus = mRepository.getStatus();
        mStatusWarningNumber = mRepository.getStatusWarningNumber();
        mStatusInterval = mRepository.getStatusInterval();
        mStatusWifi = mRepository.getStatusWifi();

        mStatusLocationLat = mRepository.getStatusLocationLat();
        mStatusLocationLng = mRepository.getStatusLocationLng();
        mStatusLocationAcc = mRepository.getStatusLocationAcc();
        mStatusNumberCellTowers = mRepository.getStatusNumberCellTowers();
        mStatusNumberWifiAccessPoints = mRepository.getStatusNumberWifiAccessPoints();

        mPendingCellTowers = mRepository.getPendingCellTowers();
        mPendingWifiAccessPoints = mRepository.getPendingWifiAccessPoints();

        mIntervalAborted =  new MutableLiveData<>();
    }

    public LiveData<List<State>> getStatus() {
        return mStatus;
    }

    public LiveData<List<State>> getStatusBattery() {
        return mStatusBattery;
    }

    public LiveData<List<State>> getStatusWarningNumber() {
        return mStatusWarningNumber;
    }

    public LiveData<List<State>> getStatusInterval() {
        return mStatusInterval;
    }

    public LiveData<List<State>> getStatusWifi() {
        return mStatusWifi;
    }

    public LiveData<List<State>> getStatusLocationLat() {
        return mStatusLocationLat;
    }

    public LiveData<List<State>> getStatusLocationLng() {
        return mStatusLocationLng;
    }

    public LiveData<List<State>> getStatusLocationAcc() {
        return mStatusLocationAcc;
    }

    public LiveData<List<State>> getStatusNumberWifiAccessPoints() {
        return mStatusNumberWifiAccessPoints;
    }

    public LiveData<List<State>> getStatusNumberCellTowers() {
        return mStatusNumberCellTowers;
    }



    public LiveData<List<State>> getPendingCellTowers() {
        return mPendingCellTowers;
    }

    public LiveData<List<State>> getPendingWifiAccessPoints() {
        return mPendingWifiAccessPoints;
    }



    public List<State> getConfirmedWifi() {
        return mRepository.getConfirmedWifi();
    }

    public List<State> getIntervalConfirmed() {
        return mRepository.getConfirmedInterval();
    }

    /*
    public List<State> getWarningNumber() {
        return mRepository.getWarningNumber();
    }
    */

    public List<State> getInterval() {
        return mRepository.getInterval();
    }

    public List<State> getWifi() {
        return mRepository.getWifi();
    }

    public void insert(State state) {
        if (state != null)
            mRepository.insert(state);
    }

    /*
    public void confirmBySmsId(int smsId) {
        mRepository.confirmBySmsId(smsId);
    }
    */

    public void confirmLocationKeys() {
        mRepository.confirmLocationKeys();
    }

    /*
    public void confirmInterval() {
        mRepository.confirmInterval();
    }

    public void confirmWifi() {
        mRepository.confirmWifi();
    }

    public void pendWifi(int smsId) {
        mRepository.pendWifi(smsId);
    }

    public void deletePendingByKey(final String key) {
        mRepository.deletePendingByKey(key);
    }

    public void deleteUnsetByKey(String key) {
        mRepository.deleteUnsetByKey(key);
    }
    */

    public void notifyIntervalAbort(boolean b) {
        mIntervalAborted.postValue(b);
    }



    public LiveData<Boolean> getIntervalAborted() {
        return mIntervalAborted;
    }
}
