package de.bikebean.app;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;

import androidx.core.app.ActivityCompat;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Utils {

    public static final String KEY_MSG = "msg";
    public static final String KEY_TYPE = "type";
    public static final String KEY_TIME = "time";
    private static final String KEY_TIMESTAMP = "timestamp";

    public static void doReadSMS(
            String address,
            ContentResolver contentResolver,
            ArrayList<HashMap<String, String>> smsList) {
        String[] argList = { address };

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
            Cursor c = new MergeCursor(new Cursor[]{inbox,sent});

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

        // Arranging sms by timestamp descending
        Collections.sort(smsList, new MapComparator(KEY_TIMESTAMP, "asc"));
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