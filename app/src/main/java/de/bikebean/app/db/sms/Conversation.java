package de.bikebean.app.db.sms;

import java.util.ArrayList;
import java.util.List;

import de.bikebean.app.db.settings.Battery;
import de.bikebean.app.db.settings.CellTowers;
import de.bikebean.app.db.settings.Interval;
import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.settings.Status;
import de.bikebean.app.db.settings.WarningNumber;
import de.bikebean.app.db.settings.Wifi;
import de.bikebean.app.db.settings.WifiAccessPoints;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.status.StateViewModel;
import de.bikebean.app.ui.status.sms.parser.SmsParser;

public class Conversation {

    private StateViewModel stateViewModel;

    private List<Setting> internalList = new ArrayList<>();

    public Conversation(StateViewModel stateViewModel) {
        this.stateViewModel = stateViewModel;

        Sms nullSms = new Sms(0, "", "", 0, 0, "", 0);

        internalList.add(new Wifi(false, nullSms));
        internalList.add(new Battery(0.0, nullSms));
        internalList.add(new Interval(0, nullSms));
        internalList.add(new WarningNumber("", nullSms));
        internalList.add(new Status(0.0, nullSms));
        internalList.add(new CellTowers("", nullSms));
        internalList.add(new WifiAccessPoints("", nullSms));
    }

    public void add(Sms sms) {
        SmsParser smsParser = new SmsParser(sms);
        List<Setting> smsSettingList = smsParser.getSettingList();

        // update the internal list, checking if the information from the SMS
        // is newer than already stored information.
        for (Setting smsSetting : smsSettingList)
            for (Setting internalListItem : internalList)
                if (internalListItem.getKey().equals(smsSetting.getKey())) {
                    if (internalListItem.getDate() < smsSetting.getDate()) {
                        internalList.add(smsSetting);
                        internalList.remove(internalListItem);
                    }
                    break;
                }
    }

    public void updatePreferences() throws InterruptedException {
        for(State state : internalList.get((0)).updatePreferences(internalList)) {
            this.stateViewModel.insert(state);
            Thread.sleep(10);
        }
    }
}
