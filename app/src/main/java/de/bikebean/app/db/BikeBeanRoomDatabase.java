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
import de.bikebean.app.db.status.Status;
import de.bikebean.app.db.status.StatusDao;

@Database(entities = {Sms.class, Status.class}, version = 5, exportSchema = false)
public abstract class BikeBeanRoomDatabase extends RoomDatabase {

    public abstract SmsDao smsDao();
    public abstract StatusDao statusDao();

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
            // databaseWriteExecutor.execute(() -> {
            //    SmsDao dao = INSTANCE.smsDao();
            //    dao.deleteAll();
            // });
        }
    };
}
