package de.bikebean.app.db.state;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import de.bikebean.app.db.DatabaseEntity;
import de.bikebean.app.ui.utils.Utils;

@Entity(tableName = "state_table")
public class State extends DatabaseEntity {

    public enum STATUS {
        // Confirmed through SMS with newest data (default state)
        CONFIRMED,
        // CellTower and WifiAccessPoints that have not yet been used for calculation of the location
        // Also Settings that have not yet been confirmed.
        PENDING,
        // Settings that have yet not even been set
        // (Mostly warningNumber)
        UNSET;

        private static String getName(int value) {
            for (STATUS s : STATUS.values()) {
                if (s.ordinal() == value)
                    return s.name();
            }

            return "UNDEFINED";
        }
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
            @NonNull KEY key,
            @NonNull Double value,
            @NonNull String longValue,
            @NonNull STATUS state,
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
            @NonNull KEY key,
            double value
    ) {
        this.mTimestamp = System.currentTimeMillis();
        this.mKey = key.get();
        this.mValue = value;
        this.mLongValue = "";
        this.mState = STATUS.PENDING.ordinal();
        this.mSmsId = 0; // Maybe allow changing this in the future?
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

    public @NonNull Double getValue() {
        return this.mValue;
    }

    public @NonNull String getLongValue() {
        return this.mLongValue;
    }

    public int getState() {
        return this.mState;
    }

    public int getSmsId() {
        return this.mSmsId;
    }

    public boolean equalsId(@Nullable State other) {
        if (other != null)
            return this.id == other.id;
        else return false;
    }

    public boolean getIsWappCellTowers() {
        return mValue == WAPP_CELL_TOWERS;
    }

    public boolean getIsWappWifiAccessPoints() {
        return mValue == WAPP_WIFI_ACCESS_POINTS;
    }

    public boolean getIsNull() {
        return equalsWhole((State) getNullType());
    }

    private boolean equalsWhole(@NonNull State other) {
        return (id == other.id &&
                mValue.equals(other.mValue) &&
                mTimestamp == other.mTimestamp &&
                mKey.equals(other.mKey) &&
                mLongValue.equals(other.mLongValue) &&
                mState == other.mState &&
                mSmsId == other.mSmsId);
    }

    @Override
    public @NonNull DatabaseEntity getNullType() {
        return new State();
    }

    @Override
    public @NonNull String createReportTitle() {
        final @NonNull String delimiter = "\t";
        return "ID" + delimiter + "Key" + delimiter + "Date" + delimiter +
                "Timestamp" + delimiter + "Value" + delimiter +
                "Long Value" + delimiter + "State" + delimiter + "Sms ID" + "\n";
    }

    @Override
    public @NonNull String createReport() {
        final @NonNull String delimiter = "\t";
        return id + delimiter + mKey + delimiter + Utils.convertToTimeLog(mTimestamp) +
                delimiter + mTimestamp + delimiter + mValue + delimiter +
                mLongValue.replace("\n", "//") + delimiter +
                STATUS.getName(mState) + delimiter + mSmsId + "\n";
    }
}
