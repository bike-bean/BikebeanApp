package de.bikebean.app.db.sms;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sms_table")
public class Sms {

    public static final int STATUS_NEW = 0;
    public static final int STATUS_PARSED = 1;

    @PrimaryKey
    @ColumnInfo(name = "_id")
    private final int mId;

    @NonNull
    @ColumnInfo(name = "address")
    private final String mAddress;

    @NonNull
    @ColumnInfo(name = "body")
    private final String mBody;

    @ColumnInfo(name = "type")
    private final int mType;

    @ColumnInfo(name = "state")
    private final int mState;

    @NonNull
    @ColumnInfo(name = "date")
    private final String mDate;

    @ColumnInfo(name = "timestamp")
    private final long mTimestamp;

    public Sms(
            int id,
            @NonNull String address,
            @NonNull String body,
            int type,
            int state,
            @NonNull String date,
            long timestamp
    ) {
        this.mId = id;
        this.mAddress = address;
        this.mBody = body;
        this.mType = type;
        this.mState = state;
        this.mDate = date;
        this.mTimestamp = timestamp;
    }

    public int getId() {
        return this.mId;
    }

    public String getAddress() {
        return this.mAddress;
    }

    public String getBody() {
        return this.mBody;
    }

    public int getType() {
        return this.mType;
    }

    int getState() {
        return this.mState;
    }

    public String getDate() {
        return this.mDate;
    }

    public long getTimestamp() {
        return this.mTimestamp;
    }
}
