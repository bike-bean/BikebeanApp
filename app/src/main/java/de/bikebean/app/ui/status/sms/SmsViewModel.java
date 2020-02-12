package de.bikebean.app.ui.status.sms;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.ui.status.StateViewModel;
import de.bikebean.app.ui.status.sms.load.SmsLoader;

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

    public int getLatestId() {
        return mRepository.getLatestId();
    }

    public void fetchSms(Context context, StateViewModel stateViewModel,
                         String address, String timestamp) {
        // load the sms list in background
        new SmsLoader(context, this, stateViewModel).execute(address, timestamp);
    }

    public void fetchSmsSync(Context context, StateViewModel stateViewModel, String address) {
        // load the sms list in foreground
        new SmsLoader(context, this, stateViewModel).loadInitial(address);
    }

    public void insert(Sms sms) {
        mRepository.insert(sms);
    }

    public void markParsed(int id) {
        mRepository.markParsed(id);
    }
}
