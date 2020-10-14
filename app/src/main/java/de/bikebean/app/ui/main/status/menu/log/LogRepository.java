package de.bikebean.app.ui.main.status.menu.log;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.BikeBeanRoomDatabase;
import de.bikebean.app.db.log.Log;
import de.bikebean.app.db.log.LogDao;

class LogRepository {

    private final @NonNull LogDao mLogDao;

    LogRepository(final @NonNull Application application) {
        final @NonNull BikeBeanRoomDatabase db = BikeBeanRoomDatabase.getDatabase(application);
        mLogDao = db.logDao();
    }

    void insert(final @NonNull Log log) {
        BikeBeanRoomDatabase.databaseWriteExecutor.execute(() -> mLogDao.insert(log));
    }

    LiveData<List<Log>> getHigherThanLevel(final @NonNull Log.LEVEL level) {
        return mLogDao.getHigherThanLevel(level);
    }

    LiveData<List<Log>> getLastLevel() {
        return mLogDao.getByLevel(Log.LEVEL.INTERNAL);
    }

    @NonNull List<Log> getLastLevelSync(final @NonNull String s, int i) {
        if (i == 0 && s.isEmpty())
            return mLogDao.getByLevelSync(Log.LEVEL.INTERNAL);
        else
            return mLogDao.getByLevelSync(Log.LEVEL.INTERNAL);
    }
}
