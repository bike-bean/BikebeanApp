package de.bikebean.app.ui.main.status;

import android.app.Application;

import androidx.annotation.NonNull;

import java.util.List;

import de.bikebean.app.db.BikeBeanRoomDatabase;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateDao;

public class StateRepository {

    protected final @NonNull StateDao mStateDao;

    public StateRepository(final @NonNull Application application) {
        final @NonNull BikeBeanRoomDatabase db = BikeBeanRoomDatabase.getDatabase(application);
        mStateDao = db.stateDao();
    }

    void insert(final @NonNull State state) {
        BikeBeanRoomDatabase.databaseWriteExecutor.execute(() -> mStateDao.insert(state));
    }

    @NonNull List<State> getConfirmedStateSync(final @NonNull String key, int smsId) {
        discard(smsId);
        return mStateDao.getByKeyAndStateSync(key, State.STATUS.CONFIRMED.ordinal());
    }

    @NonNull List<State> getLastStateSync(final @NonNull String key, int smsId) {
        discard(smsId);
        return mStateDao.getByKeySync(key);
    }

    @NonNull List<State> getStateByIdSync(final @NonNull String key, int smsId) {
        return mStateDao.getByKeyAndIdSync(key, smsId);
    }

    private void discard(int smsId) {
        if (smsId == 0)
            assert true;
        else
            assert true;
    }
}
