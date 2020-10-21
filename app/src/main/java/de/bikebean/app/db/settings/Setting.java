package de.bikebean.app.db.settings;

import androidx.annotation.NonNull;

import java.util.List;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.sms.SmsFactory;
import de.bikebean.app.db.state.State;

public abstract class Setting {

    private final State state;

    private final @NonNull ConversationListAdder conversationListAdder;

    public Setting(final @NonNull State state,
                   final @NonNull ConversationListAdder conversationListAdder) {
        this.state = state;
        this.conversationListAdder = conversationListAdder;
    }

    public final @NonNull State getState() {
        return state;
    }

    public @NonNull Sms getSms() {
        return SmsFactory.createSmsFromState(state);
    }

    public final @NonNull ConversationListAdder getConversationListAdder() {
        return conversationListAdder;
    }

    public interface ConversationListAdder {
        void add(final @NonNull List<Setting> conversationList, final @NonNull Setting setting);
    }

    /* interface methods */
    public static void addToList(final @NonNull List<Setting> conversationList,
                                 final @NonNull Setting setting) {
        conversationList.add(setting);
    }

    public static void replaceIfNewer(final @NonNull List<Setting> conversationList,
                                      final @NonNull Setting setting) {
        for (final @NonNull Setting intListItem : conversationList)
            if (setting.equalsKey(intListItem) && setting.isNewerThan(intListItem)) {
                conversationList.add(setting);
                conversationList.remove(intListItem);
                break;
            }
    }

    /* utils */
    private boolean equalsKey(final @NonNull Setting other) {
        return this.getKey() == other.getKey();
    }

    private boolean isNewerThan(final @NonNull Setting other) {
        return this.getSms().getTimestamp() > other.getSms().getTimestamp();
    }

    private @NonNull State.KEY getKey() {
        return State.KEY.getValue(state);
    }
}
