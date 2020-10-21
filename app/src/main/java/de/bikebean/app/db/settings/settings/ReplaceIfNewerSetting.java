package de.bikebean.app.db.settings.settings;

import androidx.annotation.NonNull;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public abstract class ReplaceIfNewerSetting extends Setting {

    public ReplaceIfNewerSetting(final @NonNull Sms sms, final @NonNull State state) {
        super(state, Setting::replaceIfNewer);
    }
}
