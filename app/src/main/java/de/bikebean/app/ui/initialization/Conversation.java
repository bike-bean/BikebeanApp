package de.bikebean.app.ui.initialization;

import androidx.annotation.NonNull;

import java.util.List;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.type.types.InitialConversation;
import de.bikebean.app.db.type.types.SmsParserType;
import de.bikebean.app.ui.drawer.status.StateViewModel;
import de.bikebean.app.ui.drawer.log.LogViewModel;
import de.bikebean.app.ui.drawer.sms_history.SmsViewModel;
import de.bikebean.app.ui.utils.sms.parser.SmsParser;

public class Conversation {

    private final @NonNull StateViewModel stateViewModel;
    private final @NonNull SmsViewModel smsViewModel;
    private final @NonNull LogViewModel logViewModel;

    private final @NonNull List<Setting> settings;

    public Conversation(final @NonNull StateViewModel st, final @NonNull SmsViewModel sm,
                        final @NonNull LogViewModel lv) {
        stateViewModel = st;
        smsViewModel = sm;
        logViewModel = lv;

        settings = new InitialConversation().getSettings();
    }

    public void add(final @NonNull Sms sms) {
        for (@NonNull SmsParserType type : new SmsParser(sms, null, null, logViewModel).getTypes())
            type.addToConversationList(settings);

        smsViewModel.insert(sms);
    }

    public void updatePreferences() {
        logViewModel.d("Inserting " + settings.size() + " Settings");
        stateViewModel.insert(settings);
        logViewModel.d("Done inserting Settings!");
    }
}
