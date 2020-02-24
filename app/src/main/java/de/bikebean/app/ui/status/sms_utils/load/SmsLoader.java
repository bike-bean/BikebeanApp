package de.bikebean.app.ui.status.sms_utils.load;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.Telephony;
import android.util.Log;

import java.lang.ref.WeakReference;

import de.bikebean.app.MainActivity;
import de.bikebean.app.Utils;
import de.bikebean.app.db.sms.Conversation;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.ui.status.StateViewModel;
import de.bikebean.app.ui.status.menu.log.LogViewModel;
import de.bikebean.app.ui.status.menu.sms_history.SmsViewModel;

public class SmsLoader extends AsyncTask<String, Void, Void> {

    private final WeakReference<Context> contextWeakReference;
    private final SmsViewModel smsViewModel;
    private final StateViewModel stateViewModel;
    private final LogViewModel logViewModel;

    public SmsLoader(Context context, SmsViewModel smsViewModel, StateViewModel stateViewModel,
                     LogViewModel logViewModel) {
        contextWeakReference = new WeakReference<>(context);
        this.smsViewModel = smsViewModel;
        this.stateViewModel = stateViewModel;
        this.logViewModel = logViewModel;
    }

    @Override
    public Void doInBackground(String... args) {
        String phoneNumber = args[0];
        String waitForNewMessage = args[1];
        String[] argList = {phoneNumber};

        ContentResolver contentResolver = contextWeakReference.get().getContentResolver();
        Cursor inbox = getInbox(contentResolver, argList);

        int size = smsViewModel.getInboxCount();

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

        traverseInboxAndClose(inbox);

        return null;
    }

    private void traverseInboxAndClose(Cursor inbox) {
        if (inbox.moveToFirst()) {
            Log.d(MainActivity.TAG, "Loading " + inbox.getCount() + " SMS");

            for (int i=0; i < inbox.getCount(); i++) {
                Sms sms = buildSms(inbox, Sms.STATUS.NEW);

                if (smsViewModel.getSmsById(sms.getId()).size() == 0)
                    smsViewModel.insert(sms);
                else
                    Log.d(MainActivity.TAG, "SMS " + sms.getId() + " is already present");

                inbox.moveToNext();
            }

            inbox.close();
        }
    }

    public void loadInitial(String phoneNumber) {
        String[] argList = {phoneNumber};

        ContentResolver contentResolver = contextWeakReference.get().getContentResolver();
        Cursor inbox = getInbox(contentResolver, argList);

        Log.d(MainActivity.TAG, "Initial Loading");
        traverseInboxAndCloseInitial(inbox);
        Log.d(MainActivity.TAG, "Initial Loading completed");
    }

    private void traverseInboxAndCloseInitial(Cursor inbox) {
        Conversation conversation = new Conversation(stateViewModel, smsViewModel, logViewModel);

        if (inbox.moveToFirst()) {
            Log.d(MainActivity.TAG, "Loading " + inbox.getCount() + " SMS");
            for (int i = 0; i < inbox.getCount(); i++) {
                Sms sms = buildSms(inbox, Sms.STATUS.PARSED);
                conversation.add(sms);

                inbox.moveToNext();
            }

            inbox.close();
        } else
            return;

        conversation.updatePreferences();
    }

    private Sms buildSms(Cursor inbox, Sms.STATUS smsState) {
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
