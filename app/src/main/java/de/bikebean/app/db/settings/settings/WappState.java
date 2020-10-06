package de.bikebean.app.db.settings.settings;

import androidx.annotation.NonNull;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.initialization.StateList;
import de.bikebean.app.ui.main.status.location.LocationStateViewModel;

public class WappState extends Wapp {

    private final @NonNull State wifiAccessPoints;
    private final @NonNull State cellTowers;

    public WappState(LocationStateViewModel st, @NonNull StateList states) {
        wifiAccessPoints = states.getWifiAccessPointsState(st);
        cellTowers = states.getCellTowersState(st);

        super.setSms(new Sms(cellTowers));
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

    public boolean getIfNewest(@NonNull LocationStateViewModel st) {
        return (wifiAccessPoints.equalsId(st.getConfirmedLocationSync(wifiAccessPoints))
                && cellTowers.equalsId(st.getConfirmedLocationSync(cellTowers)));
    }
}
