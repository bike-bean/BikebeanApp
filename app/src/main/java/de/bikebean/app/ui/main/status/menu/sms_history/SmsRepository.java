package de.bikebean.app.ui.main.status.menu.sms_history;

import android.app.Application;
import android.provider.Telephony;

import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.BikeBeanRoomDatabase;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.sms.SmsDao;
import de.bikebean.app.ui.main.status.menu.log.LogViewModel;

class SmsRepository {

    private final SmsDao mSmsDao;

    private final LiveData<List<Sms>> mChat;
    private final LiveData<List<Sms>> mNewIncoming;
    private final LiveData<List<Integer>> mAllIds;

    SmsRepository(Application application) {
        BikeBeanRoomDatabase db = BikeBeanRoomDatabase.getDatabase(application);
        mSmsDao = db.smsDao();

        mChat = mSmsDao.getAll();
        mNewIncoming = mSmsDao.getByStateAndType(
                Sms.STATUS.NEW.ordinal(),
                Telephony.Sms.MESSAGE_TYPE_INBOX
        );
        mAllIds = mSmsDao.getAllIdsByType(Telephony.Sms.MESSAGE_TYPE_INBOX);
    }

    LiveData<List<Sms>> getChat() {
        return mChat;
    }

    LiveData<List<Sms>> getNewIncoming() {
        return mNewIncoming;
    }

    LiveData<List<Integer>> getAllInboxIds() {
        return mAllIds;
    }

    int getInboxCount() {
        return mSmsDao.getCountByType(Telephony.Sms.MESSAGE_TYPE_INBOX);
    }

    List<Sms> getSmsById(int id) {
        return mSmsDao.getSmsById(id);
    }

    List<Sms> getSmsById(String s, int id) {
        if (s.isEmpty())
            return mSmsDao.getSmsById(id);
        else
            return mSmsDao.getSmsById(id);
    }

    int getLatestId(LogViewModel lv, int type) {
        List<Sms> l = mSmsDao.getLatestId(type);

        if (l.size() > 0) {
            return l.get(0).getId();
        } else {
            lv.d("There seems to be no last SMS saved in DB!");
            return 0;
        }
    }

    void insert(final Sms sms) {
        BikeBeanRoomDatabase.databaseWriteExecutor.execute(() -> mSmsDao.insert(sms));
    }

    void markParsed(Sms sms) {
        BikeBeanRoomDatabase.databaseWriteExecutor.execute(() ->
                mSmsDao.updateStateById(Sms.STATUS.PARSED.ordinal(), sms.getId()));
    }

}
