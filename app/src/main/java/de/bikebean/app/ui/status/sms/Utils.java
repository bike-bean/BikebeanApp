package de.bikebean.app.ui.status.sms;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


class Utils {

    static final String KEY_MSG = "msg";
    static final String KEY_TYPE = "type";
    static final String KEY_TIMESTAMP = "timestamp";
    static final String KEY_TIME = "time";

    static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    static String convertToTime(String timestamp) {
        long datetime = Long.parseLong(timestamp);
        Date date = new Date(datetime);
        DateFormat formatter = new SimpleDateFormat("dd/MM HH:mm", Locale.GERMANY);
        return formatter.format(date);
    }

    static HashMap<String, String> mappingInbox(
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

    static void createCachedFile(Context context, ArrayList<HashMap<String, String>> dataList) throws IOException {
        FileOutputStream fos = context.openFileOutput("smsapp", Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(dataList);
        oos.close();
        fos.close();
    }

    static Object readCachedFile(Context context) throws IOException, ClassNotFoundException {
        FileInputStream fis = context.openFileInput("smsapp");
        ObjectInputStream ois = new ObjectInputStream(fis);
        return ois.readObject();
    }

    static class MapComparator implements Comparator<HashMap<String, String>> {

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