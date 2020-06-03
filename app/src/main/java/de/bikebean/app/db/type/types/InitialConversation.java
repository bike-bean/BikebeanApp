package de.bikebean.app.db.type.types;

import java.util.ArrayList;
import java.util.List;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.Interval;
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.Status;
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.WarningNumber;
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.Wifi;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.type.Type;

public class InitialConversation extends Type {

    private final List<Setting> settings;

    public InitialConversation() {
        super(SMSTYPE.INITIAL_CONVERSATION);

        settings = new ArrayList<>();

        settings.add(new Interval());
        settings.add(new Wifi());
        settings.add(new Status());
        settings.add(new WarningNumber("", new Sms()));
    }

    @Override
    public List<Setting> getSettings() {
        return settings;
    }
}
