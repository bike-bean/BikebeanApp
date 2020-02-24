package de.bikebean.app.ui.status;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;

import de.bikebean.app.R;
import de.bikebean.app.db.MutableObject;
import de.bikebean.app.db.state.State;

public class StateViewModel extends AndroidViewModel {

    protected static final double INITIAL_INTERVAL = 1.0;
    protected static final double INITIAL_WIFI = 0.0;

    private final StateRepository mRepository;

    public StateViewModel(Application application) {
        super(application);

        mRepository = new StateRepository(application);
    }

    public void insert(State state) {
        if (state != null)
            mRepository.insert(state);
    }

    public void insert(State[] states) {
        for (State state : states)
            if (state != null)
                mRepository.insert(state);
    }

    public void insertInitialStates(Context ctx) {
        insert(new State(
                1, State.KEY.WARNING_NUMBER, 0.0, ctx.getString(R.string.warning_number_default),
                State.STATUS.UNSET, 0)
        );

        insert(new State(
                1, State.KEY.INTERVAL, INITIAL_INTERVAL, "",
                State.STATUS.CONFIRMED, 0)
        );

        insert(new State(
                1, State.KEY.WIFI, INITIAL_WIFI, "",
                State.STATUS.CONFIRMED, 0)
        );

        insert(new State(
                1, State.KEY._STATUS, 0.0, "",
                State.STATUS.UNSET, 0)
        );

        insert(new State(
                1, State.KEY.BATTERY, -1.0, "",
                State.STATUS.UNSET, 0)
        );

        insert(new State(
                1, State.KEY.LOCATION, 0.0, "",
                State.STATUS.UNSET, 0)
        );

        insert(new State(
                1, State.KEY.CELL_TOWERS, 0.0, "",
                State.STATUS.UNSET, 0)
        );

        insert(new State(
                1, State.KEY.WIFI_ACCESS_POINTS, 0.0, "",
                State.STATUS.UNSET, 0)
        );
    }

    public int getConfirmedIntervalSync() {
        State intervalConfirmed = getConfirmedStateSync(State.KEY.INTERVAL);

        if (intervalConfirmed != null)
            return intervalConfirmed.getValue().intValue();

        return (int) INITIAL_INTERVAL;
    }

    public String getWifiAccessPointsSync() {
        State wifiAccessPoints = getConfirmedStateSync(State.KEY.WIFI_ACCESS_POINTS);

        if (wifiAccessPoints != null)
            return wifiAccessPoints.getLongValue();
        else
            return "";
    }

    protected State getConfirmedStateSync(State.KEY key) {
        return getStateSync(mRepository::getConfirmedStateSync, key, 0);
    }

    protected State getLastStateSync(State.KEY key) {
        return getStateSync(mRepository::getLastStateSync, key, 0);
    }

    protected State getStateByIdSync(State.KEY key, int smsId) {
        return getStateSync(mRepository::getStateByIdSync, key, smsId);
    }

    private State getStateSync(MutableObject.ListGetter stateGetter, State.KEY key, int smsId) {
        final MutableObject<State> state =
                new MutableObject<>(new State(State.KEY.BATTERY, 0.0));

        return (State) state.getDbEntitySync(stateGetter, key.get(), smsId);
    }
}
