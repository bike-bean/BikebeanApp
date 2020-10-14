package de.bikebean.app.db.settings.settings.number_settings;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import de.bikebean.app.db.settings.settings.NumberSetting;
import de.bikebean.app.db.settings.settings.WappState;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.utils.sms.parser.SmsParser;

public class CellTowers extends NumberSetting {

    private static final @NonNull State.KEY key = State.KEY.CELL_TOWERS;
    private static final @NonNull State.KEY numberKey = State.KEY.NO_CELL_TOWERS;

    private final @NonNull CellTowerList cellTowerList;
    private final int number;
    private final @NonNull State numberState;

    public static class CellTowerList extends ArrayList<CellTower> {
        CellTowerList(final @NonNull String[] stringArrayWapp) {
            parse(stringArrayWapp);
        }

        private void parse(final @NonNull String[] stringArrayWapp) {
            for (final @NonNull String s : stringArrayWapp)
                if (!s.equals("    ")) {
                    final @NonNull String[] stringArray_gsm_towers = s.split(",");
                    final @NonNull CellTower c = new CellTower();

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

    public CellTowers(final @NonNull SmsParser smsParser) {
        super(smsParser.getWappCellTowers(), smsParser.getSms(), key);

        final @NonNull String[] strings = mWappString.split("\n");
        number = strings.length;

        cellTowerList = new CellTowerList(strings);
        numberState = new State(
                getDate(), numberKey,
                (double) getNumber(), "",
                State.STATUS.CONFIRMED, getId()
        );
    }

    public CellTowers(final @NonNull WappState wappState) {
        super(wappState.getCellTowers().getLongValue(), wappState.getSms(), key);

        final @NonNull String[] strings = mWappString.split("\n");
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
