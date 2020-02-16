package de.bikebean.app.ui.status;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import de.bikebean.app.R;
import de.bikebean.app.db.MutableObject;
import de.bikebean.app.db.state.State;

public class StateViewModel extends AndroidViewModel {

    protected static final double INITIAL_INTERVAL = 1.0;
    protected static final double INITIAL_WIFI = 0.0;

    private final StateRepository mRepository;

    protected final MutableLiveData<Boolean> mIntervalAborted;

    public StateViewModel(Application application) {
        super(application);

        mRepository = new StateRepository(application);

        mIntervalAborted = new MutableLiveData<>();
    }

    public void insert(State state) {
        if (state != null)
            mRepository.insert(state);
    }

    public void insertInitialStates(Context ctx) {
        insert(new State(
                1, State.KEY_WARNING_NUMBER,
                0.0, ctx.getString(R.string.warning_number_default),
                State.STATUS_UNSET, 0)
        );

        insert(new State(
                1, State.KEY_INTERVAL,
                INITIAL_INTERVAL, "",
                State.STATUS_CONFIRMED, 0)
        );

        insert(new State(
                1, State.KEY_WIFI,
                INITIAL_WIFI, "",
                State.STATUS_CONFIRMED, 0)
        );

        insert(new State(
                1, State.KEY_STATUS,
                0.0, "",
                State.STATUS_UNSET, 0)
        );

        insert(new State(
                1, State.KEY_BATTERY,
                -1.0, "",
                State.STATUS_UNSET, 0)
        );

        insert(new State(
                1, State.KEY_LOCATION,
                0.0, "",
                State.STATUS_UNSET, 0)
        );

        insert(new State(
                1, State.KEY_CELL_TOWERS,
                0.0, "",
                State.STATUS_UNSET, 0)
        );

        insert(new State(
                1, State.KEY_WIFI_ACCESS_POINTS,
                0.0, "",
                State.STATUS_UNSET, 0)
        );
    }

    public void notifyIntervalAbort(boolean b) {
        mIntervalAborted.postValue(b);
    }

    public int getConfirmedIntervalSync() {
        State intervalConfirmed = getConfirmedStateSync(State.KEY_INTERVAL);

        if (intervalConfirmed != null)
            return intervalConfirmed.getValue().intValue();

        return (int) INITIAL_INTERVAL;
    }

    public String getWifiAccessPointsSync() {
        State wifiAccessPoints = getConfirmedStateSync(State.KEY_WIFI_ACCESS_POINTS);

        if (wifiAccessPoints != null)
            return wifiAccessPoints.getLongValue();
        else
            return "";
    }

    protected State getConfirmedStateSync(String key) {
        return getStateSync(mRepository::getConfirmedStateSync, key, 0);
    }

    protected State getLastStateSync(String key) {
        return getStateSync(mRepository::getLastStateSync, key, 0);
    }

    protected State getStateByIdSync(String key, int smsId) {
        return getStateSync(mRepository::getStateByIdSync, key, smsId);
    }

    private State getStateSync(MutableObject.ListGetter stateGetter, String key, int smsId) {
        final MutableObject<State> state =
                new MutableObject<>(new State(State.KEY_BATTERY, 0.0));

        return (State) state.getDbEntitySync(stateGetter, key, smsId);
    }
}
