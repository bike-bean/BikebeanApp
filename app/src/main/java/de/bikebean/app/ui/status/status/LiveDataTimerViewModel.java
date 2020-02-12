package de.bikebean.app.ui.status.status;

import android.app.Application;
import android.util.SparseArray;
import android.util.SparseLongArray;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Timer;
import java.util.TimerTask;

public class LiveDataTimerViewModel extends AndroidViewModel {

    static final int TIMER_ONE = 0;
    static final int TIMER_TWO = 1;
    static final int TIMER_THREE = 2;
    public static final int TIMER_FOUR = 3;
    public static final int TIMER_FIVE = 4;

    private static final int N_TIMERS = 5;

    private static final int ONE_SECOND = 1000;

    private final SparseArray<MutableLiveData<Long>> residualTimes = new SparseArray<>();

    private final SparseLongArray stopTimes = new SparseLongArray();
    private final SparseArray<Timer> timers = new SparseArray<>();

    public LiveDataTimerViewModel(Application application) {
        super(application);

        for (int i=0; i<N_TIMERS; i++) {
            timers.put(i, new Timer());
            residualTimes.put(i, new MutableLiveData<>());
        }
    }

    public long startTimer(final int which, long startTime, int interval) {
        timers.put(which, new Timer());
        stopTimes.put(which, startTime + interval * 1000 * 60 * 60);

        timers.get(which).scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                final long newValueSeconds =
                        (stopTimes.get(which) - System.currentTimeMillis()) / (1000);
                residualTimes.get(which).postValue(newValueSeconds);
            }
        }, ONE_SECOND, ONE_SECOND);

        return stopTimes.get(which);
    }

    public LiveData<Long> getResidualTime(final int which) {
        return residualTimes.get(which);
    }

    public void cancelTimer(final int which) {
        timers.get(which).cancel();
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        for(int i=0; i<N_TIMERS; i++)
            timers.get(i).cancel();
    }
}
