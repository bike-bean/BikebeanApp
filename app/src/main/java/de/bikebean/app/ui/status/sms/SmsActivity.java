package de.bikebean.app.ui.status.sms;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import de.bikebean.app.MainActivity;
import de.bikebean.app.R;
import de.bikebean.app.Utils;

public class SmsActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_KEY = 1;

    private static ArrayList<HashMap<String, String>> tmpList = new ArrayList<>();
    private static ArrayList<HashMap<String, String>> smsList = new ArrayList<>();

    private String address;

    // Ui Elements
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sms);

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(SmsActivity.this);
        address = sharedPreferences.getString("number", "");

        listView = findViewById(R.id.listView);
    }

    @Override
    public void onResume() {
        super.onResume();

        String[] PERMISSIONS = {
                android.Manifest.permission.READ_SMS,
                android.Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS
        };

        if(Utils.hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_KEY);
        } else {
            init();
            new LoadSms(this).execute(address);
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

        if(!tmpList.equals(smsList)) {
            ChatAdapter adapter = new ChatAdapter(SmsActivity.this, address, smsList);
            listView.setAdapter(adapter);
        }
    }

    private static class LoadSms extends AsyncTask<String, Void, String> {

        private WeakReference<SmsActivity> activityReference;

        // only retain a weak reference to the activity
        LoadSms(SmsActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            smsList.clear();
        }

        protected String doInBackground(String... args) {
            SmsActivity act = activityReference.get();
            String address = args[0];

            Utils.doReadSMS(address, act.getContentResolver(), smsList);

            // Updating cache data
            try {
                Utils.createCachedFile(act, smsList);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return address;
        }

        @Override
        protected void onPostExecute(String address) {
            SmsActivity act = activityReference.get();

            if(!tmpList.equals(smsList)) {
                ChatAdapter adapter = new ChatAdapter(act, address, smsList);
                act.listView.setAdapter(adapter);
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
                new LoadSms(this).execute(address);
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
