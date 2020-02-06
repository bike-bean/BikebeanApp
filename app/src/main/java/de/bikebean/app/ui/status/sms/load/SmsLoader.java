package de.bikebean.app.ui.status.sms.load;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.Telephony;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.lang.ref.WeakReference;

import de.bikebean.app.MainActivity;
import de.bikebean.app.Utils;
import de.bikebean.app.db.sms.Conversation;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.ui.status.StateViewModel;
import de.bikebean.app.ui.status.sms.SmsViewModel;

public class SmsLoader extends AsyncTask<String, Void, String> {

    private final WeakReference<Context> activityReference;
    private final SmsViewModel smsViewModel;
    private final StateViewModel stateViewModel;

    public SmsLoader(Context context, SmsViewModel smsViewModel, StateViewModel stateViewModel) {
        activityReference = new WeakReference<>(context);
        this.smsViewModel = smsViewModel;
        this.stateViewModel = stateViewModel;
    }

    @Override
    public String doInBackground(String... args) {
        String phoneNumber = args[0];
        String waitForNewMessage = args[1];
        boolean initialLoading = Boolean.valueOf(args[2]);
        String[] argList = {phoneNumber};

        Context context = activityReference.get();

        int size = smsViewModel.getInboxCount();

        ContentResolver contentResolver = context.getContentResolver();

        Cursor inbox = getInbox(contentResolver, argList);

        try {
            if (!initialLoading) {
                Log.d(MainActivity.TAG, "There are " + inbox.getCount() + " messages in inbox.");
                Log.d(MainActivity.TAG, "There are " + size + " messages already saved.");

                if (!waitForNewMessage.equals("")) {
                    // only search for the newly incoming message
                    // It may take a while until it can be retrieved from the content provider
                    Log.d(MainActivity.TAG, "Parsing inbox for new message...");

                    while (inbox.getCount() <= size) {
                        Log.d(MainActivity.TAG, "There are " + inbox.getCount() + " messages in inbox.");
                        Log.d(MainActivity.TAG, "There are " + size + " messages already saved.");
                        inbox = getInbox(contentResolver, argList);
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (inbox.getCount() == size)
                    return phoneNumber;

                traverseInboxAndClose(inbox, smsViewModel);
            } else {
                Log.d(MainActivity.TAG, "Initial Loading");

                if (traverseInboxAndCloseInitial(inbox, smsViewModel)) {
                    Log.d(MainActivity.TAG, "Initial Loading completed");
                    PreferenceManager.getDefaultSharedPreferences(context).edit()
                            .putBoolean("initialLoading", false)
                            .apply();
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return phoneNumber;
    }

    private void traverseInboxAndClose(Cursor inbox, SmsViewModel vm) {
        if (inbox.moveToFirst()) {
            Log.d(MainActivity.TAG, "Loading " + inbox.getCount() + " SMS");

            for (int i=0; i < inbox.getCount(); i++) {
                Sms sms = buildSms(inbox, Sms.STATUS_NEW);

                if (vm.getSmsById(sms.getId()).size() == 0)
                    vm.insert(sms);
                else
                    Log.d(MainActivity.TAG, "SMS " + sms.getId() + " is already present");

                inbox.moveToNext();
            }

            inbox.close();
        }
    }

    private boolean traverseInboxAndCloseInitial(Cursor inbox, SmsViewModel smsViewModel) {
        Conversation conversation = new Conversation(stateViewModel);

        if (inbox.moveToFirst()) {
            Log.d(MainActivity.TAG, "Loading " + inbox.getCount() + " SMS");
            for (int i = 0; i < inbox.getCount(); i++) {
                Sms sms = buildSms(inbox, Sms.STATUS_PARSED);

                smsViewModel.insert(sms);
                conversation.add(sms);

                inbox.moveToNext();
            }

            inbox.close();
        } else
            return false;

        try {
            conversation.updatePreferences();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return true;
    }

    private Sms buildSms(Cursor inbox, int smsState) {
        String address = inbox.getString(inbox.getColumnIndexOrThrow("address"));
        String id = inbox.getString(inbox.getColumnIndexOrThrow("_id"));
        String body = inbox.getString(inbox.getColumnIndexOrThrow("body"));
        String type = inbox.getString(inbox.getColumnIndexOrThrow("type"));
        String date = inbox.getString(inbox.getColumnIndexOrThrow("date"));
        long timestamp = Long.parseLong(date);

        return new Sms(Integer.parseInt(id), address, body, Integer.parseInt(type),
                smsState, Utils.convertToTime(timestamp), timestamp);
    }

    private Cursor getInbox(ContentResolver contentResolver, String[] argList) {
        return contentResolver.query(
                Telephony.Sms.Inbox.CONTENT_URI, null,
                "address=?", argList,
                null, null);
    }
}
