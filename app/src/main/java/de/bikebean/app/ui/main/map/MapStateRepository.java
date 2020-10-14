package de.bikebean.app.ui.main.map;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.main.status.StateRepository;

class MapStateRepository extends StateRepository {

    private final LiveData<List<State>> mStatusNumberCellTowers;
    private final LiveData<List<State>> mStatusNumberWifiAccessPoints;
    private final LiveData<List<State>> mConfirmedLocationLat;
    private final LiveData<List<State>> mConfirmedLocationLng;
    private final LiveData<List<State>> mConfirmedLocationAcc;

    private final @NonNull MapMarkerCache mapMarkerCache;

    MapStateRepository(final @NonNull Application application) {
        super(application);

        mStatusNumberCellTowers = mStateDao.getAllByKey(State.KEY.NO_CELL_TOWERS.get());
        mStatusNumberWifiAccessPoints = mStateDao.getAllByKey(State.KEY.NO_WIFI_ACCESS_POINTS.get());

        mConfirmedLocationLat = mStateDao.getByKeyAndState(
                State.KEY.LAT.get(), State.STATUS.CONFIRMED.ordinal()
        );
        mConfirmedLocationLng = mStateDao.getByKeyAndState(
                State.KEY.LNG.get(), State.STATUS.CONFIRMED.ordinal()
        );
        mConfirmedLocationAcc = mStateDao.getByKeyAndState(
                State.KEY.ACC.get(), State.STATUS.CONFIRMED.ordinal()
        );

        mapMarkerCache = new MapMarkerCache(application.getApplicationContext(), 100);
    }

    LiveData<List<State>> getConfirmedLocationLat() {
        return mConfirmedLocationLat;
    }

    LiveData<List<State>> getConfirmedLocationLng() {
        return mConfirmedLocationLng;
    }

    LiveData<List<State>> getConfirmedLocationAcc() {
        return mConfirmedLocationAcc;
    }

    LiveData<List<State>> getStatusNumberCellTowers() {
        return mStatusNumberCellTowers;
    }

    LiveData<List<State>> getStatusNumberWifiAccessPoints() {
        return mStatusNumberWifiAccessPoints;
    }

    @Nullable Bitmap getMapMarkerBitmap(@ColorInt int color) {
        return mapMarkerCache.getBitmap(color);
    }
}
