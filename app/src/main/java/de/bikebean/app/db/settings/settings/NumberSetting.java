package de.bikebean.app.db.settings.settings;

import java.util.List;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public abstract class NumberSetting extends Setting {

    public abstract static class RawNumberSettings {}

    protected int number;
    protected String[] stringArrayWapp;

    private State.KEY numberKey;
    private State numberState;

    protected NumberSetting(Sms sms, State.KEY key, State.KEY numberKey, String string) {
        super(sms, key);
        conversationListAdder = super::addToList;
        stateGetter = super::getStateConfirmedLong;

        this.numberKey = numberKey;
        initList();
        parseSplitString(string);
        parseNumber();
        parse(string);

        setNumberState();
    }

    public NumberSetting(State.KEY key) {
        super(new Sms(), key);
        conversationListAdder = super::addToList;
        stateGetter = super::getStateUnsetLong;
    }

    protected abstract void initList();
    protected abstract void parseSplitString(String string);
    protected abstract void parseNumber();
    protected abstract void parse(String string);
    public abstract List<? extends RawNumberSettings> getList();

    public int getNumber() {
        return number;
    }

    private void setNumberState() {
        numberState = new State(
                getDate(), numberKey,
                (double) number, "",
                State.STATUS.CONFIRMED, getId()
        );
    }

    public State getNumberState() {
        return numberState;
    }
}
