package de.bikebean.app.ui.status.sms.load;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
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
import de.bikebean.app.ui.status.StatusViewModel;
import de.bikebean.app.ui.status.preferences.PreferenceUpdater;
import de.bikebean.app.ui.status.sms.SmsViewModel;

public class SmsLoader extends AsyncTask<String, Void, String> {

    private final WeakReference<Context> activityReference;
    private final WeakReference<SmsViewModel> vmReference;
    private final WeakReference<StatusViewModel> statusViewModelReference;

    public SmsLoader(Context context, SmsViewModel vm, StatusViewModel statusViewModel) {
        activityReference = new WeakReference<>(context);
        vmReference = new WeakReference<>(vm);
        statusViewModelReference = new WeakReference<>(statusViewModel);
    }

    @Override
    public String doInBackground(String... args) {
        String phoneNumber = args[0];
        String waitForNewMessage = args[1];
        boolean initialLoading = Boolean.valueOf(args[2]);
        String[] argList = {phoneNumber};

        Context context = activityReference.get();
        SmsViewModel vm = vmReference.get();

        int size = vm.getInboxCount();

        ContentResolver contentResolver = context.getContentResolver();

        try {
            if (!initialLoading) {
                Cursor inbox = getInbox(contentResolver, argList);
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

                traverseInboxAndClose(inbox, vm);
            } else {
                Cursor inbox = getInbox(contentResolver, argList);
                Log.d(MainActivity.TAG, "Initial Loading");

                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

                traverseInboxAndCloseInitial(inbox, vm, settings);

                new PreferenceUpdater().updateInitialLoading(settings, false);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return phoneNumber;
    }

    private void traverseInboxAndClose(Cursor inbox, SmsViewModel vm) {
        if (inbox.moveToFirst()) {
            Log.d(MainActivity.TAG, "Loading " + inbox.getCount() + " SMS");
            for (int i = 0; i < inbox.getCount(); i++) {
                String address = inbox.getString(inbox.getColumnIndexOrThrow("address"));
                String id = inbox.getString(inbox.getColumnIndexOrThrow("_id"));
                // String thread_id = inbox.getString(inbox.getColumnIndexOrThrow("thread_id"));
                String body = inbox.getString(inbox.getColumnIndexOrThrow("body"));
                String type = inbox.getString(inbox.getColumnIndexOrThrow("type"));
                String date = inbox.getString(inbox.getColumnIndexOrThrow("date"));
                long timestamp = Long.parseLong(date);

                if (vm.getSmsById(Integer.parseInt(id)).size() == 0)
                    vm.insert(new Sms(Integer.parseInt(id), address, body, Integer.parseInt(type),
                            Sms.STATUS_NEW, Utils.convertToTime(timestamp), timestamp));
                else
                    Log.d(MainActivity.TAG, "SMS " + id + " is already present");

                inbox.moveToNext();
            }

            inbox.close();
        }
    }

    private void traverseInboxAndCloseInitial(
            Cursor inbox,
            SmsViewModel smsViewModel,
            SharedPreferences settings) {
        Conversation conversation = new Conversation(settings, statusViewModelReference.get());

        if (inbox.moveToFirst()) {
            Log.d(MainActivity.TAG, "Loading " + inbox.getCount() + " SMS");
            for (int i = 0; i < inbox.getCount(); i++) {
                String address = inbox.getString(inbox.getColumnIndexOrThrow("address"));
                String id = inbox.getString(inbox.getColumnIndexOrThrow("_id"));
                // String thread_id = inbox.getString(inbox.getColumnIndexOrThrow("thread_id"));
                String body = inbox.getString(inbox.getColumnIndexOrThrow("body"));
                String type = inbox.getString(inbox.getColumnIndexOrThrow("type"));
                String date = inbox.getString(inbox.getColumnIndexOrThrow("date"));
                long timestamp = Long.parseLong(date);

                Sms sms = new Sms(Integer.parseInt(id), address, body, Integer.parseInt(type),
                        Sms.STATUS_PARSED, Utils.convertToTime(timestamp), timestamp);

                smsViewModel.insert(sms);
                conversation.add(sms);

                inbox.moveToNext();
            }

            inbox.close();
        }

        conversation.updatePreferences();
    }

    private Cursor getInbox(ContentResolver contentResolver, String[] argList) {
        return contentResolver.query(
                Telephony.Sms.Inbox.CONTENT_URI, null,
                "address=?", argList,
                null, null);
    }
}
