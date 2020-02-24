package de.bikebean.app.ui.status.menu.log;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.log.Log;

public class LogViewModel extends AndroidViewModel {

    private final LogRepository mRepository;

    public LogViewModel(Application application) {
        super(application);

        mRepository = new LogRepository(application);
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

    LiveData<List<Log>> getAll() {
        return mRepository.getAll();
    }
}
