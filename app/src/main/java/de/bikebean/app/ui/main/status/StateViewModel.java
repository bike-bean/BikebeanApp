package de.bikebean.app.ui.main.status;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

import de.bikebean.app.db.MutableObject;
import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.settings.settings.WappState;
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.Interval;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.type.Type;

public class StateViewModel extends AndroidViewModel {

    private final StateRepository mRepository;

    public StateViewModel(Application application) {
        super(application);

        mRepository = new StateRepository(application);
    }

    public void insert(State state) {
        if (state != null)
            mRepository.insert(state);
    }

    public void insert(@NonNull State[] states) {
        for (State state : states)
            if (state != null)
                mRepository.insert(state);
    }

    public void insert(@NonNull Setting setting) {
        insert(setting.getState());
    }

    public void insertNumberStates(@NonNull WappState wappState, Sms sms) {
        insert(wappState.getCellTowerSetting(sms).getNumberState());
        insert(wappState.getWifiAccessPointSetting(sms).getNumberState());
    }

    public void insert(@NonNull List<Setting> settings) {
        for (Setting s : settings)
            insert(s);
    }

    public void insert(@NonNull Type type) {
        insert(type.getSettings());
    }

    public int getConfirmedIntervalSync() {
        State intervalConfirmed = getConfirmedStateSync(State.KEY.INTERVAL);

        if (intervalConfirmed != null)
            return intervalConfirmed.getValue().intValue();

        return new Interval().get().intValue();
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

    private State getStateSync(MutableObject.ListGetter stateGetter, @NonNull State.KEY key, int smsId) {
        final MutableObject<State> state = new MutableObject<>(new State());

        return (State) state.getDbEntitySync(stateGetter, key.get(), smsId);
    }
}
