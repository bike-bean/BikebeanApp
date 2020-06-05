package de.bikebean.app.ui.utils.sms.listen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.ui.main.status.menu.log.LogViewModel;
import de.bikebean.app.ui.main.status.menu.sms_history.SmsViewModel;

public class SmsListener extends BroadcastReceiver {
    /*
     SMS Listener

     This class listens for new SMS and calls
     our viewModel to update the SMS in background.
     */
    private static SmsViewModel sm;
    private static LogViewModel lv;

    public static void setViewModels(SmsViewModel smsViewModel, LogViewModel logViewModel) {
        sm = smsViewModel;
        lv = logViewModel;
    }

    @Override
    public void onReceive(Context ctx, @NonNull Intent intent) {
        if (lv == null || sm == null)
            return;

        lv.d("Received something...");
        if (intent.getAction() == null
                || !intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION))
            return;

        lv.d("New SMS received...");
        final String address = PreferenceManager.getDefaultSharedPreferences(ctx)
                .getString("number", "");

        for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
            String sender = smsMessage.getOriginatingAddress();

            Sms sms = new Sms(sm.getLatestId(lv, Telephony.Sms.MESSAGE_TYPE_INBOX), smsMessage);

            if (sender == null || !sender.equals(address))
                lv.d("Sender is not ours. (" + sender + ")");
            else
                sm.insert(sms);
        }
    }
}

