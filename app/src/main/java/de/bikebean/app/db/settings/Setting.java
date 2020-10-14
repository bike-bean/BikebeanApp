package de.bikebean.app.db.settings;

import androidx.annotation.NonNull;

import java.util.List;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public abstract class Setting {

    public interface ConversationListAdder {
        void addToConversationList(final @NonNull List<Setting> conversationList);
    }

    public abstract @NonNull ConversationListAdder getConversationListAdder();
    public abstract @NonNull State getState();
    public abstract @NonNull Object get();

    private @NonNull Sms sms;
    private final @NonNull State.KEY key;

    public Setting(final @NonNull Sms sms, final @NonNull State.KEY key) {
        this.sms = sms;
        this.key = key;
    }

    // setters / getters
    protected void setSms(final @NonNull Sms sms) {
        this.sms = sms;
    }

    public long getDate() {
        return sms.getTimestamp();
    }

    protected int getId() {
        return sms.getId();
    }

    public @NonNull Sms getSms() {
        return sms;
    }

    // interface methods
    public void addToList(final @NonNull List<Setting> conversationList) {
        conversationList.add(this);
    }

    public void replaceIfNewer(final @NonNull List<Setting> conversationList) {
        for (final @NonNull Setting intListItem : conversationList)
            if (equalsKey(intListItem) && isNewer(intListItem)) {
                conversationList.add(this);
                conversationList.remove(intListItem);
                break;
            }
    }

    // utils
    private boolean equalsKey(final @NonNull Setting other) {
        return this.key == other.key;
    }

    private boolean isNewer(final @NonNull Setting other) {
        return this.getDate() > other.getDate();
    }
}
