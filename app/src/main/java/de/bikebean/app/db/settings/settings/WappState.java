package de.bikebean.app.db.settings.settings;

import androidx.annotation.NonNull;

import de.bikebean.app.db.sms.SmsFactory;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.initialization.StateList;
import de.bikebean.app.ui.drawer.status.location.LocationStateViewModel;

public class WappState extends Wapp {

    private final @NonNull State wifiAccessPoints;
    private final @NonNull State cellTowers;

    public WappState(final LocationStateViewModel st, final @NonNull StateList states) {
        super(SmsFactory.createSmsFromState(states.getCellTowersState(st)), 0.0);

        wifiAccessPoints = states.getWifiAccessPointsState(st);
        cellTowers = states.getCellTowersState(st);
    }

    public int getSmsId() {
        return cellTowers.getSmsId();
    }

    public boolean getIsNull() {
        return cellTowers.getIsNull() || wifiAccessPoints.getIsNull();
    }

    public @NonNull State getCellTowers() {
        return cellTowers;
    }

    public @NonNull State getWifiAccessPoints() {
        return wifiAccessPoints;
    }

    public boolean getIfNewest(final @NonNull LocationStateViewModel st) {
        return (wifiAccessPoints.equalsId(st.getConfirmedLocationSync(wifiAccessPoints))
                && cellTowers.equalsId(st.getConfirmedLocationSync(cellTowers)));
    }
}
