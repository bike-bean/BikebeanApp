package de.bikebean.app.ui.drawer.status;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.settings.settings.add_to_list_settings.NumberSetting;
import de.bikebean.app.db.settings.settings.add_to_list_settings.WappState;
import de.bikebean.app.db.settings.settings.add_to_list_settings.number_settings.CellTowers;
import de.bikebean.app.db.settings.settings.add_to_list_settings.number_settings.WifiAccessPoints;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.utils.sms.send.SmsSender;

import static de.bikebean.app.db.settings.settings.replace_if_newer_settings.Interval.INITIAL_INTERVAL;
import static de.bikebean.app.ui.drawer.status.StateViewModelExtKt.getConfirmedStateSync;

public class StateViewModel extends AndroidViewModel {

    final @NonNull StateRepository mRepository;

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

    public void insertNumberStates(final @NonNull WappState wappState) {
        insert(new CellTowers(wappState));
        insert(new WifiAccessPoints(wappState));
    }

    public int getConfirmedIntervalSync() {
        final @Nullable State intervalConfirmed =
                getConfirmedStateSync(this, State.KEY.INTERVAL);

        if (intervalConfirmed != null)
            return intervalConfirmed.getValue().intValue();

        return INITIAL_INTERVAL;
    }

    public boolean getHasPositionSync() {
        return getConfirmedStateSync(this, State.KEY.LAT) != null;
    }

    public boolean isIntervalConfirmedSync() {
        return getConfirmedStateSync(this, State.KEY.INTERVAL) != null;
    }

    public @NonNull String getWarningNumberSync() {
        final @Nullable State warningNumberSync =
                getConfirmedStateSync(this, State.KEY.WARNING_NUMBER);

        if (warningNumberSync != null)
            return warningNumberSync.getLongValue();

        return "";
    }

    public @NonNull String getWifiAccessPointsSync() {
        final @Nullable State wifiAccessPoints =
                getConfirmedStateSync(this, State.KEY.WIFI_ACCESS_POINTS);

        if (wifiAccessPoints != null)
            return wifiAccessPoints.getLongValue();
        else
            return "";
    }

}
