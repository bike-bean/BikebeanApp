package de.bikebean.app.db.state;

import android.os.Bundle;

import androidx.annotation.NonNull;

import de.bikebean.app.db.DatabaseEntity;

import static de.bikebean.app.db.state.State.KEY.ACC;
import static de.bikebean.app.db.state.State.KEY.LAT;
import static de.bikebean.app.db.state.State.KEY.LNG;
import static de.bikebean.app.db.state.State.KEY.NO_CELL_TOWERS;
import static de.bikebean.app.db.state.State.KEY.NO_WIFI_ACCESS_POINTS;

public class LocationState extends DatabaseEntity {

    private final int smsId;
    private final long timestamp;

    private final double lat;
    private final double lng;
    private final double acc;

    private final int noCellTowers;
    private final int noWifiAccessPoints;

    public LocationState(final @NonNull State latState, final @NonNull State lngState,
                         final @NonNull State accState, final @NonNull State noCellTowersState,
                         final @NonNull State noWifiAccessPointsState) {
        smsId = latState.getSmsId();
        timestamp = latState.getTimestamp();

        lat = latState.getValue();
        lng = lngState.getValue();
        acc = accState.getValue();

        noCellTowers = noCellTowersState.getValue().intValue();
        noWifiAccessPoints = noWifiAccessPointsState.getValue().intValue();
    }

    public double getAcc() {
        return acc;
    }

    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getSmsId() {
        return smsId;
    }

    public @NonNull Bundle getArgs() {
        return new LocationBundle()
                .putDouble(LAT, lat)
                .putDouble(LNG, lng)
                .putDouble(ACC, acc)
                .putInt(NO_CELL_TOWERS, noCellTowers)
                .putInt(NO_WIFI_ACCESS_POINTS, noWifiAccessPoints)
                .get();
    }

    static class LocationBundle {

        private final @NonNull Bundle bundle = new Bundle();

        Bundle get() {
            return bundle;
        }

        LocationBundle putDouble(final @NonNull State.KEY key, double value) {
            bundle.putDouble(key.get(), value);
            return this;
        }

        LocationBundle putInt(final @NonNull State.KEY key, int value) {
            bundle.putInt(key.get(), value);
            return this;
        }
    }

    @Override
    public DatabaseEntity getNullType() {
        return null;
    }

    @Override
    public String createReportTitle() {
        return null;
    }

    @Override
    public String createReport() {
        return null;
    }
}
