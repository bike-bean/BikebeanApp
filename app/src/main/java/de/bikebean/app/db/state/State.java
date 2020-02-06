package de.bikebean.app.db.state;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "state_table")
public class State {

    // Confirmed through SMS with newest data (default state)
    public static final int STATUS_CONFIRMED = 0;
    // CellTowers and WifiAccessPoints that have not yet been used for calculation of the location
    // Also Settings that have not yet been confirmed.
    public static final int STATUS_PENDING = 1;
    // Settings that have yet not even been set
    // (Mostly warningNumber)
    public static final int STATUS_UNSET = 2;

    public static final String KEY_BATTERY = "battery";
    public static final String KEY_STATUS = "status";
    public static final String KEY_WARNING_NUMBER = "warningNumber";
    public static final String KEY_INTERVAL = "interval";
    public static final String KEY_WIFI = "wifi";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LNG = "lng";
    public static final String KEY_ACC = "acc";
    public static final String KEY_NO_CELL_TOWERS = "noCellTowers";
    public static final String KEY_NO_WIFI_ACCESS_POINTS = "noWifiAccessPoints";
    public static final String KEY_CELL_TOWERS = "cellTowers";
    public static final String KEY_WIFI_ACCESS_POINTS = "wifiAccessPoints";

    @PrimaryKey(autoGenerate = true)
    public int id;

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

    public State(
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

    // For "PENDING" State
    @Ignore
    public State(
            @NonNull String key,
            double value
    ) {
        this.mTimestamp = System.currentTimeMillis();
        this.mKey = key;
        this.mValue = value;
        this.mLongValue = "";
        this.mState = STATUS_PENDING;
        this.mSmsId = 0; // Maybe allow changing this in the future?
    }

    public String getKey() {
        return this.mKey;
    }

    public long getTimestamp() {
        return this.mTimestamp;
    }

    public Double getValue() {
        return this.mValue;
    }

    public String getLongValue() {
        return this.mLongValue;
    }

    public int getState() {
        return this.mState;
    }

    public int getSmsId() {
        return this.mSmsId;
    }
}
