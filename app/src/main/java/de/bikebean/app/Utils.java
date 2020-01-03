package de.bikebean.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.status.Status;
import de.bikebean.app.ui.status.StatusViewModel;
import de.bikebean.app.ui.status.location.LocationUpdater;
import de.bikebean.app.ui.status.sms.SmsViewModel;
import de.bikebean.app.ui.status.sms.parser.SmsParser;

public class Utils extends AsyncTask<String, Void, String> {

    private static final int READ_DONE = 0;
    private static final int NEED_MORE_INFO = 1;

    private final WeakReference<Context> contextReference;
    private final WeakReference<SmsViewModel> smsViewModelReference;
    private final WeakReference<StatusViewModel> statusViewModelReference;

    public Utils(Context context, SmsViewModel smsViewModel, StatusViewModel statusViewModel) {
        contextReference = new WeakReference<>(context);
        smsViewModelReference = new WeakReference<>(smsViewModel);
        statusViewModelReference = new WeakReference<>(statusViewModel);
    }

    @Override
    protected String doInBackground(String... args) {
        Context ctx = contextReference.get();
        SmsViewModel smsViewModel = smsViewModelReference.get();
        StatusViewModel statusViewModel = statusViewModelReference.get();

        checkLastTwoMessages(ctx, smsViewModel, statusViewModel);
        return "";
    }

    private void checkLastTwoMessages(
            Context ctx,
            SmsViewModel smsViewModel,
            StatusViewModel statusViewModel) {
        // Get the last to SMS and update the local database
        List<Sms> l = smsViewModel.getLatestTwoInInbox();

        if (l.size() > 1) {
            Sms item = l.get(0);
            Sms item2 = l.get(1);

            if (updateStatus(ctx, statusViewModel, item, false) == READ_DONE)
                assert true;
            else
                updateStatus(ctx, statusViewModel, item2, true);
        } else if (l.size() > 0) {
            Sms item = l.get(0);

            if (updateStatus(ctx, statusViewModel, item, false) == NEED_MORE_INFO)
                new LocationUpdater(ctx, statusViewModel).execute();
        }
    }

    private int updateStatus(
            Context ctx,
            StatusViewModel statusViewModel,
            Sms item,
            boolean isSecond) {
        if (item != null) {
            if (!isSecond) {
                Date date = new Date(item.getTimestamp());
                Log.d(MainActivity.TAG, "SMS is from " + date.toString());

                if (isNewer(date, statusViewModel)) {
                    Log.d(MainActivity.TAG, "SMS is newer than latest update! Parsing SMS...");
                    SmsParser smsParser = new SmsParser(item.getBody());
                    int type = smsParser.getType();
                    smsParser.updateStatus(ctx, type, statusViewModel);
                    if (type == SmsParser.SMS_TYPE_WIFI_LIST)
                        new LocationUpdater(ctx, statusViewModel).execute();
                    if (type == SmsParser.SMS_TYPE_CELL_TOWERS)
                        return NEED_MORE_INFO;
                } else
                    Log.d(MainActivity.TAG, "SMS is older than latest update.");
            } else {
                SmsParser smsParser = new SmsParser(item.getBody());
                int type = smsParser.getType();
                smsParser.updateStatus(ctx, type, statusViewModel);
                if (type == SmsParser.SMS_TYPE_WIFI_LIST)
                    new LocationUpdater(ctx, statusViewModel).execute();
            }
        }

        return READ_DONE;
    }

    private boolean isNewer(Date d_new, StatusViewModel statusViewModel) {
        List<de.bikebean.app.db.status.Status> l = statusViewModel.getBattery();
        Date d;

        if (l.size() > 0) {
            d = new Date(l.get(0).getTimestamp());
        } else return false;

        Log.d(MainActivity.TAG, "DB Entry is from: " + d.toString());

        return d_new.compareTo(d) >= 0;
    }

    public static boolean isWarningNumberSet(Context ctx) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        String w = sharedPreferences.getString("warningNumber", "");
        boolean b = sharedPreferences.getBoolean("askedForWarningNumber", true);

        return !w.isEmpty() || b;
    }

    static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String convertToTime(long datetime) {
        Date date = new Date(datetime);
        DateFormat formatter = new SimpleDateFormat("dd.MM HH:mm", Locale.GERMANY);
        return formatter.format(date);
    }

    public static String createTransactionID() {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }
}