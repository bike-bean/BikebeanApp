package de.bikebean.app.db.log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import de.bikebean.app.db.DatabaseEntity;
import de.bikebean.app.ui.utils.date.DateUtils;

@Entity(tableName = "log_table")
public class Log extends DatabaseEntity {

    public enum LEVEL {
        INTERNAL, DEBUG, INFO, WARNING, ERROR
    }

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "timestamp")
    private final long mTimestamp;

    @NonNull
    @ColumnInfo(name = "message")
    private final String mMessage;

    @ColumnInfo(name = "level")
    private final LEVEL mLevel;

    public Log(
            long timestamp,
            final @NonNull String message,
            final @NonNull LEVEL level
    ) {
        this.mTimestamp = timestamp;
        this.mMessage = message;
        this.mLevel = level;
    }

    @Ignore
    public Log(
            final @NonNull String message,
            final @NonNull LEVEL level
    ) {
        this.mTimestamp = System.currentTimeMillis();
        this.mMessage = message;
        this.mLevel = level;
    }

    // nullType
    @Ignore
    public Log() {
        this.mTimestamp = 0;
        this.mMessage = "";
        this.mLevel = LEVEL.DEBUG;
    }

    public @NonNull String getMessage() {
        return this.mMessage;
    }

    public long getTimestamp() {
        return this.mTimestamp;
    }

    public @NonNull LEVEL getLevel() {
        return this.mLevel;
    }

    @Override
    public @NonNull DatabaseEntity getNullType() {
        return new Log();
    }

    @Override
    public @NonNull String createReportTitle() {
        final @NonNull String delimiter = "\t";
        return "ID" + delimiter + "Message" + delimiter +
                "Date" + delimiter + "level" + "\n";
    }

    @Override
    public @NonNull String createReport() {
        final @NonNull String delimiter = "\t";
        return id + delimiter + mMessage.replace("\n", "//") +
                delimiter + DateUtils.convertToTimeLog(mTimestamp) + delimiter + mLevel + "\n";
    }
}
