package de.bikebean.app.ui.drawer.log;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.MainActivity;
import de.bikebean.app.db.MutableObject;
import de.bikebean.app.db.log.Log;

public class LogViewModel extends AndroidViewModel {

    private final @NonNull LogRepository mRepository;

    public LogViewModel(final @NonNull Application application) {
        super(application);

        mRepository = new LogRepository(application);
    }

    void setInternal(final @NonNull String level) {
        mRepository.insert(new Log(level, Log.LEVEL.INTERNAL));
    }

    public void d(final @NonNull String message) {
        android.util.Log.d(MainActivity.TAG, message);
        mRepository.insert(new Log(message, Log.LEVEL.DEBUG));
    }

    public void i(final @NonNull String message) {
        android.util.Log.i(MainActivity.TAG, message);
        mRepository.insert(new Log(message, Log.LEVEL.INFO));
    }

    public void w(final @NonNull String message) {
        android.util.Log.w(MainActivity.TAG, message);
        mRepository.insert(new Log(message, Log.LEVEL.WARNING));
    }

    public void e(final @NonNull String message) {
        android.util.Log.e(MainActivity.TAG, message);
        mRepository.insert(new Log(message, Log.LEVEL.ERROR));
    }

    LiveData<List<Log>> getHigherThanLevel(final @NonNull Log.LEVEL level) {
        return mRepository.getHigherThanLevel(level);
    }

    LiveData<List<Log>> getLastLevel() {
        return mRepository.getLastLevel();
    }

    Log.LEVEL getLastLevelSync() {
        final @Nullable Log log = getLastLog();
        if (log == null)
            return Log.LEVEL.ERROR;
        else
            return Log.LEVEL.valueOf(log.getMessage());
    }

    private @Nullable Log getLastLog() {
        final @NonNull MutableObject log = new MutableObject();

        return (Log) log.getDbEntitySync(mRepository::getLastLevelSync, "", 0);
    }
}
