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

    // These are ViewModels
    private SmsViewModel smsViewModel;
    private StatusViewModel statusViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // init the usual UI stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupNavView();

        initPreferences();

        // init the ViewModels
        smsViewModel = new ViewModelProvider(this).get(SmsViewModel.class);
        statusViewModel = new ViewModelProvider(this).get(StatusViewModel.class);

        setupObservers();
        setupSmsListener();

        if (address.equals(""))
            startInitialConfiguration();
        else
            fetchSms();
    }

    private void setupNavView() {
        /*
        Setup some UI stuff for the navigation bar at the bottom
         */
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

    // These fields represent Preferences:
    // - phone number of Bike Bean -> address
    private String address;
    // - If the app gets started for the first time -> initialLoading
    //   (This is for avoiding an overhaul of new messages at the start of the app,
    //    which happens if the user has some older messages from the bike bean)
    private boolean initialLoading;

    private void initPreferences() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        address = sharedPreferences.getString(
                "number", ""
        );
        initialLoading = sharedPreferences.getBoolean(
                "initialLoading", true
        );
    }

    private void setupObservers() {
        /*
        Setup the listeners on our ViewModels

        (Only check for new incoming messages here)
         */

        // Listen for new incoming messages and react to their content
        smsViewModel.getNewIncoming().observe(this, newSmsList -> {
            for (Sms newSms : newSmsList)
                new SmsParser(newSms, getApplicationContext(), statusViewModel,
                        isDatabaseUpdated -> smsViewModel.markParsed(newSms.getId())).execute();
        });
    }

    private void setupSmsListener() {
        /*
        Setup the Sms Listener
        (it needs references to our ViewModels
        so that it can save the newly arrived message(s))
         */
        SmsListener.setSmsViewModel(smsViewModel);
        SmsListener.setStatusViewModel(statusViewModel);
    }

    private static final int INITIAL_CONFIGURATION_KEY = 2;

    private void startInitialConfiguration() {
        /*
        Start the initial configuration screen.
         */
        startActivityForResult(
                new Intent(this, InitialConfigurationActivity.class),
                INITIAL_CONFIGURATION_KEY
        );
    }

    private static final int REQUEST_PERMISSION_KEY = 1;

    private void fetchSms() {
        /*
        Load the messages from the phone's message storage into the App-internal DB.

        Before that, take care if the user has granted the necessary permissions.
         */
        String[] permissions = {
                android.Manifest.permission.READ_SMS,
                android.Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS
        };

        if (Utils.hasNoPermissions(this, permissions)) {
            // TODO: display a dialog explaining the need of SMS permissions.
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_KEY);
        }
        else
            smsViewModel.fetchSms(
                    this,
                    statusViewModel,
                    address,
                    "",
                    String.valueOf(initialLoading)
            );
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        /*
        This is executed after the user has decided if he wants to grant permissions to the App.

        If successful, start with fetching the messages.
        If not, display a toast noting the user needs to accept. Then prompt the user again!
        TODO: make the prompt translatable!
         */
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // final String prompt = "You must accept permissions!";
        final String prompt = "Die App wird ohne Berechtigung nicht funktionieren!";

        if (requestCode == REQUEST_PERMISSION_KEY) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, prompt, Toast.LENGTH_LONG).show();
            fetchSms();
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {
        /*
        This is executed after the initial configuration screen
        (where the user is prompted to enter their bike bean's phone number)

        If successful, start with fetching the messages.
        If not, re-show the initial configuration screen.
         */
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == INITIAL_CONFIGURATION_KEY)
            if (resultCode == RESULT_OK)
                fetchSms();
            else if (resultCode == RESULT_CANCELED)
                startInitialConfiguration();
    }
}
