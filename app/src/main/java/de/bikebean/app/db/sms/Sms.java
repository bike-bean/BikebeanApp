package de.bikebean.app.db.sms;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sms_table")
public class Sms {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "_id")
    private final String mId;

    @NonNull
    @ColumnInfo(name = "address")
    private final String mAddress;

    @NonNull
    @ColumnInfo(name = "body")
    private final String mBody;

    @NonNull
    @ColumnInfo(name = "type")
    private final String mType;

    @NonNull
    @ColumnInfo(name = "date")
    private final String mDate;

    @ColumnInfo(name = "timestamp")
    private final long mTimestamp;

    public Sms(
            @NonNull String id,
            @NonNull String address,
            @NonNull String body,
            @NonNull String type,
            @NonNull String date,
            long timestamp
    ) {
        this.mId = id;
        this.mAddress = address;
        this.mBody = body;
        this.mType = type;
        this.mDate = date;
        this.mTimestamp = timestamp;
    }

    public String getId() {
        return this.mId;
    }

    public String getAddress() {
        return this.mAddress;
    }

    public String getBody() {
        return this.mBody;
    }

    public String getType() {
        return this.mType;
    }

    public String getDate() {
        return this.mDate;
    }

    public long getTimestamp() {
        return this.mTimestamp;
    }
}
