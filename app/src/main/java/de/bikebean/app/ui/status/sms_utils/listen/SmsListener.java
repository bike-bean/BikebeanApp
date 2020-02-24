package de.bikebean.app.ui.status.sms_utils.listen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.preference.PreferenceManager;

import de.bikebean.app.MainActivity;
import de.bikebean.app.ui.status.StateViewModel;
import de.bikebean.app.ui.status.menu.log.LogViewModel;
import de.bikebean.app.ui.status.menu.sms_history.SmsViewModel;

public class SmsListener extends BroadcastReceiver {
    /*
     SMS Listener

     This class listens for new SMS and calls
     our viewModel to update the SMS in background.
     */
    private static SmsViewModel mSmsViewModel;
    private static StateViewModel mStateViewModel;
    private static LogViewModel mLogViewModel;

    public static void setViewModels(StateViewModel stateViewModel, SmsViewModel smsViewModel,
                                     LogViewModel logViewModel) {
        mSmsViewModel = smsViewModel;
        mStateViewModel = stateViewModel;
        mLogViewModel = logViewModel;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(MainActivity.TAG, "New SMS received...");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String address = sharedPreferences.getString("number", "");

        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            boolean isMessageFromBikeBean = false;

            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                String sender = smsMessage.getOriginatingAddress();

                if (sender != null && sender.equals(address)) {
                    isMessageFromBikeBean = true;
                    break;
                } else if (sender != null)
                    Log.d(MainActivity.TAG, "Sender is not ours. (" + sender + ")");
            }

            if (isMessageFromBikeBean)
                mSmsViewModel.fetchSms(
                        context,
                        mStateViewModel,
                        mLogViewModel,
                        address,
                        "true"
                );
        }
    }
}

