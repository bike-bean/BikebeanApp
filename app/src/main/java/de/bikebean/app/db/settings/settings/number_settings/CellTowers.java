package de.bikebean.app.db.settings.settings.number_settings;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import de.bikebean.app.db.settings.settings.NumberSetting;
import de.bikebean.app.db.settings.settings.WappState;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.utils.sms.parser.SmsParser;

public class CellTowers extends NumberSetting {

    private static final State.KEY key = State.KEY.CELL_TOWERS;
    private static final State.KEY numberKey = State.KEY.NO_CELL_TOWERS;

    private final @NonNull CellTowerList cellTowerList;
    private final int number;
    private final @NonNull State numberState;

    public static class CellTowerList extends ArrayList<CellTower> {
        CellTowerList(@NonNull String[] stringArrayWapp) {
            parse(stringArrayWapp);
        }

        private void parse(@NonNull String[] stringArrayWapp) {
            for (String s : stringArrayWapp)
                if (!s.equals("    ")) {
                    String[] stringArray_gsm_towers = s.split(",");
                    CellTower c = new CellTower();

                    c.mobileCountryCode = Integer.parseInt(stringArray_gsm_towers[0]);
                    c.mobileNetworkCode = Integer.parseInt(stringArray_gsm_towers[1]);
                    c.locationAreaCode = Integer.parseInt(stringArray_gsm_towers[2], 16);
                    c.cellId = Integer.parseInt(stringArray_gsm_towers[3], 16);
                    c.signalStrength = Integer.parseInt("-" + stringArray_gsm_towers[4]);

                    this.add(c);
                }
        }
    }

    private static class CellTower extends RawNumberSettings {
        Integer mobileCountryCode;
        Integer mobileNetworkCode;
        Integer locationAreaCode;
        Integer cellId;
        Integer signalStrength;
    }

    public CellTowers(@NonNull SmsParser smsParser) {
        super(smsParser.getWappCellTowers(), smsParser.getSms(), key);

        @NonNull String[] strings = mWappString.split("\n");
        number = strings.length;

        cellTowerList = new CellTowerList(strings);
        numberState = new State(
                getDate(), numberKey,
                (double) getNumber(), "",
                State.STATUS.CONFIRMED, getId()
        );
    }

    public CellTowers(@NonNull WappState wappState) {
        super(wappState.getCellTowers().getLongValue(), wappState.getSms(), key);

        @NonNull String[] strings = mWappString.split("\n");
        number = strings.length;

        cellTowerList = new CellTowerList(strings);
        numberState = new State(
                getDate(), numberKey,
                (double) getNumber(), "",
                State.STATUS.CONFIRMED, getId()
        );
    }

    public CellTowers() {
        super("", key);

        cellTowerList = new CellTowerList(new String[]{});
        number = 0;
        numberState = new State();
    }

    @Override
    public @NonNull List<? extends RawNumberSettings> getList() {
        return cellTowerList;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public @NonNull State getNumberState() {
        return numberState;
    }
}
