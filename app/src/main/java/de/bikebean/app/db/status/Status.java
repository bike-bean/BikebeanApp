package de.bikebean.app.db.status;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "status_table")
public class Status {

    // Confirmed through SMS with newest data (default state)
    public static final int STATUS_CONFIRMED = 0;
    // CellTowers and WifiAccessPoints that have not yet been used for calculation of the location
    // (Future: Also Settings that have not yet been confirmed? How to mark in UI??)
    public static final int STATUS_PENDING = 1;

    public static final String KEY_BATTERY = "battery";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LNG = "lng";
    public static final String KEY_ACC = "acc";
    public static final String KEY_CELL_TOWERS = "cellTowers";
    public static final String KEY_WIFI_ACCESS_POINTS = "wifiAccessPoints";
    public static final String KEY_NO_CELL_TOWERS = "noCellTowers";
    public static final String KEY_NO_WIFI_ACCESS_POINTS = "noWifiAccessPoints";
    public static final String KEY_STATUS = "status";

    @PrimaryKey
    @ColumnInfo(name = "timestamp")
    private long mTimestamp;

    @NonNull
    @ColumnInfo(name = "_key")
    private String mKey;

    @NonNull
    @ColumnInfo(name = "value")
    private Double mValue;

    @NonNull
    @ColumnInfo(name = "lvalue")
    private String mLongValue;

    @ColumnInfo(name = "state")
    private int mState;

    @ColumnInfo(name = "sms_id")
    private int mSmsId;

    public Status(
            long timestamp,
            @NonNull String key,
            @NonNull Double value,
            @NonNull String longValue,
            int state,
            int smsId
    ) {
        this.mTimestamp = timestamp;
        this.mKey = key;
        this.mValue = value;
        this.mLongValue = longValue;
        this.mState = state;
        this.mSmsId = smsId;
    }

    String getKey() {
        return this.mKey;
    }

    public long getTimestamp() {
        return this.mTimestamp;
    }

    public void setTimestamp(long timestamp) {
        this.mTimestamp = timestamp;
    }

    public Double getValue() {
        return this.mValue;
    }

    public void setValue(double value) {
        this.mValue = value;
    }

    public String getLongValue() {
        return this.mLongValue;
    }

    int getState() {
        return this.mState;
    }

    public int getSmsId() {
        return this.mSmsId;
    }
}
