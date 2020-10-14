package de.bikebean.app.ui.initialization;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.main.status.location.LocationStateViewModel;

public class StateList extends ArrayList<State> {

    public StateList(final @NonNull List<State> a) {
        super(a);
    }

    public @NonNull State getCellTowersState(final LocationStateViewModel st) {
        if (isEmpty())
            return new State();

        for (final @Nullable State s : this)
            if (s != null && s.getIsWappCellTowers())
                return st.getCellTowersByWappSync(s);

        return new State();
    }

    public @NonNull State getWifiAccessPointsState(final LocationStateViewModel st) {
        if (isEmpty())
            return new State();

        for (final @Nullable State s : this)
            if (s != null && s.getIsWappWifiAccessPoints())
                return st.getWifiAccessPointsByWappSync(s);

        return new State();
    }
}
