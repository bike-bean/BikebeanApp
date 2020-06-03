package de.bikebean.app.db.settings.settings;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public abstract class ReplaceIfNewerSetting extends Setting {

    public ReplaceIfNewerSetting(Sms sms, State.KEY key) {
        super(sms, key);
    }

    @Override
    public final ConversationListAdder getConversationListAdder() {
        return super::replaceIfNewer;
    }
}