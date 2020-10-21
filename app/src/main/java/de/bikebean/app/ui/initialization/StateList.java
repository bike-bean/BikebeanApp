package de.bikebean.app.ui.initialization;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateFactory;
import de.bikebean.app.ui.drawer.status.location.LocationStateViewModel;

public class StateList extends ArrayList<State> {

    public StateList(final @NonNull List<State> a) {
        super(a);
    }

    public @NonNull State getCellTowersState(final LocationStateViewModel st) {
        if (isEmpty())
            return StateFactory.createNullState();

        for (final @Nullable State s : this)
            if (s != null && s.getIsWappCellTowers()) {
                if (s.isRecent()) {
                    final @Nullable State state = st.getCellTowersByWappSync(s);
                    if (state != null)
                        return state;
                } else
                    st.confirmWapp(s);
            }

        return StateFactory.createNullState();
    }

    public @NonNull State getWifiAccessPointsState(final LocationStateViewModel st) {
        if (isEmpty())
            return StateFactory.createNullState();

        for (final @Nullable State s : this)
            if (s != null && s.getIsWappWifiAccessPoints()) {
                if (s.isRecent()) {
                    final @Nullable State state = st.getWifiAccessPointsByWappSync(s);
                    if (state != null)
                        return state;
                } else
                    st.confirmWapp(s);
            }

        return StateFactory.createNullState();
    }
}
