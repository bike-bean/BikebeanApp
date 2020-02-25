package de.bikebean.app.ui.status.menu.log;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.MutableObject;
import de.bikebean.app.db.log.Log;

public class LogViewModel extends AndroidViewModel {

    private final LogRepository mRepository;

    public LogViewModel(Application application) {
        super(application);

        mRepository = new LogRepository(application);
    }

    void setInternal(String level) {
        mRepository.insert(new Log(level, Log.LEVEL.INTERNAL));
    }

    public void d(String message) {
        mRepository.insert(new Log(message, Log.LEVEL.DEBUG));
    }

    /*
    public void i(String message) {
        mRepository.insert(new Log(message, Log.LEVEL.INFO));
    }
    */

    public void w(String message) {
        mRepository.insert(new Log(message, Log.LEVEL.WARNING));
    }

    public void e(String message) {
        mRepository.insert(new Log(message, Log.LEVEL.ERROR));
    }

    LiveData<List<Log>> getHigherThanLevel(Log.LEVEL level) {
        return mRepository.getHigherThanLevel(level);
    }

    LiveData<List<Log>> getLastLevel() {
        return mRepository.getLastLevel();
    }

    Log.LEVEL getLastLevelSync() {
        Log log = getLastLog();
        if (log == null)
            return Log.LEVEL.ERROR;
        else
            return Log.LEVEL.valueOf(log.getMessage());
    }

    private Log getLastLog() {
        MutableObject<Log> log = new MutableObject<>(new Log());

        return (Log) log.getDbEntitySync(mRepository::getLastLevelSync, "", 0);
    }
}
