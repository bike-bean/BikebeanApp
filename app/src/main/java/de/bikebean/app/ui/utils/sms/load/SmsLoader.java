package de.bikebean.app.ui.utils.sms.load;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.Telephony;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

import de.bikebean.app.ui.initialization.Conversation;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.ui.main.status.StateViewModel;
import de.bikebean.app.ui.main.status.menu.log.LogViewModel;
import de.bikebean.app.ui.main.status.menu.sms_history.SmsViewModel;

public class SmsLoader extends AsyncTask<String, Void, Void> {

    public interface InboxTraverser {
        void traverseInbox(final @NonNull Cursor inbox);
    }

    private final @NonNull WeakReference<Context> contextWeakReference;
    private final SmsViewModel smsViewModel;
    private final StateViewModel stateViewModel;
    private final LogViewModel logViewModel;

    public SmsLoader(final @NonNull Context context, final SmsViewModel smsViewModel,
                     final StateViewModel stateViewModel, final LogViewModel logViewModel) {
        contextWeakReference = new WeakReference<>(context);
        this.smsViewModel = smsViewModel;
        this.stateViewModel = stateViewModel;
        this.logViewModel = logViewModel;
    }

    @Override
    public Void doInBackground(final @NonNull String... args) {
        final @NonNull String phoneNumber = args[0];

        getCursor(phoneNumber, this::traverseInboxAndClose);

        return null;
    }

    public void loadInitial(final @NonNull String phoneNumber) {
        logViewModel.i("Initial Loading");
        getCursor(phoneNumber, this::traverseInboxAndCloseInitial);
        logViewModel.i("Initial Loading completed");
    }

    private void getCursor(final @NonNull String phoneNumber,
                           final @NonNull InboxTraverser inboxTraverser) {
        final @NonNull String[] argList = { phoneNumber };

        final @Nullable Context context = contextWeakReference.get();
        final @Nullable ContentResolver contentResolver;
        final @Nullable Cursor cursor;

        if (context != null)
            contentResolver = context.getContentResolver();
        else {
            logViewModel.w("Failed to load SMS, context was removed!");
            return;
        }

        if (contentResolver != null)
            cursor = getInbox(contentResolver, argList);
        else {
            logViewModel.w("Failed to load SMS, could not get contentResolver!");
            return;
        }

        if (cursor != null)
            inboxTraverser.traverseInbox(cursor);
        else
            logViewModel.w("Failed to load SMS, could not get cursor");
    }

    private @Nullable Cursor getInbox(final @NonNull ContentResolver contentResolver,
                                      final @NonNull String[] argList) {
        return contentResolver.query(
                Telephony.Sms.Inbox.CONTENT_URI, null,
                "address=?", argList,
                null, null);
    }

    private void traverseInboxAndClose(final @NonNull Cursor inbox) {
        if (inbox.moveToFirst()) {

            for (int i=0; i < inbox.getCount(); i++) {
                final @NonNull Sms sms = new Sms(inbox, Sms.STATUS.NEW);

                if (smsViewModel.getSmsById(sms.getId()).size() == 0)
                    smsViewModel.insert(sms);

                inbox.moveToNext();
            }

            inbox.close();
        }
    }

    private void traverseInboxAndCloseInitial(final @NonNull Cursor inbox) {
        final @NonNull Conversation conversation =
                new Conversation(stateViewModel, smsViewModel, logViewModel);

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
}
