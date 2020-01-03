package de.bikebean.app.ui.status;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.status.Status;

public class StatusViewModel extends AndroidViewModel {

    private final StatusRepository mRepository;

    private final LiveData<List<Status>> mStatusBattery;
    private final LiveData<List<Status>> mStatusLocationLat;
    private final LiveData<List<Status>> mStatusLocationLng;
    private final LiveData<List<Status>> mStatusLocationAcc;
    private final LiveData<List<Status>> mStatusNumberCellTowers;
    private final LiveData<List<Status>> mStatusNumberWifiAccessPoints;

    public StatusViewModel(Application application) {
        super(application);
        mRepository = new StatusRepository(application);
        mStatusBattery = mRepository.getStatusBattery();
        mStatusLocationLat = mRepository.getStatusLocationLat();
        mStatusLocationLng = mRepository.getStatusLocationLng();
        mStatusLocationAcc = mRepository.getStatusLocationAcc();
        mStatusNumberCellTowers = mRepository.getStatusNumberCellTowers();
        mStatusNumberWifiAccessPoints = mRepository.getStatusNumberWifiAccessPoints();
    }

    LiveData<List<Status>> getStatusBattery() {
        return mStatusBattery;
    }

    public LiveData<List<Status>> getStatusLocationLat() {
        return mStatusLocationLat;
    }

    public LiveData<List<Status>> getStatusLocationLng() {
        return mStatusLocationLng;
    }

    public LiveData<List<Status>> getStatusLocationAcc() {
        return mStatusLocationAcc;
    }

    public LiveData<List<Status>> getStatusNumberWifiAccessPoints() {
        return mStatusNumberWifiAccessPoints;
    }

    public LiveData<List<Status>> getStatusNumberCellTowers() {
        return mStatusNumberCellTowers;
    }

    public List<Status> getCellTowers() {
        return mRepository.getCellTowers();
    }

    public List<Status> getWifiAccessPoints() {
        return mRepository.getWifiAccessPoints();
    }

    public List<Status> getLng() {
        return mRepository.getLng();
    }

    public List<Status> getBattery() {
        return mRepository.getBattery();
    }

    public void insert(Status status) {
        mRepository.insert(status);
    }
}
