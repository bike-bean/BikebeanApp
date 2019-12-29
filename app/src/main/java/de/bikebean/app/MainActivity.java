package de.bikebean.app;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lifeofcoding.cacheutlislibrary.CacheUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "BIKE";

    public static ArrayList<HashMap<String, String>> smsList = new ArrayList<>();

    private static final int REQUEST_PERMISSION_KEY = 1;

    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        address = sharedPreferences.getString("number", "");

        CacheUtils.configureCache(this);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_status, R.id.navigation_map, R.id.navigation_wifi)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
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
            new LoadSms(this).execute(address);
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
                new LoadSms(this).execute(address);
            } else {
                Toast.makeText(MainActivity.this, "You must accept permissions.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private static final class LoadSms extends AsyncTask<String, Void, String> {

        private WeakReference<MainActivity> activityReference;

        // only retain a weak reference to the activity
        LoadSms(MainActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            smsList.clear();
        }

        protected String doInBackground(String... args) {
            MainActivity act = activityReference.get();
            String address = args[0];

            Utils.doReadSMS(address, act, smsList);

            // Updating cache data
            try {
                Utils.createCachedFile(act, smsList);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return address;
        }
    }
}


