package de.bikebean.app.db.sms;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import de.bikebean.app.db.DatabaseEntity;

@Entity(tableName = "sms_table")
public class Sms extends DatabaseEntity {

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

    Sms(
            int id,
            final @NonNull String address,
            final @NonNull String body,
            int type,
            int state,
            final @NonNull String date,
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

    public @NonNull String getAddress() {
        return this.mAddress;
    }

    public @NonNull String getBody() {
        return this.mBody;
    }

    public int getType() {
        return this.mType;
    }

    public int getState() {
        return this.mState;
    }

    public @NonNull String getDate() {
        return this.mDate;
    }

    public long getTimestamp() {
        return this.mTimestamp;
    }

    @Override
    public @NonNull DatabaseEntity getNullType() {
        return SmsFactory.createNullSms();
    }

    @Override
    public @NonNull String createReportTitle() {
        final @NonNull String delimiter = "\t";
        return "ID" + delimiter + "Body" + delimiter + "Type" + delimiter +
                "State" + delimiter + "Date" + "\n";
    }

    @Override
    public @NonNull String createReport() {
        final @NonNull String delimiter = "\t";
        return mId + delimiter + mBody.replace("\n", "//") +
                delimiter + mType + delimiter + STATUS.getName(mState) + delimiter + mDate + "\n";
    }

    public enum STATUS {
        NEW, PARSED;

        private static @NonNull String getName(int value) {
            for (final @NonNull STATUS s : STATUS.values()) {
                if (s.ordinal() == value)
                    return s.name();
            }

            return "UNDEFINED";
        }
    }

    public enum MESSAGE {
        _STATUS("Status"),
        WAPP("Wapp"),
        INT(""),
        WIFI(""),
        WARNING_NUMBER("Warningnumber");

        private @NonNull String msg;

        MESSAGE(@NonNull String msg) {
            this.msg = msg;
        }

        public @NonNull String getMsg() {
            return msg;
        }

        public void setValue(final @NonNull String msg) {
            this.msg = msg;
        }
    }
}
