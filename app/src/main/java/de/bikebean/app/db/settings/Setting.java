package de.bikebean.app.db.settings;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.initialization.SettingsList;

public abstract class Setting {

    protected interface ConversationListAdder {
        void addToConversationList(SettingsList conversationList);
    }

    protected interface StateGetter {
        State getState();
    }

    protected ConversationListAdder conversationListAdder;
    protected StateGetter stateGetter;

    private Sms sms;
    private final State.KEY key;

    public Setting(Sms sms, State.KEY key) {
        this.sms = sms;
        this.key = key;
    }

    public State getState() {
        return stateGetter.getState();
    }

    public void addToConversationList(SettingsList conversationList) {
        conversationListAdder.addToConversationList(conversationList);
    }

    // simple getters. BEWARE: get() yields no value when called from the Settings-constructor!
    protected abstract Object get();

    private State.KEY getKey() {
        return key;
    }

    public long getDate() {
        return sms.getTimestamp();
    }

    protected int getId() {
        return sms.getId();
    }

    protected Sms getSms() {
        return sms;
    }

    protected void setSms(Sms sms) {
        this.sms = sms;
    }

    // methods called as interface methods
    public State getStateConfirmedLong() {
        return new State(
                getDate(), getKey(), 0.0, (String) get(),
                State.STATUS.CONFIRMED, getId()
        );
    }

    public State getStateConfirmed() {
        return new State(
                getDate(), getKey(), (double) get(), "",
                State.STATUS.CONFIRMED, getId()
        );
    }

    public State getStateConfirmedNewer() {
        return new State(
                getDate() + 1, getKey(), (double) get(), "",
                State.STATUS.CONFIRMED, getId()
        );
    }

    public State getStatePending() {
        return new State(
                getDate(), getKey(), (Double) get(), "",
                State.STATUS.PENDING, getId()
        );
    }

    public State getStateUnsetLong() {
        return new State(
                getDate(), getKey(), 0.0, (String) get(),
                State.STATUS.UNSET, getId()
        );
    }

    public State getStateUnset() {
        return new State(
                getDate(), getKey(), (Double) get(), "",
                State.STATUS.UNSET, getId()
        );
    }

    public void addToList(SettingsList conversationList) {
        conversationList.add(this);
    }

    public void replaceIfNewer(SettingsList conversationList) {
        for (Setting intListItem : conversationList)
            if (equalsKey(intListItem) && isNewer(intListItem)) {
                conversationList._add(this).remove(intListItem);
                break;
            }
    }

    private boolean equalsKey(Setting other) {
        return this.getKey().equals(other.getKey());
    }

    private boolean isNewer(Setting other) {
        return this.getDate() > other.getDate();
    }
}
