package de.bikebean.app.ui.status;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.BikeBeanRoomDatabase;
import de.bikebean.app.db.status.Status;
import de.bikebean.app.db.status.StatusDao;

class StatusRepository {

    private final StatusDao mStatusDao;
    private final LiveData<List<Status>> mStatusBattery;
    private final LiveData<List<Status>> mStatusLocationLat;
    private final LiveData<List<Status>> mStatusLocationLng;
    private final LiveData<List<Status>> mStatusLocationAcc;
    private final LiveData<List<Status>> mStatusNumberCellTowers;
    private final LiveData<List<Status>> mStatusNumberWifiAccessPoints;

    StatusRepository(Application application) {
        BikeBeanRoomDatabase db = BikeBeanRoomDatabase.getDatabase(application);
        mStatusDao = db.statusDao();

        mStatusBattery = mStatusDao.getAllByKey("battery");
        mStatusLocationLat = mStatusDao.getAllByKey("lat");
        mStatusLocationLng = mStatusDao.getAllByKey("lng");
        mStatusLocationAcc = mStatusDao.getAllByKey("acc");
        mStatusNumberCellTowers = mStatusDao.getAllByKey("numberCellTowers");
        mStatusNumberWifiAccessPoints = mStatusDao.getAllByKey("numberWifiAccessPoints");
    }

    LiveData<List<Status>> getStatusBattery() {
        return mStatusBattery;
    }

    LiveData<List<Status>> getStatusLocationLat() {
        return mStatusLocationLat;
    }

    LiveData<List<Status>> getStatusLocationLng() {
        return mStatusLocationLng;
    }

    LiveData<List<Status>> getStatusLocationAcc() {
        return mStatusLocationAcc;
    }

    LiveData<List<Status>> getStatusNumberCellTowers() {
        return mStatusNumberCellTowers;
    }

    LiveData<List<Status>> getStatusNumberWifiAccessPoints() {
        return mStatusNumberWifiAccessPoints;
    }

    List<Status> getCellTowers() {
        return mStatusDao.getByKey("cellTowers");
    }

    List<Status> getWifiAccessPoints() {
        return mStatusDao.getByKey("wifiAccessPoints");
    }

    List<Status> getLng() {
        return mStatusDao.getByKey("lng");
    }

    List<Status> getBattery() {
        return mStatusDao.getByKey("battery");
    }

    void insert(final Status status) {
        BikeBeanRoomDatabase.databaseWriteExecutor.execute(() -> mStatusDao.insert(status));
    }
}
