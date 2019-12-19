package de.bikebean.app.ui.status.sms.listen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.preference.PreferenceManager;

import de.bikebean.app.MainActivity;


public class SmsListener extends BroadcastReceiver {
    /*
     SMS Listener

     This class listens for new SMS and checks
     if the SMS is coming from "our" bikebean
     */

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(MainActivity.TAG, "New SMS received...");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String bikeBeanNumber = sharedPreferences.getString("number", "");

        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                String messageBody = smsMessage.getMessageBody();
                String sender = smsMessage.getOriginatingAddress();

                if (sender != null && sender.equals(bikeBeanNumber))
                    Log.d(MainActivity.TAG, messageBody);
                    // TODO: Do some more stuff with the message here!
                else if (sender != null)
                    Log.d(MainActivity.TAG, "Sender is not ours. (" + sender + ")");
            }
        }
    }
}

