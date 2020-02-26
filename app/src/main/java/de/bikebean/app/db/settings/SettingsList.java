package de.bikebean.app.db.settings;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateList;

public class SettingsList extends ArrayList<Setting> {

    public SettingsList _add(Setting t) {
        super.add(t);
        return this;
    }

    @NonNull
    @Override
    public Setting[] toArray() {
        return super.toArray(new Setting[]{});
    }

    public State[] updatePreferences() {
        /*
        Basically takes a list of Settings and converts them into a list of State's.

        This happens through the magical properties of "Setting", which connects
        Settings to State's.
         */
        StateList newStateEntries = new StateList();

        for (Setting setting : this)
            if (setting.getDate() != 0)
                setting.addStatusEntry(newStateEntries);

        return newStateEntries.toArray();
    }

    public void addToConversationList(SettingsList conversationList) {
        // update the conversationList, checking if the information from the SMS
        // is newer than already stored information.
        for (Setting smsSetting : this)
            smsSetting.addToConversationList(conversationList);
    }
}
