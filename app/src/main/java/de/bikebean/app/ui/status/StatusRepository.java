package de.bikebean.app.ui.status;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
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
    private final LiveData<List<Status>> mPendingCellTowers;
    private final LiveData<List<Status>> mPendingWifiAccessPoints;

    StatusRepository(Application application) {
        BikeBeanRoomDatabase db = BikeBeanRoomDatabase.getDatabase(application);
        mStatusDao = db.statusDao();

        mStatusBattery = mStatusDao.getAllByKey(Status.KEY_BATTERY);
        mStatusLocationLat = mStatusDao.getAllByKey(Status.KEY_LAT);
        mStatusLocationLng = mStatusDao.getAllByKey(Status.KEY_LNG);
        mStatusLocationAcc = mStatusDao.getAllByKey(Status.KEY_ACC);
        mStatusNumberCellTowers = mStatusDao.getAllByKey(Status.KEY_NO_CELL_TOWERS);
        mStatusNumberWifiAccessPoints = mStatusDao.getAllByKey(Status.KEY_NO_WIFI_ACCESS_POINTS);
        mPendingCellTowers = mStatusDao.getByKeyAndState(
                Status.KEY_CELL_TOWERS, Status.STATUS_PENDING);
        mPendingWifiAccessPoints = mStatusDao.getByKeyAndState(
                Status.KEY_WIFI_ACCESS_POINTS, Status.STATUS_PENDING);
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

    LiveData<List<Status>> getPendingCellTowers() {
        return mPendingCellTowers;
    }

    LiveData<List<Status>> getPendingWifiAccessPoints() {
        return mPendingWifiAccessPoints;
    }

    List<Status> getBattery() {
        return mStatusDao.getByKey(Status.KEY_BATTERY);
    }

    void insert(final Status status) {
        BikeBeanRoomDatabase.databaseWriteExecutor.execute(() -> mStatusDao.insert(status));
    }

    void confirmBySmsId(final int smsId) {
        mStatusDao.updateStateBySmsId(Status.STATUS_CONFIRMED, smsId);
    }

    void confirmLocationKeys() {
        mStatusDao.updateStateByKey(Status.STATUS_CONFIRMED, "cellTowers");
        mStatusDao.updateStateByKey(Status.STATUS_CONFIRMED, "wifiAccessPoints");
    }
}
