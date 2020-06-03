package de.bikebean.app.db.settings;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.initialization.SettingsList;

public abstract class Setting {

    protected enum STATE_TYPE {
        CONFIRMED, CONFIRMED_LONG, CONFIRMED_NEWER, UNSET, UNSET_LONG, PENDING
    }

    protected enum C_LIST_ADD_TYPE {
        ADD_TO_LIST, REPLACE_IF_NEWER
    }

    protected interface ConversationListAdder {
        void addToConversationList(SettingsList conversationList);
    }

    protected interface StateGetter {
        State getState();
    }

    private final ConversationListAdder conversationListAdder;
    private final StateGetter stateGetter;

    private Sms sms;
    private final State.KEY key;

    public Setting(Sms sms, State.KEY key, STATE_TYPE stateType, C_LIST_ADD_TYPE cListAddType) {
        this.sms = sms;
        this.key = key;

        switch (stateType) {
            case CONFIRMED_LONG:
                stateGetter = this::getStateConfirmedLong;
                break;
            case CONFIRMED_NEWER:
                stateGetter = this::getStateConfirmedNewer;
                break;
            case PENDING:
                stateGetter = this::getStatePending;
                break;
            case UNSET:
                stateGetter = this::getStateUnset;
                break;
            case UNSET_LONG:
                stateGetter = this::getStateUnsetLong;
                break;
            case CONFIRMED: // And:
            default:
                stateGetter = this::getStateConfirmed;
        }

        switch (cListAddType) {
            case REPLACE_IF_NEWER:
                conversationListAdder = this::replaceIfNewer;
                break;
            case ADD_TO_LIST: // And
            default:
                conversationListAdder = this::addToList;
        }
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
    private State getStateConfirmedLong() {
        return new State(
                getDate(), getKey(), 0.0, (String) get(),
                State.STATUS.CONFIRMED, getId()
        );
    }

    private State getStateConfirmed() {
        return new State(
                getDate(), getKey(), (double) get(), "",
                State.STATUS.CONFIRMED, getId()
        );
    }

    private State getStateConfirmedNewer() {
        return new State(
                getDate() + 1, getKey(), (double) get(), "",
                State.STATUS.CONFIRMED, getId()
        );
    }

    private State getStatePending() {
        return new State(
                getDate(), getKey(), (Double) get(), "",
                State.STATUS.PENDING, getId()
        );
    }

    private State getStateUnsetLong() {
        return new State(
                getDate(), getKey(), 0.0, (String) get(),
                State.STATUS.UNSET, getId()
        );
    }

    private State getStateUnset() {
        return new State(
                getDate(), getKey(), (Double) get(), "",
                State.STATUS.UNSET, getId()
        );
    }

    private void addToList(SettingsList conversationList) {
        conversationList.add(this);
    }

    private void replaceIfNewer(SettingsList conversationList) {
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
