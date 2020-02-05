package de.bikebean.app.ui.status.status;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Timer;
import java.util.TimerTask;

public class LiveDataTimerViewModel extends AndroidViewModel {

    private static final int ONE_SECOND = 1000;

    private MutableLiveData<Long> mResidualTime1 = new MutableLiveData<>();
    private MutableLiveData<Long> mResidualTime2 = new MutableLiveData<>();

    private long mStopTime1;
    private long mStopTime2;
    private Timer timer1;
    private Timer timer2;

    public LiveDataTimerViewModel(Application application) {
        super(application);

        timer1 = new Timer();
        timer2 = new Timer();
    }

    long startTimer1(long startTime, int interval) {
        mStopTime1 = startTime + interval * 1000 * 60 * 60;
        timer1 = new Timer();

        timer1.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                final long newValueSeconds = (mStopTime1 - System.currentTimeMillis()) / (1000);
                mResidualTime1.postValue(newValueSeconds);
            }
        }, ONE_SECOND, ONE_SECOND);

        return mStopTime1;
    }

    long startTimer2(long startTime, int interval) {
        mStopTime2 = startTime + interval * 1000 * 60 * 60;
        timer2 = new Timer();

        timer2.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                final long newValueSeconds = (mStopTime2 - System.currentTimeMillis()) / (1000);
                mResidualTime2.postValue(newValueSeconds);
            }
        }, ONE_SECOND, ONE_SECOND);

        return mStopTime2;
    }

    LiveData<Long> getResidualTime1() {
        return mResidualTime1;
    }

    LiveData<Long> getResidualTime2() {
        return mResidualTime2;
    }

    void cancelTimer1() {
        timer1.cancel();
    }

    void cancelTimer2() {
        timer2.cancel();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        timer1.cancel();
        timer2.cancel();
    }
}
