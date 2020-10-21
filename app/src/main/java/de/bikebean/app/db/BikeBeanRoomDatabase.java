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
import de.bikebean.app.db.sms.SmsFactory;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateDao;
import de.bikebean.app.db.state.StateFactory;
import de.bikebean.app.ui.drawer.log.GithubGistUploader;
import de.bikebean.app.ui.drawer.log.LogViewModel;
import de.bikebean.app.ui.utils.date.DateUtils;
import de.bikebean.app.ui.utils.device.DeviceUtils;

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

    public static void resetAll() {
        final @NonNull SmsDao smsDao = INSTANCE.smsDao();
        final @NonNull StateDao stateDao = INSTANCE.stateDao();
        final @NonNull LogDao logDao = INSTANCE.logDao();

        final @NonNull MutableObject<Sms> smsIsClearedFlag =
                new MutableObject<>(SmsFactory.createNullSms());
        final @NonNull MutableObject<State> stateIsClearedFlag =
                new MutableObject<>(StateFactory.createNullState());
        final @NonNull MutableObject<Log> logIsClearedFlag =
                new MutableObject<>(new Log());

        databaseWriteExecutor.execute(() -> {
            smsDao.deleteAll();
            stateDao.deleteAll();
            logDao.deleteAll();
        });

        smsIsClearedFlag.waitForDelete(smsDao::getAllSync);
        stateIsClearedFlag.waitForDelete(stateDao::getAllSync);
        logIsClearedFlag.waitForDelete(logDao::getAllSync);
    }

    public static @NonNull GithubGistUploader createReport(
            final @NonNull Context ctx, LogViewModel lv,
            final @NonNull GithubGistUploader.UploadSuccessNotifier usn) {
        final @NonNull SmsDao smsDao = INSTANCE.smsDao();
        final @NonNull StateDao stateDao = INSTANCE.stateDao();
        final @NonNull LogDao logDao = INSTANCE.logDao();

        final @NonNull MutableObject<Sms> smsMutableObject =
                new MutableObject<>(SmsFactory.createNullSms());
        final @NonNull MutableObject<State> stateMutableObject =
                new MutableObject<>(StateFactory.createNullState());
        final @NonNull MutableObject<Log> logMutableObject =
                new MutableObject<>(new Log());

        final @NonNull List<? extends DatabaseEntity> smsList =
                smsMutableObject.getAllItems(smsDao::getAllSync);
        final @NonNull List<? extends DatabaseEntity> stateList =
                stateMutableObject.getAllItems(stateDao::getAllSync);
        final List<? extends DatabaseEntity> logList =
                logMutableObject.getAllItems(logDao::getAllSync);

        final @NonNull String description =
                "BikeBeanAppCrashReport " + DateUtils.convertToDateHuman() +
                        " \nVersion: " + DeviceUtils.getVersionName() +
                        " \nID: " + DeviceUtils.getUUID(ctx);
        final @NonNull StringBuilder smsTsv = new StringBuilder();
        final @NonNull StringBuilder stateTsv = new StringBuilder();
        final @NonNull StringBuilder logTsv = new StringBuilder();

        if (smsList.size() > 0) {
            smsTsv.append(smsList.get(0).createReportTitle());

            for (final @NonNull DatabaseEntity sms : smsList)
                smsTsv.append(sms.createReport());
        }

        if (stateList.size() > 0) {
            stateTsv.append(stateList.get(0).createReportTitle());

            for (final @NonNull DatabaseEntity state : stateList)
                stateTsv.append(state.createReport());
        }

        if (logList.size() > 0) {
            logTsv.append(logList.get(0).createReportTitle());

            for (final @NonNull DatabaseEntity log : logList)
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
