package de.bikebean.app.ui.utils.sms.load;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.Telephony;

import java.lang.ref.WeakReference;

import de.bikebean.app.ui.initialization.Conversation;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.ui.main.status.StateViewModel;
import de.bikebean.app.ui.main.status.menu.log.LogViewModel;
import de.bikebean.app.ui.main.status.menu.sms_history.SmsViewModel;

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

        if (!waitForNewMessage.isEmpty()) {
            // only search for the newly incoming message
            // It may take a while until it can be retrieved from the content provider
            logViewModel.d("Parsing inbox for new message...");

            while (inbox.getCount() <= size) {
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

            for (int i=0; i < inbox.getCount(); i++) {
                Sms sms = new Sms(inbox, Sms.STATUS.NEW);

                if (smsViewModel.getSmsById(sms.getId()).size() == 0)
                    smsViewModel.insert(sms);

                inbox.moveToNext();
            }

            inbox.close();
        }
    }

    public void loadInitial(String phoneNumber) {
        String[] argList = {phoneNumber};

        ContentResolver contentResolver = contextWeakReference.get().getContentResolver();
        Cursor inbox = getInbox(contentResolver, argList);

        logViewModel.i("Initial Loading");
        traverseInboxAndCloseInitial(inbox);
        logViewModel.i("Initial Loading completed");
    }

    private void traverseInboxAndCloseInitial(Cursor inbox) {
        Conversation conversation = new Conversation(stateViewModel, smsViewModel, logViewModel);

        if (inbox.moveToFirst()) {
            logViewModel.d("Loading " + inbox.getCount() + " SMS");
            for (int i = 0; i < inbox.getCount(); i++) {
                conversation.add(new Sms(inbox, Sms.STATUS.PARSED));
                inbox.moveToNext();
            }

            logViewModel.d("Done Loading SMS!");
            inbox.close();
        } else
            return;

        conversation.updatePreferences();
    }

    private Cursor getInbox(ContentResolver contentResolver, String[] argList) {
        return contentResolver.query(
                Telephony.Sms.Inbox.CONTENT_URI, null,
                "address=?", argList,
                null, null);
    }
}
