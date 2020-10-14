package de.bikebean.app;

import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import de.bikebean.app.ui.utils.Utils;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.ui.main.status.menu.log.LogViewModel;
import de.bikebean.app.ui.main.status.StateViewModel;
import de.bikebean.app.ui.main.status.StatusFragment;
import de.bikebean.app.ui.main.status.menu.sms_history.SmsViewModel;
import de.bikebean.app.ui.utils.sms.listen.SmsListener;
import de.bikebean.app.ui.utils.sms.parser.SmsParser;

public class MainActivity extends AppCompatActivity {

    public static final @NonNull String TAG = "TAG123";

    // These are ViewModels
    private SmsViewModel smsViewModel;
    private StateViewModel stateViewModel;
    private LogViewModel logViewModel;

    @Override
    protected void onCreate(final @Nullable Bundle savedInstanceState) {
        /*
         init the usual UI stuff
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupNavViewAndActionBar();

        /*
         init the ViewModels
         */
        smsViewModel = new ViewModelProvider(this).get(SmsViewModel.class);
        stateViewModel = new ViewModelProvider(this).get(StateViewModel.class);
        logViewModel = new ViewModelProvider(this).get(LogViewModel.class);

        /*
         observe incoming messages
         */
        smsViewModel.getNewIncoming().observe(this, this::handleNewIncomingMessages);

        SmsListener.setViewModels(smsViewModel, stateViewModel, logViewModel);
    }

    private void setupNavViewAndActionBar() {
        /*
        Setup some UI stuff for the navigation bar at the bottom and the action bar at the top
         */
        final @Nullable BottomNavigationView navView = findViewById(R.id.nav_view);
        final @Nullable NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment);

        if (navView != null)
            NavigationUI.setupWithNavController(navView, navController);

        final @Nullable Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            final @Nullable Drawable overFlowIcon = toolbar.getOverflowIcon();

            if (overFlowIcon != null)
                overFlowIcon.setColorFilter(new PorterDuffColorFilter(
                        ContextCompat.getColor(this, R.color.white),
                        PorterDuff.Mode.SRC_ATOP)
                );
        }

        setSupportActionBar(toolbar);
    }

    private void handleNewIncomingMessages(final @NonNull List<Sms> newSmsList) {
        for (@NonNull Sms newSms : newSmsList)
            new SmsParser(newSms, stateViewModel, smsViewModel, logViewModel).execute();
    }

    @Override
    public void onRequestPermissionsResult(
            final int requestCode,
            final @NonNull String[] permissions,
            final @NonNull int[] grantResults) {
        if (requestCode == Utils.PERMISSION_KEY.SMS.ordinal()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                StatusFragment.permissionGrantedHandler.continueWithPermission();
                StatusFragment.permissionDeniedHandler.continueWithoutPermission(false);
            } else
                StatusFragment.permissionDeniedHandler.continueWithoutPermission(true);
        }
    }
}
