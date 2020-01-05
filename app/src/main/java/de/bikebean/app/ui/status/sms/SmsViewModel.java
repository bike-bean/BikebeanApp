package de.bikebean.app.ui.status.sms;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.ui.status.sms.load.SmsLoader;

public class SmsViewModel extends AndroidViewModel {

    private final SmsRepository mRepository;

    private final LiveData<List<Sms>> mChat;
    private final LiveData<List<Sms>> mNewIncoming;

    public SmsViewModel(Application application) {
        super(application);
        mRepository = new SmsRepository(application);
        mChat = mRepository.getChat();
        mNewIncoming = mRepository.getNewIncoming();
    }

    public LiveData<List<Sms>> getChat() {
        return mChat;
    }

    public LiveData<List<Sms>> getNewIncoming() {
        return mNewIncoming;
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

    public void fetchSms(Context context, String address, String timestamp) {
        // load the sms list in background
        SmsLoader smsLoader = new SmsLoader(context, this);
        smsLoader.execute(address, timestamp);
    }

    public void insert(Sms sms) {
        mRepository.insert(sms);
    }

    public void markParsed(int id) {
        mRepository.markParsed(id);
    }
}
