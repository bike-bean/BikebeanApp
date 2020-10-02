package de.bikebean.app.ui.utils.sms.listen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import de.bikebean.app.ui.main.status.StateViewModel;
import de.bikebean.app.ui.main.status.menu.log.LogViewModel;
import de.bikebean.app.ui.main.status.menu.sms_history.SmsViewModel;

public class SmsListener extends BroadcastReceiver {
    /*
     SMS Listener

     This class listens for new SMS and calls
     our viewModel to update the SMS in background.
     */
    private static SmsViewModel sm;
    private static StateViewModel st;
    private static LogViewModel lv;

    public static void setViewModels(SmsViewModel smsViewModel, StateViewModel stateViewModel,
                                     LogViewModel logViewModel) {
        sm = smsViewModel;
        st = stateViewModel;
        lv = logViewModel;
    }

    @Override
    public void onReceive(@NonNull Context ctx, @NonNull Intent intent) {
        if (lv == null || st == null || sm == null)
            return;

        lv.d("Received something...");
        if (intent.getAction() == null
                || !intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION))
            return;
        else
            lv.d("New SMS received...");

        final @Nullable String address = PreferenceManager.getDefaultSharedPreferences(ctx)
                .getString("number", null);
        if (address == null) {
            lv.e("Failed to load BB-number! Maybe it's not set?");
            return;
        }

        for (@NonNull SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
            final @Nullable String senderAddress = smsMessage.getOriginatingAddress();

            if (senderAddress != null)
                if (senderAddress.equals(address)) {
                    lv.d("Sender is ours, start fetching sms");
                    sm.fetchSms(ctx, st, lv, address);
                } else
                    lv.d("Sender is not ours. (" + senderAddress + ")");
            else
                lv.w("Failed to get sender address!");
        }
    }
}

