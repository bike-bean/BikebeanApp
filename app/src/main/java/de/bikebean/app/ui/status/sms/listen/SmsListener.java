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
import de.bikebean.app.ui.status.sms.parser.SmsParser;


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

        final SmsParser smsParser = new SmsParser();

        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            StringBuilder messageBody = new StringBuilder();

            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                String messageBodyInner = smsMessage.getMessageBody();
                String sender = smsMessage.getOriginatingAddress();

                if (sender != null && sender.equals(bikeBeanNumber)) {
                    messageBody.append(messageBodyInner);
                } else if (sender != null)
                    Log.d(MainActivity.TAG, "Sender is not ours. (" + sender + ")");
            }

            if (!messageBody.toString().equals("")) {
                Log.d(MainActivity.TAG, messageBody.toString());
                smsParser.updateStatus(context, messageBody.toString());
            }
        }
    }
}

