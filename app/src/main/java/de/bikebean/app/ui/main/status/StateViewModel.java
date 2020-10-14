package de.bikebean.app.ui.main.status;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

import de.bikebean.app.db.MutableObject;
import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.settings.settings.NumberSetting;
import de.bikebean.app.db.settings.settings.WappState;
import de.bikebean.app.db.settings.settings.number_settings.CellTowers;
import de.bikebean.app.db.settings.settings.number_settings.WifiAccessPoints;
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.Interval;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.type.Type;
import de.bikebean.app.ui.utils.sms.send.SmsSender;

public class StateViewModel extends AndroidViewModel {

    private final @NonNull StateRepository mRepository;

    public StateViewModel(final @NonNull Application application) {
        super(application);

        mRepository = new StateRepository(application);
    }

    public void insert(final @Nullable State state) {
        if (state != null)
            mRepository.insert(state);
    }

    public void insert(final @NonNull SmsSender smsSender) {
        for (State state : smsSender.getUpdates())
            if (state != null)
                mRepository.insert(state);
    }

    private void insert(final @NonNull NumberSetting numberSetting) {
        insert(numberSetting.getNumberState());
    }

    public void insert(final @NonNull Setting setting) {
        insert(setting.getState());
    }

    public void insert(final @NonNull List<Setting> settings) {
        for (final @NonNull Setting s : settings)
            insert(s);
    }

    public void insert(final @NonNull Type type) {
        insert(type.getSettings());
    }

    public void insertNumberStates(final @NonNull WappState wappState) {
        insert(new CellTowers(wappState));
        insert(new WifiAccessPoints(wappState));
    }

    public int getConfirmedIntervalSync() {
        final @Nullable State intervalConfirmed = getConfirmedStateSync(State.KEY.INTERVAL);

        if (intervalConfirmed != null)
            return intervalConfirmed.getValue().intValue();

        return new Interval().get().intValue();
    }

    public @NonNull String getWifiAccessPointsSync() {
        final @Nullable State wifiAccessPoints = getConfirmedStateSync(State.KEY.WIFI_ACCESS_POINTS);

        if (wifiAccessPoints != null)
            return wifiAccessPoints.getLongValue();
        else
            return "";
    }

    protected @Nullable State getConfirmedStateSync(State.KEY key) {
        return getStateSync(mRepository::getConfirmedStateSync, key, 0);
    }

    protected @Nullable State getLastStateSync(State.KEY key) {
        return getStateSync(mRepository::getLastStateSync, key, 0);
    }

    protected @Nullable State getStateByIdSync(State.KEY key, int smsId) {
        return getStateSync(mRepository::getStateByIdSync, key, smsId);
    }

    private @Nullable State getStateSync(final @NonNull MutableObject.ListGetter stateGetter,
                                         final @NonNull State.KEY key, int smsId) {
        final @NonNull MutableObject<State> state = new MutableObject<>(new State());

        return (State) state.getDbEntitySync(stateGetter, key.get(), smsId);
    }
}
