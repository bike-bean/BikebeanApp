package de.bikebean.app;

import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.Objects;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.ui.status.StateViewModel;
import de.bikebean.app.ui.status.StatusFragment;
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

        // init the ViewModels
        smsViewModel = new ViewModelProvider(this).get(SmsViewModel.class);
        stateViewModel = new ViewModelProvider(this).get(StateViewModel.class);

        smsViewModel.getNewIncoming().observe(this, this::handleNewIncomingMessages);

        SmsListener.setSmsViewModel(smsViewModel);
        SmsListener.setStatusViewModel(stateViewModel);
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

    private void handleNewIncomingMessages(List<Sms> newSmsList) {
        for (Sms newSms : newSmsList)
            new SmsParser(newSms, stateViewModel, smsViewModel).execute();
    }


    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode == Utils.PERMISSION_KEY.SMS.ordinal()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                StatusFragment.permissionGrantedHandler.continueWithPermission();
                StatusFragment.permissionDeniedHandler.continueWithoutPermission(false);
            } else
                StatusFragment.permissionDeniedHandler.continueWithoutPermission(true);
        }
    }
}
