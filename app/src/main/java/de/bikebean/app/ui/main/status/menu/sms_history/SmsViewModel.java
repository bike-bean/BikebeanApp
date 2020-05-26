package de.bikebean.app.ui.main.status.menu.sms_history;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.MutableObject;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.ui.main.status.StateViewModel;
import de.bikebean.app.ui.main.status.menu.log.LogViewModel;
import de.bikebean.app.ui.utils.sms.load.SmsLoader;

public class SmsViewModel extends AndroidViewModel {

    private final SmsRepository mRepository;

    private final LiveData<List<Sms>> mChat;
    private final LiveData<List<Sms>> mNewIncoming;
    private final LiveData<List<Integer>> mAllIds;

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

    public int getInboxCount() {
        return mRepository.getInboxCount();
    }

    public List<Sms> getSmsById(int id) {
        return mRepository.getSmsById(id);
    }

    public Sms getSmsByIdSync(int id) {
        final MutableObject<Sms> sms = new MutableObject<>(new Sms());

        return (Sms) sms.getDbEntitySync(mRepository::getSmsById, "", id);
    }

    public int getLatestId(LogViewModel lv) {
        MutableInt id = new MutableInt();

        new Thread(() -> id.set(mRepository.getLatestId(lv))).start();

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

    public void fetchSms(Context context, StateViewModel st, LogViewModel lv,
                         String address, String timestamp) {
        // load the sms list in background
        new SmsLoader(context, this, st, lv).execute(address, timestamp);
    }

    public void fetchSmsSync(Context context, StateViewModel st, LogViewModel lv, String address) {
        // load the sms list in foreground
        new SmsLoader(context, this, st, lv).loadInitial(address);
    }

    public void insert(Sms sms) {
        mRepository.insert(sms);
    }

    public void markParsed(Sms sms) {
        mRepository.markParsed(sms);
    }

}
