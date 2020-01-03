package de.bikebean.app.ui.status.sms;

import android.app.Application;
import android.provider.Telephony;

import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.BikeBeanRoomDatabase;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.sms.SmsDao;

class SmsRepository {

    private final SmsDao mSmsDao;
    private final LiveData<List<Sms>> mChat;

    SmsRepository(Application application) {
        BikeBeanRoomDatabase db = BikeBeanRoomDatabase.getDatabase(application);
        mSmsDao = db.smsDao();
        mChat = mSmsDao.getAll();
    }

    LiveData<List<Sms>> getChat() {
        return mChat;
    }

    int getInboxCount() {
        return mSmsDao.getCountByType(String.valueOf(Telephony.Sms.MESSAGE_TYPE_INBOX));
    }

    List<Sms> getLatestTwoInInbox() {
        return mSmsDao.getLatestByType(2, String.valueOf(Telephony.Sms.MESSAGE_TYPE_INBOX));
    }

    void insert(final Sms sms) {
        BikeBeanRoomDatabase.databaseWriteExecutor.execute(() -> mSmsDao.insert(sms));
    }
}
