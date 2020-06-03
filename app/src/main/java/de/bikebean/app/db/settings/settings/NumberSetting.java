package de.bikebean.app.db.settings.settings;

import java.util.List;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public abstract class NumberSetting extends Setting {

    public abstract static class RawNumberSettings {}

    private static final C_LIST_ADD_TYPE cListAddType = C_LIST_ADD_TYPE.ADD_TO_LIST;

    protected int number;
    protected String[] stringArrayWapp;

    private final State numberState;

    protected NumberSetting(Sms sms, State.KEY key, State.KEY numberKey, String string) {
        super(sms, key, STATE_TYPE.CONFIRMED_LONG, cListAddType);

        initList();
        parseSplitString(string);
        parseNumber();
        parse(string);

        numberState = new State(
                getDate(), numberKey,
                (double) number, "",
                State.STATUS.CONFIRMED, getId()
        );
    }

    public NumberSetting(State.KEY key) {
        super(new Sms(), key, STATE_TYPE.UNSET_LONG, cListAddType);
        numberState = null;
    }

    protected abstract void initList();
    protected abstract void parseSplitString(String string);
    protected abstract void parseNumber();
    protected abstract void parse(String string);
    public abstract List<? extends RawNumberSettings> getList();

    public int getNumber() {
        return number;
    }

    public State getNumberState() {
        return numberState;
    }
}
