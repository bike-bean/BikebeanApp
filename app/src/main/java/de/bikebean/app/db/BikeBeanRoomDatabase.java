package de.bikebean.app.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.bikebean.app.db.log.Log;
import de.bikebean.app.db.log.LogDao;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.sms.SmsDao;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateDao;

@Database(entities = {Sms.class, State.class, Log.class}, version = 12, exportSchema = false)
@TypeConverters({LevelConverters.class})
public abstract class BikeBeanRoomDatabase extends RoomDatabase {

    public abstract SmsDao smsDao();
    public abstract StateDao stateDao();
    public abstract LogDao logDao();

    static volatile BikeBeanRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static BikeBeanRoomDatabase getDatabase(final @NonNull Context context) {
        if (INSTANCE == null)
            synchronized (BikeBeanRoomDatabase.class) {
                if (INSTANCE == null)
                    INSTANCE = Room.databaseBuilder(context,
                            BikeBeanRoomDatabase.class, "bikebean_database")
                            .addCallback(sRoomDatabaseCallback)
                            .fallbackToDestructiveMigration()
                            .build();
            }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(final @NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            /*
             Code to be executed on
             App restarts goes here!
             */
        }
    };

}
