package de.bikebean.app.db.settings;

import androidx.annotation.NonNull;

import java.util.List;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public abstract class Setting {

    public interface ConversationListAdder {
        void addToConversationList(List<Setting> conversationList);
    }

    public abstract ConversationListAdder getConversationListAdder();
    public abstract State getState();
    public abstract Object get();

    private Sms sms;
    private final State.KEY key;

    public Setting(Sms sms, State.KEY key) {
        this.sms = sms;
        this.key = key;
    }

    // setters / getters
    protected void setSms(Sms sms) {
        this.sms = sms;
    }

    public long getDate() {
        return sms.getTimestamp();
    }

    protected int getId() {
        return sms.getId();
    }

    public Sms getSms() {
        return sms;
    }

    // interface methods
    public void addToList(@NonNull List<Setting> conversationList) {
        conversationList.add(this);
    }

    public void replaceIfNewer(@NonNull List<Setting> conversationList) {
        for (Setting intListItem : conversationList)
            if (equalsKey(intListItem) && isNewer(intListItem)) {
                conversationList.add(this);
                conversationList.remove(intListItem);
                break;
            }
    }

    // utils
    private boolean equalsKey(@NonNull Setting other) {
        return this.key == other.key;
    }

    private boolean isNewer(@NonNull Setting other) {
        return this.getDate() > other.getDate();
    }
}
