package de.bikebean.app.ui.drawer.map;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.drawer.status.StateRepository;

class MapStateRepository extends StateRepository {

    private final LiveData<List<State>> mConfirmedLocationLat;
    private final LiveData<List<State>> mConfirmedLocationLng;
    private final LiveData<List<State>> mConfirmedLocationAcc;

    private final @NonNull MapMarkerCache mapMarkerCache;

    MapStateRepository(final @NonNull Application application) {
        super(application);

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



    @Nullable Bitmap getMapMarkerBitmap(final MapMarkerCache.DRAWABLES drawable,
                                        final @ColorInt int color) {
        return mapMarkerCache.getBitmap(drawable, color);
    }

    @NonNull Bitmap getMapMarkerBitmap(final Drawable drawable,
                                        final @ColorInt int color) {
        return mapMarkerCache.getBitmap(drawable, color);
    }
}
