package de.bikebean.app.db.sms;

import de.bikebean.app.db.settings.Interval;
import de.bikebean.app.db.settings.SettingsList;
import de.bikebean.app.db.settings.Status;
import de.bikebean.app.db.settings.WarningNumber;
import de.bikebean.app.db.settings.Wifi;
import de.bikebean.app.ui.main.status.StateViewModel;
import de.bikebean.app.ui.main.status.menu.log.LogViewModel;
import de.bikebean.app.ui.main.status.menu.sms_history.SmsViewModel;
import de.bikebean.app.ui.utils.sms.parser.SmsParser;

public class Conversation {

    private final StateViewModel stateViewModel;
    private final SmsViewModel smsViewModel;
    private final LogViewModel logViewModel;

    private final SettingsList settingsList = new SettingsList();

    public Conversation(StateViewModel st, SmsViewModel sm, LogViewModel lv) {
        this.stateViewModel = st;
        this.smsViewModel = sm;
        this.logViewModel = lv;

        settingsList._add(new Wifi(false, new Sms()))
                ._add(new Interval(0, new Sms()))
                ._add(new WarningNumber("", new Sms()))
                ._add(new Status(0.0, new Sms()));
    }

    public void add(Sms sms) {
        new SmsParser(sms, logViewModel).getSettingList().addToConversationList(settingsList);
        smsViewModel.insert(sms);
    }

    public void updatePreferences() {
        stateViewModel.insert(settingsList.updatePreferences());
    }
}
