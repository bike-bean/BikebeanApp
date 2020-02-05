package de.bikebean.app;

import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.ui.status.StateViewModel;
import de.bikebean.app.ui.status.sms.SmsViewModel;
import de.bikebean.app.ui.status.sms.listen.SmsListener;
import de.bikebean.app.ui.status.sms.parser.SmsParser;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "TAG123";

    // These are ViewModels
    private SmsViewModel smsViewModel;
    private StateViewModel stateViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // init the usual UI stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupNavViewAndActionBar();

        initPreferences();

        // init the ViewModels
        smsViewModel = new ViewModelProvider(this).get(SmsViewModel.class);
        stateViewModel = new ViewModelProvider(this).get(StateViewModel.class);

        setupObservers();
        setupSmsListener();

        if (address.equals(""))
            // Navigate to the initial configuration screen
            Navigation.findNavController(this, R.id.nav_host_fragment)
                .navigate(R.id.initial_configuration_action);
        else
            fetchSms();
    }

    private void setupNavViewAndActionBar() {
        /*
        Setup some UI stuff for the navigation bar at the bottom and the action bar at the top
         */
        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupWithNavController(navView, navController);

        Toolbar toolbar = findViewById(R.id.toolbar);
        Objects.requireNonNull(toolbar.getOverflowIcon()).setColorFilter(
                ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP
        );
        setSupportActionBar(toolbar);
    }

    // These fields represent Preferences:
    // - phone number of Bike Bean -> address
    private String address;
    // - If the app gets started for the first time -> initialLoading
    //   (This is for avoiding an overhaul of new messages at the start of the app,
    //    which happens if the user has some older messages from the bike bean)
    private boolean initialLoading;

    private void fetchSms() {
        /*
        Load the messages from the phone's message storage into the App-internal DB.
        */
        initPreferences();

        smsViewModel.fetchSms(
                this,
                stateViewModel,
                address,
                "",
                String.valueOf(initialLoading)
        );
    }

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
                new SmsParser(newSms, stateViewModel, isDatabaseUpdated ->
                        smsViewModel.markParsed(newSms.getId())).execute();
        });
    }

    private void setupSmsListener() {
        /*
        Setup the Sms Listener
        (it needs references to our ViewModels
        so that it can save the newly arrived message(s))
         */
        SmsListener.setSmsViewModel(smsViewModel);
        SmsListener.setStatusViewModel(stateViewModel);
    }
}
