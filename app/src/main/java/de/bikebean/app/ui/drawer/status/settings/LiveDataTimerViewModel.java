package de.bikebean.app.ui.drawer.status.settings;

import android.app.Application;
import android.util.SparseArray;
import android.util.SparseLongArray;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Timer;
import java.util.TimerTask;

public class LiveDataTimerViewModel extends AndroidViewModel {

    public enum TIMER {
        ONE, TWO, THREE, FOUR, FIVE
    }

    private static final int N_TIMERS = 5;

    private static final int ONE_SECOND = 1000;

    private final @NonNull SparseArray<MutableLiveData<Long>> residualTimes = new SparseArray<>();

    private final @NonNull SparseLongArray stopTimes = new SparseLongArray();
    private final @NonNull SparseArray<Timer> timers = new SparseArray<>();

    public LiveDataTimerViewModel(final @NonNull Application application) {
        super(application);

        for (int i=0; i<N_TIMERS; i++) {
            timers.put(i, new Timer());
            residualTimes.put(i, new MutableLiveData<>());
        }
    }

    public long startTimer(final @NonNull TIMER which, long startTime, int interval) {
        timers.put(which.ordinal(), new Timer());
        stopTimes.put(which.ordinal(), startTime + interval * 1000 * 60 * 60);

        timers.get(which.ordinal()).scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                final long newValueSeconds =
                        (stopTimes.get(which.ordinal()) - System.currentTimeMillis()) / (1000);
                residualTimes.get(which.ordinal()).postValue(newValueSeconds);
            }
        }, ONE_SECOND, ONE_SECOND);

        return stopTimes.get(which.ordinal());
    }

    public LiveData<Long> getResidualTime(final @NonNull TIMER which) {
        return residualTimes.get(which.ordinal());
    }

    public void cancelTimer(final @NonNull TIMER which) {
        timers.get(which.ordinal()).cancel();
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        for (int i=0; i<N_TIMERS; i++)
            timers.get(i).cancel();
    }
}
