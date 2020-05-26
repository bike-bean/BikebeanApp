package de.bikebean.app.ui.utils.sms.listen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Telephony;
import android.telephony.SmsMessage;

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
        mLogViewModel.d("New SMS received...");

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
                    mLogViewModel.d("Sender is not ours. (" + sender + ")");
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

