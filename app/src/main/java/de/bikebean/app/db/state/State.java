package de.bikebean.app.db.state;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import de.bikebean.app.db.DatabaseEntity;

@Entity(tableName = "state_table")
public class State extends DatabaseEntity {

    public enum STATUS {
        // Confirmed through SMS with newest data (default state)
        CONFIRMED,
        // CellTowers and WifiAccessPoints that have not yet been used for calculation of the location
        // Also Settings that have not yet been confirmed.
        PENDING,
        // Settings that have yet not even been set
        // (Mostly warningNumber)
        UNSET
    }

    public enum KEY {
        _STATUS("status"),
        BATTERY("battery"),
        WARNING_NUMBER("warningNumber"),
        INTERVAL("interval"),
        WIFI("wifi"),

        LOCATION("location"),
        WAPP("wapp"),
        LAT("lat"), LNG("lng"), ACC("acc"),
        NO_CELL_TOWERS("noCellTowers"), NO_WIFI_ACCESS_POINTS("noWifiAccessPoints"),
        CELL_TOWERS("cellTowers"), WIFI_ACCESS_POINTS("wifiAccessPoints");

        private final String key;

        KEY(String key) {
            this.key = key;
        }

        public String get() {
            return key;
        }

        public static KEY getValue(String key) {
            for (KEY k : KEY.values())
                if (k.get().equals(key))
                    return k;

            return KEY.BATTERY;
        }
    }

    public static final double WAPP_CELL_TOWERS = 0.0;
    public static final double WAPP_WIFI_ACCESS_POINTS = 1.0;

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "timestamp")
    private final long mTimestamp;

    @NonNull
    @ColumnInfo(name = "_key")
    private final String mKey;

    @NonNull
    @ColumnInfo(name = "value")
    private final Double mValue;

    @NonNull
    @ColumnInfo(name = "lvalue")
    private final String mLongValue;

    @ColumnInfo(name = "state")
    private final int mState;

    @ColumnInfo(name = "sms_id")
    private final int mSmsId;

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

    @Ignore
    public State(
            long timestamp,
            KEY key,
            @NonNull Double value,
            @NonNull String longValue,
            STATUS state,
            int smsId
    ) {
        this.mTimestamp = timestamp;
        this.mKey = key.get();
        this.mValue = value;
        this.mLongValue = longValue;
        this.mState = state.ordinal();
        this.mSmsId = smsId;
    }

    // For "PENDING" State
    @Ignore
    public State(
            KEY key,
            double value
    ) {
        this.mTimestamp = System.currentTimeMillis();
        this.mKey = key.get();
        this.mValue = value;
        this.mLongValue = "";
        this.mState = STATUS.PENDING.ordinal();
        this.mSmsId = 0; // Maybe allow changing this in the future?
    }

    // For "PENDING" State
    @Ignore
    public State(
            State.KEY key,
            double value,
            long timestamp
    ) {
        this.mTimestamp = timestamp;
        this.mKey = key.get();
        this.mValue = value;
        this.mLongValue = "";
        this.mState = STATUS.PENDING.ordinal();
        this.mSmsId = 0;
    }

    // nullType
    @Ignore
    public State() {
        this.mTimestamp = 0;
        this.mKey = "";
        this.mValue = 0.0;
        this.mLongValue = "";
        this.mState = 0;
        this.mSmsId = 0;
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

    @Override
    public DatabaseEntity getNullType() {
        return new State();
    }
}
