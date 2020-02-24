package de.bikebean.app.db.sms;

import java.util.ArrayList;
import java.util.List;

import de.bikebean.app.db.settings.Interval;
import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.settings.Status;
import de.bikebean.app.db.settings.WarningNumber;
import de.bikebean.app.db.settings.Wifi;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.status.StateViewModel;
import de.bikebean.app.ui.status.sms.SmsViewModel;
import de.bikebean.app.ui.status.sms.parser.SmsParser;

public class Conversation {

    private final StateViewModel stateViewModel;
    private final SmsViewModel smsViewModel;

    private final List<Setting> internalList = new ArrayList<>();

    public Conversation(StateViewModel stateViewModel, SmsViewModel smsViewModel) {
        this.stateViewModel = stateViewModel;
        this.smsViewModel = smsViewModel;

        Sms nullSms = new Sms(0, "", "", 0, 0, "", 0);

        internalList.add(new Wifi(false, nullSms));
        internalList.add(new Interval(0, nullSms));
        internalList.add(new WarningNumber("", nullSms));
        internalList.add(new Status(0.0, nullSms));
    }

    public void add(Sms sms) {
        SmsParser smsParser = new SmsParser(sms);
        List<Setting> smsSettingList = smsParser.getSettingList();

        // update the internal list, checking if the information from the SMS
        // is newer than already stored information.
        for (Setting smsSetting : smsSettingList)
            if (smsSetting.getKey().equals(State.KEY.WAPP)
                    || smsSetting.getKey().equals(State.KEY.BATTERY)
                    || smsSetting.getKey().equals(State.KEY.WIFI_ACCESS_POINTS)
                    || smsSetting.getKey().equals(State.KEY.CELL_TOWERS))
                internalList.add(smsSetting);
            else
                for (Setting internalListItem : internalList)
                    if (internalListItem.getKey().equals(smsSetting.getKey())) {
                        if (smsSetting.getDate() > internalListItem.getDate()) {
                            internalList.add(smsSetting);
                            internalList.remove(internalListItem);
                        }
                        break;
                    }

        smsViewModel.insert(sms);
    }

    public void updatePreferences() {
        this.stateViewModel.insert(internalList.get((0)).updatePreferences(internalList));
    }
}
