package de.bikebean.app;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import de.bikebean.app.ui.status.sms.SmsViewModel;
import de.bikebean.app.ui.status.sms.listen.SmsListener;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "BIKE";
    private static final int REQUEST_PERMISSION_KEY = 1;

    private SmsViewModel smsViewModel;

    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        smsViewModel = new ViewModelProvider(this).get(SmsViewModel.class);

        setupNavView();

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        address = sharedPreferences.getString("number", "");

        setupSmsListener();
        fetchSms();
    }

    private void setupNavView() {
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

    private void setupSmsListener() {
        SmsListener.setSmsViewModel(smsViewModel);
    }

    private void fetchSms() {
        String[] PERMISSIONS = {
                android.Manifest.permission.READ_SMS,
                android.Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS
        };

        if (Utils.hasPermissions(this, PERMISSIONS))
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_KEY);
        else
            smsViewModel.fetchSms(this, address, "");
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_KEY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                smsViewModel.fetchSms(this, address, "");
            } else {
                Toast.makeText(this, "You must accept permissions.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
