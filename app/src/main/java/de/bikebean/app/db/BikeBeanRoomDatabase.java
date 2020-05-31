package de.bikebean.app.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.bikebean.app.db.log.Log;
import de.bikebean.app.db.log.LogDao;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.sms.SmsDao;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateDao;
import de.bikebean.app.ui.main.status.menu.log.GithubGistUploader;
import de.bikebean.app.ui.main.status.menu.log.LogViewModel;
import de.bikebean.app.ui.utils.Utils;

@Database(entities = {Sms.class, State.class, Log.class}, version = 11, exportSchema = false)
@TypeConverters({LevelConverters.class})
public abstract class BikeBeanRoomDatabase extends RoomDatabase {

    public abstract SmsDao smsDao();
    public abstract StateDao stateDao();
    public abstract LogDao logDao();

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
        SmsDao smsDao = INSTANCE.smsDao();
        StateDao stateDao = INSTANCE.stateDao();
        LogDao logDao = INSTANCE.logDao();

        final MutableObject<Sms> smsIsClearedFlag = new MutableObject<>(new Sms());
        final MutableObject<State> stateIsClearedFlag = new MutableObject<>(new State());
        final MutableObject<Log> logIsClearedFlag = new MutableObject<>(new Log());

        databaseWriteExecutor.execute(() -> {
            smsDao.deleteAll();
            stateDao.deleteAll();
            logDao.deleteAll();
        });

        smsIsClearedFlag.waitForDelete(smsDao::getAllSync);
        stateIsClearedFlag.waitForDelete(stateDao::getAllSync);
        logIsClearedFlag.waitForDelete(logDao::getAllSync);
    }

    public static GithubGistUploader createReport(Context ctx, LogViewModel lv,
                                                  GithubGistUploader.UploadSuccessNotifier usn) {
        SmsDao smsDao = INSTANCE.smsDao();
        StateDao stateDao = INSTANCE.stateDao();
        LogDao logDao = INSTANCE.logDao();

        final MutableObject<Sms> smsMutableObject = new MutableObject<>(new Sms());
        final MutableObject<State> stateMutableObject = new MutableObject<>(new State());
        final MutableObject<Log> logMutableObject = new MutableObject<>(new Log());

        List<? extends DatabaseEntity> smsList = smsMutableObject.getAllItems(smsDao::getAllSync);
        List<? extends DatabaseEntity> stateList = stateMutableObject.getAllItems(stateDao::getAllSync);
        List<? extends DatabaseEntity> logList = logMutableObject.getAllItems(logDao::getAllSync);

        String description = "BikeBeanAppCrashReport " + Utils.convertToDateHuman();
        StringBuilder smsTsv = new StringBuilder();
        StringBuilder stateTsv = new StringBuilder();
        StringBuilder logTsv = new StringBuilder();

        if (smsList.size() > 0) {
            smsTsv.append(smsList.get(0).createReportTitle());

            for (DatabaseEntity sms : smsList)
                smsTsv.append(sms.createReport());
        }

        if (stateList.size() > 0) {
            stateTsv.append(stateList.get(0).createReportTitle());

            for (DatabaseEntity state: stateList)
                stateTsv.append(state.createReport());
        }

        if (logList.size() > 0) {
            logTsv.append(logList.get(0).createReportTitle());

            for (DatabaseEntity log: logList)
                logTsv.append(log.createReport());
        }

        return new GithubGistUploader(
                ctx, lv, usn,
                description,
                smsTsv.toString(),
                stateTsv.toString(),
                logTsv.toString()
        );
    }
}
