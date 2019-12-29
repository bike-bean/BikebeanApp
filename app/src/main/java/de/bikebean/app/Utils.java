package de.bikebean.app;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import de.bikebean.app.ui.status.sms.parser.SmsParser;

public class Utils {

    public static final String KEY_MSG = "msg";
    public static final String KEY_TYPE = "type";
    public static final String KEY_TIME = "time";
    private static final String KEY_TIMESTAMP = "timestamp";

    public static void doReadSMS(
            String address,
            Activity act,
            ArrayList<HashMap<String, String>> smsList) {
        String[] argList = {address};
        Context ctx = act.getApplicationContext();
        ContentResolver contentResolver = act.getContentResolver();
        SmsParser smsParser = new SmsParser();

        try {
            Cursor inbox = contentResolver.query(
                    Uri.parse("content://sms/inbox"), null,
                    "address=?", argList,
                    null, null);

            Cursor sent = contentResolver.query(
                    Uri.parse("content://sms/sent"), null,
                    "address=?", argList,
                    null, null);

            // Attaching inbox and sent sms
            Cursor c = new MergeCursor(new Cursor[]{inbox, sent});

            if (c.moveToFirst()) {
                for (int i = 0; i < c.getCount(); i++) {
                    String phone = c.getString(c.getColumnIndexOrThrow("address"));
                    String _id = c.getString(c.getColumnIndexOrThrow("_id"));
                    String thread_id = c.getString(c.getColumnIndexOrThrow("thread_id"));
                    String msg = c.getString(c.getColumnIndexOrThrow("body"));
                    String type = c.getString(c.getColumnIndexOrThrow("type"));
                    String timestamp = c.getString(c.getColumnIndexOrThrow("date"));

                    smsList.add(mappingInbox(
                            _id, thread_id, phone,
                            msg, type, timestamp,
                            Utils.convertToTime(timestamp)));
                    c.moveToNext();
                }
            }
            c.close();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        Collections.sort(smsList, new MapComparator(KEY_TIMESTAMP, "asc"));

        // Get latest SMS and check if latest update is newer
        if (!smsList.isEmpty()) {
            HashMap<String, String> item;

            for (int i = 1; ;i++) {
                item = smsList.get(smsList.size() - i);
                if (Objects.equals(item.get("type"), "1"))
                    break;
            }

            Date date = new Date(Long.parseLong(Objects.requireNonNull(item.get("timestamp"))));
            DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.GERMANY);
            Log.d(MainActivity.TAG, "SMS is from " + formatter.format(date));

            if (isNewer(ctx, date)) {
                Log.d(MainActivity.TAG, "SMS is newer than latest update! Parsing SMS...");
                smsParser.updateStatus(ctx, item.get("msg"));
            } else {
                Log.d(MainActivity.TAG, "SMS is older than latest update.");
            }
        }
    }

    private static boolean isNewer(Context ctx, Date d_new) {
        final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.GERMANY);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);

        String s = sharedPreferences.getString("batteryLastChange", "01.01.1990 00:00:00");
        Log.d(MainActivity.TAG, "DB Entry is from: " + s);

        try {
            Date d_old = dateFormat.parse(s);
            return d_new.compareTo(d_old) >= 0;
        } catch(ParseException e) {
            Log.d(MainActivity.TAG, "Parser Error!");
            return true;
        }
    }

    public static boolean isLatLngUpdated(Context ctx) {
        final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.GERMANY);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);

        String s1 = sharedPreferences.getString("locationLastChange", "01.01.1990 00:00:00");
        String s2 = sharedPreferences.getString("latLngLastChange", "02.01.1990 00:00:00");
        Log.d(MainActivity.TAG, "Location last update: " + s1);
        Log.d(MainActivity.TAG, "Lat/Lng last update: " + s2);

        try {
            Date d_old = dateFormat.parse(s1);
            Date d_new = dateFormat.parse(s2);
            if (d_new != null) {
                return d_new.compareTo(d_old) >= 0;
            } else {
                return false;
            }
        } catch(ParseException e) {
            Log.d(MainActivity.TAG, "Parser Error!");
            return false;
        }
    }

    public static boolean isWarningNumberSet(Context ctx) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        String w = sharedPreferences.getString("warningNumber", "");
        boolean b = sharedPreferences.getBoolean("askedForWarningNumber", true);

        return !w.isEmpty() || b;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String convertToTime(String timestamp) {
        long datetime = Long.parseLong(timestamp);
        Date date = new Date(datetime);
        DateFormat formatter = new SimpleDateFormat("dd/MM HH:mm", Locale.GERMANY);
        return formatter.format(date);
    }

    private static HashMap<String, String> mappingInbox(
            String _id, String thread_id, String phone,
            String msg, String type,
            String timestamp, String time) {
        HashMap<String, String> map = new HashMap<>();
        String _ID = "_id";
        String KEY_THREAD_ID = "thread_id";
        String KEY_PHONE = "phone";

        map.put(_ID, _id);
        map.put(KEY_THREAD_ID, thread_id);
        map.put(KEY_PHONE, phone);
        map.put(KEY_MSG, msg);
        map.put(KEY_TYPE, type);
        map.put(KEY_TIMESTAMP, timestamp);
        map.put(KEY_TIME, time);

        return map;
    }

    public static void createCachedFile(Context context, ArrayList<HashMap<String, String>> dataList) throws IOException {
        FileOutputStream fos = context.openFileOutput("smsapp", Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(dataList);
        oos.close();
        fos.close();
    }

    public static Object readCachedFile(Context context) throws IOException, ClassNotFoundException {
        FileInputStream fis = context.openFileInput("smsapp");
        ObjectInputStream ois = new ObjectInputStream(fis);
        return ois.readObject();
    }

    public static class MapComparator implements Comparator<HashMap<String, String>> {

        private final String key;
        private final String order;

        MapComparator(String key, String order) {
            this.key = key;
            this.order = order;
        }

        public int compare(HashMap<String, String> first,
                           HashMap<String, String> second) {
            String firstValue = first.get(key);
            String secondValue = second.get(key);
            if (firstValue != null && secondValue != null) {
                if(this.order.toLowerCase().contentEquals("asc"))
                    return firstValue.compareTo(secondValue);
                else
                    return secondValue.compareTo(firstValue);
            } else
                return 0;
        }
    }
}