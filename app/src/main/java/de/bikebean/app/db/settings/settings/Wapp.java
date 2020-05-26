package de.bikebean.app.db.settings.settings;

import java.util.List;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.settings.settings.number_settings.CellTowers;
import de.bikebean.app.db.settings.settings.number_settings.WifiAccessPoints;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.main.status.location.LocationStateViewModel;

public class Wapp extends Setting {

    private final double wapp;

    public Wapp(double wapp, Sms sms) {
        super(sms, State.KEY.WAPP);
        conversationListAdder = super::addToList;
        stateGetter = super::getStatePending;

        this.wapp= wapp;
    }

    public Wapp() {
        super(new Sms(), State.KEY.WAPP);
        conversationListAdder = super::addToList;
        stateGetter = super::getStatePending;

        this.wapp = 0.0;
    }

    @Override
    public Double get() {
        return wapp;
    }

    private State wifiAccessPoints = null;
    private State cellTowers = null;

    public boolean getWappState(LocationStateViewModel st, List<State> states) {
        if (states.size() == 0)
            return false;

        if (states.size() > 1) {
            for (State s : states)
                if (s.getValue() == State.WAPP_CELL_TOWERS) {
                    cellTowers = st.getCellTowersByWappSync(s);
                    break;
                }
            for (State s : states)
                if (s.getValue() == State.WAPP_WIFI_ACCESS_POINTS) {
                    wifiAccessPoints = st.getWifiAccessPointsByWappSync(s);
                    break;
                }
        }

        if (cellTowers == null || wifiAccessPoints == null)
            return false;

        setSms(new Sms(cellTowers));
        return true;
    }

    public boolean getIfNewest(LocationStateViewModel st) {
        return wifiAccessPoints.equals(st.getConfirmedLocationSync(wifiAccessPoints))
                && cellTowers.equals(st.getConfirmedLocationSync(cellTowers));
    }

    public Sms getSms() {
        return super.getSms();
    }

    public State getCellTowers() {
        return cellTowers;
    }

    public State getWifiAccessPoints() {
        return wifiAccessPoints;
    }

    public CellTowers getCellTowerSetting(Sms sms) {
        return new CellTowers(this, sms);
    }

    public WifiAccessPoints getWifiAccessPointSetting(Sms sms) {
        return new WifiAccessPoints(this, sms);
    }

    public int getSmsId() {
        return cellTowers.getSmsId();
    }

}
