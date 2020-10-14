package de.bikebean.app.db.sms;

import android.database.Cursor;
import android.provider.Telephony;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import de.bikebean.app.db.DatabaseEntity;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.utils.Utils;
import de.bikebean.app.ui.utils.sms.send.SmsSender;

@Entity(tableName = "sms_table")
public class Sms extends DatabaseEntity {

    public enum STATUS {
        NEW, PARSED
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

    @Ignore
    public Sms(
            int id,
            final @NonNull String address,
            final @NonNull String body,
            int type,
            final @NonNull STATUS state,
            final @NonNull String date,
            long timestamp
    ) {
        this.mId = id;
        this.mAddress = address;
        this.mBody = body;
        this.mType = type;
        this.mState = state.ordinal();
        this.mDate = date;
        this.mTimestamp = timestamp;
    }

    // New Sent SMS
    @Ignore
    public Sms(
            int smsId,
            final @NonNull SmsSender smsSender
            ) {
        long timestamp = System.currentTimeMillis();

        this.mId = smsId - 1;
        this.mAddress = smsSender.getAddress();
        this.mBody = smsSender.getMessage().getMsg();
        this.mType = Telephony.Sms.MESSAGE_TYPE_SENT;
        this.mState = STATUS.NEW.ordinal();
        this.mDate = Utils.convertToTime(timestamp);
        this.mTimestamp = timestamp;
    }

    @Ignore
    public Sms(
            final @NonNull State state
    ) {
        this.mId = state.getSmsId();
        this.mAddress = "";
        this.mBody = "";
        this.mType = 0;
        this.mState = 0;
        this.mDate = Utils.convertToTime(state.getTimestamp());
        this.mTimestamp = state.getTimestamp();
    }

    // with cursor
    @Ignore
    public Sms(
            final @NonNull Cursor inbox,
            final @NonNull STATUS smsState
    ) {
        final @NonNull String id = inbox.getString(inbox.getColumnIndexOrThrow("_id"));
        this.mId = Integer.parseInt(id);

        final @NonNull String type = inbox.getString(inbox.getColumnIndexOrThrow("type"));
        this.mType = Integer.parseInt(type);

        this.mState = smsState.ordinal();

        this.mAddress = inbox.getString(inbox.getColumnIndexOrThrow("address"));
        this.mBody = inbox.getString(inbox.getColumnIndexOrThrow("body"));

        final @NonNull String date = inbox.getString(inbox.getColumnIndexOrThrow("date"));
        this.mTimestamp = Long.parseLong(date);
        this.mDate = Utils.convertToTime(mTimestamp);
    }

    // nullType
    @Ignore
    public Sms() {
        this.mId = 0;
        this.mAddress = "";
        this.mBody = "";
        this.mType = 0;
        this.mState = 0;
        this.mDate = "";
        this.mTimestamp = 0;
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
        return new Sms();
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
                delimiter + mType + delimiter + mState + delimiter + mDate + "\n";
    }
}
