package de.bikebean.app.ui.initialization;

import de.bikebean.app.db.settings.settings.Interval;
import de.bikebean.app.db.settings.settings.Status;
import de.bikebean.app.db.settings.settings.WarningNumber;
import de.bikebean.app.db.settings.settings.Wifi;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.ui.main.status.StateViewModel;
import de.bikebean.app.ui.main.status.menu.log.LogViewModel;
import de.bikebean.app.ui.main.status.menu.sms_history.SmsViewModel;
import de.bikebean.app.ui.utils.sms.parser.SmsParser;

public class Conversation {

    private final StateViewModel stateViewModel;
    private final SmsViewModel smsViewModel;
    private final LogViewModel logViewModel;

    private final SettingsList settings = new SettingsList();

    public Conversation(StateViewModel st, SmsViewModel sm, LogViewModel lv) {
        this.stateViewModel = st;
        this.smsViewModel = sm;
        this.logViewModel = lv;

        settings._add(new Wifi(false, new Sms()))
                ._add(new Interval(0, new Sms()))
                ._add(new WarningNumber("", new Sms()))
                ._add(new Status(0.0, new Sms()));
    }

    public void add(Sms sms) {
        new SmsParser(sms, logViewModel).getSettingList().addToConversationList(settings);
        smsViewModel.insert(sms);
    }

    public void updatePreferences() {
        logViewModel.d("Inserting " + settings.size() + " Settings");
        stateViewModel.insert(settings);
        logViewModel.d("Done inserting Settings!");
    }
}
