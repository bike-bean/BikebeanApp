package de.bikebean.app.db.type.types.sms_parser_types;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.settings.settings.Wapp;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.type.types.SmsParserType;
import de.bikebean.app.ui.utils.sms.parser.SmsParser;

public class CellTowers extends SmsParserType {

    private final List<Setting> settings;

    public CellTowers(@NonNull SmsParser smsParser) {
        super(SMSTYPE.CELL_TOWERS);
        this.mSmsParser = smsParser;
        this.settings = new ArrayList<>();

        // no battery entry in this special case
        settings.add(new de.bikebean.app.db.settings.settings.number_settings.CellTowers(mSmsParser));
        settings.add(new Wapp(State.WAPP_CELL_TOWERS, smsParser));
    }

    @Override
    public List<Setting> getSettings() {
        return settings;
    }
}
