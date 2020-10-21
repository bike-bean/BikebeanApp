package de.bikebean.app.db.type.types;

import de.bikebean.app.db.settings.settings.replace_if_newer_settings.Interval;
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.Status;
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.WarningNumber;
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.Wifi;
import de.bikebean.app.db.sms.SmsFactory;
import de.bikebean.app.db.type.SmsType;

public class InitialConversation extends SmsType {

    public InitialConversation() {
        super(TYPE.INITIAL_CONVERSATION);

        getSettings().add(new Interval());
        getSettings().add(new Wifi());
        getSettings().add(new Status());
        getSettings().add(new WarningNumber(SmsFactory.createNullSms(), ""));
    }
}
