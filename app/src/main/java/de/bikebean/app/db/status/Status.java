package de.bikebean.app.db.status;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "status_table")
public class Status {

    public static final int STATUS_CONFIRMED = 0;
    public static final int STATUS_ESTIMATED = 1;
    public static final int STATUS_CALCULATED = 2;

    @PrimaryKey
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

    public Status(
            long timestamp,
            @NonNull String key,
            @NonNull Double value,
            @NonNull String longValue,
            int state
    ) {
        this.mTimestamp = timestamp;
        this.mKey = key;
        this.mValue = value;
        this.mLongValue = longValue;
        this.mState = state;
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
}
