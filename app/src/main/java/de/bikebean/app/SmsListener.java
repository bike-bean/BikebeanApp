package de.bikebean.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;


public class SmsListener extends BroadcastReceiver {
    /*
     SMS Listener

     This class listens for new SMS and checks
     if the SMS is coming from "our" bikebean
     */

    // TODO: Put bikeBeanNumber in a better place than here!
    // Maybe create a central config / settings repository.
    private static String bikeBeanNumber;

    public static void setBikeBeanNumber(String bikeBeanNumber) {
        SmsListener.bikeBeanNumber = bikeBeanNumber;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(MainActivity.TAG, "New SMS received...");

        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                String messageBody = smsMessage.getMessageBody();
                String sender = smsMessage.getOriginatingAddress();

                if (sender != null && sender.equals(SmsListener.bikeBeanNumber))
                    Log.d(MainActivity.TAG, messageBody);
                    // TODO: Do some more stuff with the message here!
                else if (sender != null)
                    Log.d(MainActivity.TAG, "Sender is not ours. (" + sender + ")");
            }
        }
    }
}

