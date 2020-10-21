package de.bikebean.app.db.settings.settings.number_settings;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import de.bikebean.app.db.settings.settings.NumberSetting;
import de.bikebean.app.db.settings.settings.WappState;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateFactory;

public class CellTowers extends NumberSetting {

    private static final @NonNull State.KEY key = State.KEY.CELL_TOWERS;
    private static final @NonNull State.KEY numberKey = State.KEY.NO_CELL_TOWERS;

    public CellTowers(final @NonNull Sms sms, final @NonNull String cellTowers) {
        super(cellTowers, sms, key,
                StateFactory.createNumberState(
                        sms, numberKey,
                        cellTowers.split("\n").length,
                        State.STATUS.CONFIRMED
                ),
                new CellTowerList(cellTowers.split("\n"))
        );
    }

    public CellTowers(final @NonNull Sms sms, final @NonNull CellTowersGetter cellTowersGetter) {
        this(sms, cellTowersGetter.getCellTowers());
    }

    public CellTowers(final @NonNull WappState wappState) {
        this(wappState.getSms(), wappState.getCellTowers().getLongValue());
    }

    public CellTowers() {
        super("", key, StateFactory.createNullState(), new CellTowerList(new String[]{}));
    }

    public interface CellTowersGetter {
        @NonNull String getCellTowers();
    }

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
}
