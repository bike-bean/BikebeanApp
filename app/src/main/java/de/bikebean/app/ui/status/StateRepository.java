package de.bikebean.app.ui.status;

import android.app.Application;

import java.util.List;

import de.bikebean.app.db.BikeBeanRoomDatabase;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateDao;

public class StateRepository {

    protected final StateDao mStateDao;

    public StateRepository(Application application) {
        BikeBeanRoomDatabase db = BikeBeanRoomDatabase.getDatabase(application);
        mStateDao = db.stateDao();
    }

    void insert(final State state) {
        BikeBeanRoomDatabase.databaseWriteExecutor.execute(() -> mStateDao.insert(state));
    }

    List<State> getConfirmedStateSync(String key, int smsId) {
        discard(smsId);
        return mStateDao.getByKeyAndStateSync(key, State.STATUS.CONFIRMED.ordinal());
    }

    List<State> getLastStateSync(String key, int smsId) {
        discard(smsId);
        return mStateDao.getByKeySync(key);
    }

    List<State> getStateByIdSync(String key, int smsId) {
        return mStateDao.getByKeyAndIdSync(key, smsId);
    }

    private void discard(int smsId) {
        if (smsId == 0)
            assert true;
        else
            assert true;
    }
}
