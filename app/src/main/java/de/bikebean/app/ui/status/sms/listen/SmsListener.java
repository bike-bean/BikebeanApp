package de.bikebean.app.ui.status.sms.listen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import de.bikebean.app.MainActivity;
import de.bikebean.app.ui.status.sms.SmsViewModel;

public class SmsListener extends BroadcastReceiver {
    /*
     SMS Listener

     This class listens for new SMS and calls
     our viewModel to update the SMS in background.
     */
    private static SmsViewModel mSmsViewModel;

    public static void setSmsViewModel(SmsViewModel smsViewModel) {
        mSmsViewModel = smsViewModel;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(MainActivity.TAG, "New SMS received...");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String address = sharedPreferences.getString("number", "");

        mSmsViewModel.fetchSms(context, address, "true");
    }
}

