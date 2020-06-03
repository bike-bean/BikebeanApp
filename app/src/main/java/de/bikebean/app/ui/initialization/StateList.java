package de.bikebean.app.ui.initialization;

import java.util.ArrayList;
import java.util.List;

import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.main.status.location.LocationStateViewModel;

public class StateList extends ArrayList<State> {

    public StateList(List<State> a) {
        super(a);
    }

    public State getCellTowersState(LocationStateViewModel st) {
        if (isEmpty())
            return new State();

        for (State s : this)
            if (s != null && s.getIsWappCellTowers())
                return st.getCellTowersByWappSync(s);

        return new State();
    }

    public State getWifiAccessPointsState(LocationStateViewModel st) {
        if (isEmpty())
            return new State();

        for (State s : this)
            if (s != null && s.getIsWappWifiAccessPoints())
                return st.getWifiAccessPointsByWappSync(s);

        return new State();
    }
}
