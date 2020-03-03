package de.bikebean.app.db.settings.settings.number_settings;

import java.util.ArrayList;
import java.util.List;

import de.bikebean.app.db.settings.settings.NumberSetting;
import de.bikebean.app.db.settings.settings.Wapp;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class CellTowers extends NumberSetting {

    private static final State.KEY key = State.KEY.CELL_TOWERS;
    private static final State.KEY numberKey = State.KEY.NO_CELL_TOWERS;

    private CellTowerList cellTowerList;

    public class CellTowerList extends ArrayList<CellTower> {}

    private class CellTower extends RawNumberSettings {
        Integer mobileCountryCode;
        Integer mobileNetworkCode;
        Integer locationAreaCode;
        Integer cellId;
        Integer signalStrength;
    }

    private final String cellTowers;

    public CellTowers(String cellTowers, Sms sms) {
        super(sms, key, numberKey, cellTowers);
        this.cellTowers = cellTowers;
    }

    public CellTowers(Wapp wapp, Sms sms) {
        super(sms, key, numberKey, wapp.getCellTowers().getLongValue());
        this.cellTowers = wapp.getCellTowers().getLongValue();
    }

    public CellTowers() {
        super(key);

        this.cellTowers = "";
    }

    @Override
    protected void initList() {
        cellTowerList = new CellTowerList();
    }

    @Override
    protected void parseSplitString(String cellTowers) {
        stringArrayWapp = cellTowers.split("\n");
    }

    @Override
    protected void parseNumber() {
        number = stringArrayWapp.length;
    }

    @Override
    protected void parse(String cellTowers) {
        for (String s : stringArrayWapp)
            if (!s.equals("    ")) {
                String[] stringArray_gsm_towers = s.split(",");
                CellTower c = new CellTower();

                c.mobileCountryCode = Integer.parseInt(stringArray_gsm_towers[0]);
                c.mobileNetworkCode = Integer.parseInt(stringArray_gsm_towers[1]);
                c.locationAreaCode = Integer.parseInt(stringArray_gsm_towers[2], 16);
                c.cellId = Integer.parseInt(stringArray_gsm_towers[3], 16);
                c.signalStrength = Integer.parseInt("-" + stringArray_gsm_towers[4]);

                cellTowerList.add(c);
            }
    }

    @Override
    public List<? extends RawNumberSettings> getList() {
        return cellTowerList;
    }

    @Override
    public String get() {
        return cellTowers;
    }
}
