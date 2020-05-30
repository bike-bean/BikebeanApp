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

        private String msg;

        MESSAGE(String msg) {
            this.msg = msg;
        }

        public String getMsg() {
            return msg;
        }

        public void setValue(String msg) {
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

    @Ignore
    public Sms(
            int id,
            @NonNull String address,
            @NonNull String body,
            int type,
            STATUS state,
            @NonNull String date,
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
            @NonNull String address,
            @NonNull String message
    ) {
        long timestamp = System.currentTimeMillis();

        this.mId = smsId - 1;
        this.mAddress = address;
        this.mBody = message;
        this.mType = Telephony.Sms.MESSAGE_TYPE_SENT;
        this.mState = STATUS.NEW.ordinal();
        this.mDate = Utils.convertToTime(timestamp);
        this.mTimestamp = timestamp;
    }

    @Ignore
    public Sms(
            State state
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
            Cursor inbox,
            STATUS smsState
    ) {
        String id = inbox.getString(inbox.getColumnIndexOrThrow("_id"));
        this.mId = Integer.parseInt(id);

        String type = inbox.getString(inbox.getColumnIndexOrThrow("type"));
        this.mType = Integer.parseInt(type);

        this.mState = smsState.ordinal();

        this.mAddress = inbox.getString(inbox.getColumnIndexOrThrow("address"));
        this.mBody = inbox.getString(inbox.getColumnIndexOrThrow("body"));

        String date = inbox.getString(inbox.getColumnIndexOrThrow("date"));
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

    public String getAddress() {
        return this.mAddress;
    }

    public String getBody() {
        return this.mBody;
    }

    public int getType() {
        return this.mType;
    }

    public int getState() {
        return this.mState;
    }

    public String getDate() {
        return this.mDate;
    }

    public long getTimestamp() {
        return this.mTimestamp;
    }

    @Override
    public DatabaseEntity getNullType() {
        return new Sms();
    }

    @Override
    public String createReportTitle() {
        String delimiter = ",";
        return "ID" + delimiter + "Body" + delimiter + "Type" + delimiter +
                "State" + delimiter + "Date" + "\n";
    }

    @Override
    public String createReport() {
        String delimiter = ",";
        return mId + delimiter + mBody.replace("\n", "//") +
                delimiter + mType + delimiter + mState + delimiter + mDate + "\n";
    }
}
