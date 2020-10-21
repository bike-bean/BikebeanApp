package de.bikebean.app.ui.drawer.sms_history;

import android.app.Application;
import android.content.Context;
import android.provider.Telephony;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.sms.SmsFactory;
import de.bikebean.app.ui.drawer.status.StateViewModel;
import de.bikebean.app.ui.drawer.log.LogViewModel;
import de.bikebean.app.ui.utils.sms.load.SmsLoader;
import de.bikebean.app.ui.utils.sms.send.SmsSender;

public class SmsViewModel extends AndroidViewModel {

    private final SmsRepository mRepository;

    private final LiveData<List<Sms>> mChat;
    private final LiveData<List<Sms>> mNewIncoming;
    private final LiveData<List<Integer>> mAllIds;

    private boolean newMessagesObserving = false;

    public SmsViewModel(Application application) {
        super(application);

        mRepository = new SmsRepository(application);

        mChat = mRepository.getChat();
        mNewIncoming = mRepository.getNewIncoming();
        mAllIds = mRepository.getAllInboxIds();
    }

    LiveData<List<Sms>> getChat() {
        return mChat;
    }

    public LiveData<List<Sms>> getNewIncoming() {
        return mNewIncoming;
    }

    public LiveData<List<Integer>> getAllIds() {
        return mAllIds;
    }

    public boolean isNewMessagesObserving() {
        return newMessagesObserving;
    }

    public void setNewMessagesObserving(final boolean newMessagesObserving) {
        this.newMessagesObserving = newMessagesObserving;
    }

    public @NonNull List<Sms> getSmsById(int id) {
        return mRepository.getSmsById(id);
    }

    public int getLatestId(LogViewModel lv, int type) {
        MutableInt id = new MutableInt();

        new Thread(() -> id.set(mRepository.getLatestId(lv, type))).start();

        return id.waitForSet();
    }

    static class MutableInt {
        private volatile int i;
        private boolean isSet = false;

        void set(int i) {
            this.i = i;
            isSet = true;
        }

        int waitForSet() {
            while (!isSet) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return i;
        }
    }

    public void fetchSms(final @NonNull Context context, final StateViewModel st,
                         final LogViewModel lv, final @NonNull String address) {
        /* load the sms list in background */
        new SmsLoader(context, this, st, lv).execute(address);
    }

    public void fetchSmsSync(final @NonNull Context context,
                             final StateViewModel st, final LogViewModel lv,
                             final @NonNull String address) {
        /* load the sms list in foreground */
        new SmsLoader(context, this, st, lv).loadInitial(address);
    }

    public void insert(final @NonNull Sms sms) {
        mRepository.insert(sms);
    }

    public void insert(final @NonNull SmsSender smsSender, LogViewModel lv) {
        mRepository.insert(SmsFactory.createNewSentSms(
                getLatestId(lv, Telephony.Sms.MESSAGE_TYPE_SENT), smsSender)
        );
    }

    public void markParsed(final @NonNull Sms sms) {
        mRepository.markParsed(sms);
    }
}
