package de.bikebean.app.db;

import androidx.annotation.NonNull;
import androidx.room.TypeConverter;

import de.bikebean.app.db.log.Log;

public class LevelConverters {
    @TypeConverter
    public static @NonNull Log.LEVEL fromIntValue(int value) {
        for (final @NonNull Log.LEVEL l : Log.LEVEL.values())
            if (l.ordinal() == value)
                return l;

        return Log.LEVEL.DEBUG;
    }

    @TypeConverter
    public static int levelToIntValue(final @NonNull Log.LEVEL level) {
        return level.ordinal();
    }
}
