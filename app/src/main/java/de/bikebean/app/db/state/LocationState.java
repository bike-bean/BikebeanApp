package de.bikebean.app.db.state;

import de.bikebean.app.db.DatabaseEntity;

public class LocationState extends DatabaseEntity {

    private final int smsId;
    private final long timestamp;

    private final double lat;
    private final double lng;
    private final double acc;

    private final int noCellTowers;
    private final int noWifiAccessPoints;

    public LocationState(State latState, State lngState, State accState,
                         State noCellTowersState, State noWifiAccessPointsState) {
        smsId = latState.getSmsId();
        timestamp = latState.getTimestamp();

        lat = latState.getValue();
        lng = lngState.getValue();
        acc = accState.getValue();

        noCellTowers = noCellTowersState.getValue().intValue();
        noWifiAccessPoints = noWifiAccessPointsState.getValue().intValue();
    }

    public int getNoWifiAccessPoints() {
        return noWifiAccessPoints;
    }

    public int getNoCellTowers() {
        return noCellTowers;
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
