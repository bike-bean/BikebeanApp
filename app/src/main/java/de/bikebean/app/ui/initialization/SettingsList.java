package de.bikebean.app.ui.initialization;

import java.util.ArrayList;

import de.bikebean.app.db.settings.Setting;

public class SettingsList extends ArrayList<Setting> {

    public SettingsList _add(Setting t) {
        super.add(t);
        return this;
    }

    void addToConversationList(SettingsList conversationList) {
        // update the conversationList, checking if the information from the SMS
        // is newer than already stored information.
        for (Setting smsSetting : this)
            smsSetting.addToConversationList(conversationList);
    }
}
