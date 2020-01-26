package de.bikebean.app;

import android.Manifest;
import android.content.Intent;
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

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.ui.status.StatusViewModel;
import de.bikebean.app.ui.status.sms.SmsViewModel;
import de.bikebean.app.ui.status.sms.listen.SmsListener;
import de.bikebean.app.ui.status.sms.parser.SmsParser;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "TAG123";

    private static final int REQUEST_PERMISSION_KEY = 1;
    private static final int INITIAL_CONFIGURATION_KEY = 2;

    private SmsViewModel smsViewModel;
    private StatusViewModel statusViewModel;

    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        smsViewModel = new ViewModelProvider(this).get(SmsViewModel.class);
        statusViewModel = new ViewModelProvider(this).get(StatusViewModel.class);

        // Listen for new incoming messages and react to their content
        smsViewModel.getNewIncoming().observe(this, newSmsList -> {
            for (Sms newSms : newSmsList)
                new SmsParser(newSms, getApplicationContext(), statusViewModel,
                        isDatabaseUpdated -> smsViewModel.markParsed(newSms.getId())).execute();
        });

        setupNavView();

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        address = sharedPreferences.getString("number", "");

        setupSmsListener();
        if (address.equals(""))
            startInitialConfiguration();
        else
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

    private void startInitialConfiguration() {
        startActivityForResult(
                new Intent(this, InitialConfigurationActivity.class),
                INITIAL_CONFIGURATION_KEY
        );
    }

    private void fetchSms() {
        String[] permissions = {
                android.Manifest.permission.READ_SMS,
                android.Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS
        };

        if (Utils.hasNoPermissions(this, permissions))
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_KEY);
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

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == INITIAL_CONFIGURATION_KEY)
            if (resultCode == RESULT_OK)
                fetchSms();
            else if (resultCode == RESULT_CANCELED)
                startInitialConfiguration();
    }
}