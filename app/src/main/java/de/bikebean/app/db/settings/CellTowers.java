package de.bikebean.app.db.settings;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class CellTowers extends Setting {
    private final String cellTowers;

    public CellTowers(String cellTowers, Sms sms) {
        this.cellTowers = cellTowers;
        this.sms = sms;
        this.key = State.KEY.CELL_TOWERS;
    }

    public String get() {
        return cellTowers;
    }
}
