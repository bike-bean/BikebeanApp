package de.bikebean.app.db.settings.settings;

import androidx.annotation.NonNull;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateFactory;

public class Wapp extends Setting {

    public Wapp(final @NonNull Sms sms, final double value) {
        super(
                StateFactory.createSimplePendingState(
                        sms, State.KEY.WAPP, value
                ),
                Setting::addToList
        );
    }
}
