package de.bikebean.app.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.sms.SmsDao;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateDao;

@Database(entities = {Sms.class, State.class}, version = 10, exportSchema = false)
public abstract class BikeBeanRoomDatabase extends RoomDatabase {

    public abstract SmsDao smsDao();
    public abstract StateDao stateDao();

    private static volatile BikeBeanRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static BikeBeanRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BikeBeanRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context,
                            BikeBeanRoomDatabase.class, "bikebean_database")
                            .addCallback(sRoomDatabaseCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            // Code to be executed on
            // App restarts goes here!
        }
    };

    public static void resetAll() {
        databaseWriteExecutor.execute(() -> {
            SmsDao dao0 = INSTANCE.smsDao();
            dao0.deleteAll();

            StateDao dao1 = INSTANCE.stateDao();
            dao1.deleteAll();
        });
    }
}
