package de.bikebean.app.ui.main.status.menu.sms_history;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
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

    public List<Sms> getAllSinceDate(long timestamp) {
        List<Sms> smsList = new ArrayList<>();

        for (int i=0; i<=10; i++) {
            Sms sms = getSmsSync(mRepository::getAllSinceDate, String.valueOf(timestamp), i);

            if (sms != null)
                smsList.add(sms);
            else
                break;
        }

        return smsList;
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

    public int getLatestId() {
        return mRepository.getLatestId();
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

    public void markParsed(int id) {
        mRepository.markParsed(id);
    }

    private Sms getSmsSync(MutableObject.ListGetter smsGetter, String timestamp, int position) {
        final MutableObject<Sms> sms = new MutableObject<>(
                new Sms(0, "", "", 0, 0, "", 0), position
        );

        return (Sms) sms.getDbEntitySync(smsGetter, timestamp, 0);
    }
}
