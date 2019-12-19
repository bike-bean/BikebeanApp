package de.bikebean.app.ui.status.sms;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import com.lifeofcoding.cacheutlislibrary.CacheUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

import de.bikebean.app.MainActivity;
import de.bikebean.app.R;

public class SmsActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_KEY = 1;

    private ArrayList<HashMap<String, String>> smsList = new ArrayList<>();
    private ArrayList<HashMap<String, String>> tmpList = new ArrayList<>();
    private LoadSms loadSmsTask;
    private ListView listView;

    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sms);

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(SmsActivity.this);
        address = sharedPreferences.getString("number", "");

        listView = findViewById(R.id.listView);

        CacheUtils.configureCache(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        String[] PERMISSIONS = {
                Manifest.permission.READ_SMS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS
        };

        if(!Utils.hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_KEY);
        } else {
            init();
            loadSmsTask = new LoadSms();
            loadSmsTask.execute();
        }
    }

    @SuppressWarnings("unchecked")
    private void init() {
        tmpList.clear();
        try {
            tmpList = (ArrayList<HashMap<String, String>>) Utils.readCachedFile(SmsActivity.this);
            ChatAdapter tmpAdapter = new ChatAdapter(SmsActivity.this, address, tmpList);
            listView.setAdapter(tmpAdapter);
        } catch(Exception e) {
            Log.d(MainActivity.TAG, "SMS Cache file not found, creating it...");
        }
    }

    @SuppressLint("StaticFieldLeak")
    class LoadSms extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            smsList.clear();
        }

        protected String doInBackground(String... args) {
            String xml = "";
            String[] argList = { address };

            try {
                Cursor inbox = getContentResolver().query(
                        Uri.parse("content://sms/inbox"), null,
                        "address=?", argList,
                        null, null);

                Cursor sent = getContentResolver().query(
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

                        smsList.add(Utils.mappingInbox(
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
            Collections.sort(smsList, new Utils.MapComparator(Utils.KEY_TIMESTAMP, "asc"));

            // Updating cache data
            try {
                Utils.createCachedFile (SmsActivity.this, smsList);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {
            if(!tmpList.equals(smsList)) {
                ChatAdapter adapter = new ChatAdapter(SmsActivity.this, address, smsList);
                listView.setAdapter(adapter);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_KEY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init();
                loadSmsTask = new LoadSms();
                loadSmsTask.execute();
            } else {
                Toast.makeText(SmsActivity.this, "You must accept permissions.", Toast.LENGTH_LONG).show();
            }
        }
    }
}


class ChatAdapter extends BaseAdapter {
    private ArrayList<HashMap<String, String>> data;
    private LayoutInflater mInflater;
    private String address;

    ChatAdapter(Activity a, String address, ArrayList<HashMap<String, String>> d) {
        mInflater = LayoutInflater.from(a);
        this.address = address;
        data = d;
    }

    public int getCount() {
        return data.size();
    }
    public Object getItem(int position) {
        return position;
    }
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ChatViewHolder holder;

        if (convertView == null) {
            holder = new ChatViewHolder();
            convertView = mInflater.inflate(R.layout.chat_item, parent, false);

            holder.txtMsgYou = convertView.findViewById(R.id.txtMsgYou);
            holder.timeMsgYou = convertView.findViewById(R.id.timeMsgYou);
            holder.lblMsgFrom = convertView.findViewById(R.id.lblMsgFrom);
            holder.timeMsgFrom = convertView.findViewById(R.id.timeMsgFrom);
            holder.txtMsgFrom = convertView.findViewById(R.id.txtMsgFrom);
            holder.msgFrom = convertView.findViewById(R.id.msgFrom);
            holder.msgYou = convertView.findViewById(R.id.msgYou);

            convertView.setTag(holder);
        } else {
            holder = (ChatViewHolder) convertView.getTag();
        }

        holder.txtMsgYou.setId(position);
        holder.timeMsgYou.setId(position);
        holder.lblMsgFrom.setId(position);
        holder.timeMsgFrom.setId(position);
        holder.txtMsgFrom.setId(position);
        holder.msgFrom.setId(position);
        holder.msgYou.setId(position);

        HashMap<String, String> song;
        song = data.get(position);
        try {
            if (Objects.requireNonNull(song.get(Utils.KEY_TYPE)).contentEquals("1")) {
                holder.lblMsgFrom.setText(String.format("Bike Bean (%s)", address));
                holder.txtMsgFrom.setText(song.get(Utils.KEY_MSG));
                holder.timeMsgFrom.setText(song.get(Utils.KEY_TIME));
                holder.msgFrom.setVisibility(View.VISIBLE);
                holder.msgYou.setVisibility(View.GONE);
            } else {
                holder.txtMsgYou.setText(song.get(Utils.KEY_MSG));
                holder.timeMsgYou.setText(song.get(Utils.KEY_TIME));
                holder.msgFrom.setVisibility(View.GONE);
                holder.msgYou.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }
}


class ChatViewHolder {
    LinearLayout msgFrom, msgYou;
    TextView txtMsgYou, timeMsgYou, lblMsgFrom, txtMsgFrom, timeMsgFrom;
}
