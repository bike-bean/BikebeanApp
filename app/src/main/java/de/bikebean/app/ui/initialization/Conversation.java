package de.bikebean.app.ui.initialization;

import androidx.annotation.NonNull;

import java.util.List;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.type.types.InitialConversation;
import de.bikebean.app.ui.main.status.StateViewModel;
import de.bikebean.app.ui.main.status.menu.log.LogViewModel;
import de.bikebean.app.ui.main.status.menu.sms_history.SmsViewModel;
import de.bikebean.app.ui.utils.sms.parser.SmsParser;

public class Conversation {

    private final @NonNull StateViewModel stateViewModel;
    private final @NonNull SmsViewModel smsViewModel;
    private final @NonNull LogViewModel logViewModel;

    private final @NonNull List<Setting> settings;

    public Conversation(@NonNull StateViewModel st, @NonNull SmsViewModel sm,
                        @NonNull LogViewModel lv) {
        stateViewModel = st;
        smsViewModel = sm;
        logViewModel = lv;

        settings = new InitialConversation().getSettings();
    }

    public void add(Sms sms) {
        new SmsParser(sms, null, null, logViewModel).getType().addToConversationList(settings);
        smsViewModel.insert(sms);
    }

    public void updatePreferences() {
        logViewModel.d("Inserting " + settings.size() + " Settings");
        stateViewModel.insert(settings);
        logViewModel.d("Done inserting Settings!");
    }
}
