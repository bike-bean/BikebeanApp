package de.bikebean.app.db.state;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class StateList extends ArrayList<State> {

    @NonNull
    @Override
    public State[] toArray() {
        return super.toArray(new State[]{});
    }
}
