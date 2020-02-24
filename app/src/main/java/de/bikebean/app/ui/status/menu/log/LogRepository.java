package de.bikebean.app.ui.status.menu.log;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.BikeBeanRoomDatabase;
import de.bikebean.app.db.log.Log;
import de.bikebean.app.db.log.LogDao;

class LogRepository {

    private final LogDao mLogDao;

    LogRepository(Application application) {
        BikeBeanRoomDatabase db = BikeBeanRoomDatabase.getDatabase(application);
        mLogDao = db.logDao();
    }

    void insert(final Log log) {
        BikeBeanRoomDatabase.databaseWriteExecutor.execute(() -> mLogDao.insert(log));
    }

    LiveData<List<Log>> getAll() {
        return mLogDao.getAll();
    }
}
