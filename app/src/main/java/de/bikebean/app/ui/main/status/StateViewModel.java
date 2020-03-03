package de.bikebean.app.ui.main.status;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;

import de.bikebean.app.R;
import de.bikebean.app.db.MutableObject;
import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.settings.settings.Battery;
import de.bikebean.app.db.settings.settings.Interval;
import de.bikebean.app.db.settings.settings.Location;
import de.bikebean.app.db.settings.settings.Status;
import de.bikebean.app.db.settings.settings.Wapp;
import de.bikebean.app.db.settings.settings.Wifi;
import de.bikebean.app.db.settings.settings.number_settings.CellTowers;
import de.bikebean.app.db.settings.settings.number_settings.WifiAccessPoints;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.initialization.SettingsList;

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

    public void insert(State[] states) {
        for (State state : states)
            if (state != null)
                mRepository.insert(state);
    }

    public void insert(Setting setting) {
        if (setting.getDate() != 0)
            insert(setting.getState());
    }

    public void insertNumberStates(Wapp wapp, Sms sms) {
        insert(wapp.getCellTowerSetting(sms).getNumberState());
        insert(wapp.getWifiAccessPointSetting(sms).getNumberState());
    }

    public void insert(SettingsList settings) {
        for (Setting s : settings)
            insert(s);
    }

    public void insertInitialStates(Context ctx) {
        insert(new State(
                1, State.KEY.WARNING_NUMBER, 0.0, ctx.getString(R.string.warning_number_default),
                State.STATUS.UNSET, 0)
        );

        SettingsList settings = new SettingsList();

        settings._add(new Interval())
                ._add(new Wifi())
                ._add(new Status())
                ._add(new Battery())
                ._add(new Location())
                ._add(new CellTowers())
                ._add(new WifiAccessPoints());

        insert(settings);
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

    private State getStateSync(MutableObject.ListGetter stateGetter, State.KEY key, int smsId) {
        final MutableObject<State> state = new MutableObject<>(new State());

        return (State) state.getDbEntitySync(stateGetter, key.get(), smsId);
    }
}
