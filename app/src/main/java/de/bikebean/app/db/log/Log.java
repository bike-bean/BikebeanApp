package de.bikebean.app.db.log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import de.bikebean.app.db.DatabaseEntity;

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
            @NonNull String message,
            LEVEL level
    ) {
        this.mTimestamp = timestamp;
        this.mMessage = message;
        this.mLevel = level;
    }

    @Ignore
    public Log(
            @NonNull String message,
            LEVEL level
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

    public String getMessage() {
        return this.mMessage;
    }

    public long getTimestamp() {
        return this.mTimestamp;
    }

    public LEVEL getLevel() {
        return this.mLevel;
    }

    @Override
    public DatabaseEntity getNullType() {
        return new Log();
    }
}
