package de.bikebean.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import de.bikebean.app.ui.drawer.map.MapFragment;
import de.bikebean.app.ui.drawer.status.SubStatusFragmentSmall;
import de.bikebean.app.ui.drawer.status.battery.BatteryStatusFragment;
import de.bikebean.app.ui.drawer.status.battery.BatteryStatusFragmentSmall;
import de.bikebean.app.ui.drawer.status.location.LocationStatusFragment;
import de.bikebean.app.ui.drawer.status.location.LocationStatusFragmentSmall;
import de.bikebean.app.ui.drawer.status.settings.SettingsStatusFragment;
import de.bikebean.app.ui.drawer.status.settings.SettingsStatusFragmentSmall;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.ui.drawer.log.LogViewModel;
import de.bikebean.app.ui.drawer.status.StateViewModel;
import de.bikebean.app.ui.drawer.sms_history.SmsViewModel;
import de.bikebean.app.ui.initialization.InitialConfigurationFragment;
import de.bikebean.app.ui.utils.permissions.PermissionUtils;
import de.bikebean.app.ui.utils.preferences.PreferencesUtils;
import de.bikebean.app.ui.utils.sms.listen.SmsListener;
import de.bikebean.app.ui.utils.sms.parser.SmsParser;

import static de.bikebean.app.ui.drawer.preferences.SettingsFragment.NAME_PREFERENCE;

public class MainActivity extends AppCompatActivity {

    public static final @NonNull String TAG = "TAG123";

    private static final int SMALL = 0;
    private static final int EXPANDED = 1;

    private AppBarConfiguration mAppBarConfiguration;

    private SharedPreferences preferences;

    /* These are ViewModels */
    private SmsViewModel smsViewModel;
    private StateViewModel stateViewModel;
    private LogViewModel logViewModel;

    /* UI elements */
    private @Nullable Toolbar toolbar;
    private FragmentContainerView fragmentContainerView;
    private CardView errorView;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private BottomSheetBehavior<LinearLayout> initialBehavior;
    private NestedScrollView statusScrollView;

    public static PermissionGrantedHandler permissionGrantedHandler;
    public static PermissionDeniedHandler permissionDeniedHandler;

    @Override
    protected void onCreate(final @Nullable Bundle savedInstanceState) {
        /*
         init the usual UI stuff
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setupNavViewAndActionBar();

        /*
         init the preferences
         */
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        /*
         init the bottomSheet and its children
         */
        fragmentContainerView = findViewById(R.id.nav_host_fragment);
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.linearLayout));
        initialBehavior = BottomSheetBehavior.from(findViewById(R.id.initialConfiguration));
        statusScrollView = findViewById(R.id.statusScrollView);

        final @NonNull String bikeName =
                preferences.getString(NAME_PREFERENCE, "Bike Bean");

        final @Nullable Toolbar topPanel = findViewById(R.id.topPanel);
        if (topPanel != null) {
            topPanel.setTitle(bikeName);
            topPanel.setNavigationOnClickListener(v -> resumeToolbarAndBottomSheet());
            topPanel.getViewTreeObserver().addOnGlobalLayoutListener(() ->
                    initialBehavior.setPeekHeight(topPanel.getHeight())
            );
        }

        final @Nullable Toolbar topPanel2 = findViewById(R.id.topPanel2);
        if (topPanel2 != null)
            topPanel2.setTitle(R.string.initial_heading);

        fragmentContainerView.getViewTreeObserver().addOnGlobalLayoutListener(() ->
                bottomSheetBehavior.setPeekHeight(fragmentContainerView.getHeight() / 3)
        );
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    setToolbarScrollEnabled(true);
                    setToolbarVisible(false);
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    setToolbarScrollEnabled(true);
                    setToolbarVisible(true);
                } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    setToolbarScrollEnabled(false);
                    setToolbarVisible(true);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
        });

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.include0, new LocationStatusFragmentSmall())
                .replace(R.id.include1, new BatteryStatusFragmentSmall())
                .replace(R.id.include2, new SettingsStatusFragmentSmall())
                .replace(R.id.include3, new InitialConfigurationFragment())
                .disallowAddToBackStack()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();

        /*
         init the error view
         */
        errorView = findViewById(R.id.errorView);
        final @Nullable Button openSettingsButton = findViewById(R.id.errorViewButton);
        if (openSettingsButton != null)
            openSettingsButton.setOnClickListener(this::openSettings);

        /*
         init "scenes"
         */
        final @NonNull ViewGroup locationRoot = findViewById(R.id.include0);
        final @NonNull ViewGroup batteryRoot = findViewById(R.id.include1);
        final @NonNull ViewGroup settingsRoot = findViewById(R.id.include2);

        locationRoot.setOnClickListener(this::transitionSmallNormal);
        batteryRoot.setOnClickListener(this::transitionSmallNormal);
        settingsRoot.setOnClickListener(this::transitionSmallNormal);

        /*
         init the ViewModels
         */
        smsViewModel = new ViewModelProvider(this).get(SmsViewModel.class);
        stateViewModel = new ViewModelProvider(this).get(StateViewModel.class);
        logViewModel = new ViewModelProvider(this).get(LogViewModel.class);

        permissionDeniedHandler = this::showErrorView;
        permissionGrantedHandler = this::fetchSms;

        /*
         show the initial configuration bottomView if necessary
         */
        if (PreferencesUtils.isInitDone(this)) {
            initialBehavior.setHideable(true);
            initialBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            getPermissions();
        } else {
            initialBehavior.setHideable(false);
            initialBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            if (getSupportActionBar() != null)
                getSupportActionBar().hide();
        }

        setBottomSheetHideable(true);
        setBottomSheetBehaviorState(BottomSheetBehavior.STATE_HIDDEN);

        /*
         observe incoming messages
         */
        smsViewModel.getNewIncoming().observe(this, this::handleNewIncomingMessages);
        if (getIntent().getIntExtra(SmsListener.getNewSmsString(), 0) != 0) {
            smsViewModel.setNewMessagesObserving(false);
            getPermissions();
        } else smsViewModel.setNewMessagesObserving(true);

    }

    private int locationCurrentView = SMALL;
    private int batteryCurrentView = SMALL;
    private int settingsCurrentView = SMALL;

    public void transitionSmallNormal(final @NonNull SubStatusFragmentSmall f) {
        final int switchId;

        if (f.getClass() == LocationStatusFragment.class
                || f.getClass() == LocationStatusFragmentSmall.class)
            switchId = R.id.include0;
        else if (f.getClass() == BatteryStatusFragment.class
                || f.getClass() == BatteryStatusFragmentSmall.class)
            switchId = R.id.include1;
        else if (f.getClass() == SettingsStatusFragment.class
                || f.getClass() == SettingsStatusFragmentSmall.class)
            switchId = R.id.include2;
        else
            switchId = 0;

        transitionSmallNormal(switchId);
    }

    public void transitionSmallNormal(final @NonNull View v) {
        transitionSmallNormal(v.getId());
    }

    public void transitionSmallNormal(final int id) {
        final @Nullable SubStatusFragmentSmall fragment;
        final int switchId;

        switch (id) {
            case R.id.include0:
                if (locationCurrentView == SMALL) {
                    fragment = new LocationStatusFragment();
                    locationCurrentView = EXPANDED;
                } else {
                    fragment = new LocationStatusFragmentSmall();
                    locationCurrentView = SMALL;
                }
                switchId = R.id.include0;
                break;

            case R.id.include1:
                if (batteryCurrentView == SMALL) {
                    fragment = new BatteryStatusFragment();
                    batteryCurrentView = EXPANDED;
                } else {
                    fragment = new BatteryStatusFragmentSmall();
                    batteryCurrentView = SMALL;
                }
                switchId = R.id.include1;
                break;

            case R.id.include2:
                if (settingsCurrentView == SMALL) {
                    fragment = new SettingsStatusFragment();
                    settingsCurrentView = EXPANDED;
                } else {
                    fragment = new SettingsStatusFragmentSmall();
                    settingsCurrentView = SMALL;
                }
                switchId = R.id.include2;
                break;

            default:
                switchId = 0;
                fragment = null;
        }

        if (fragment != null) {
            fragment.setTransitionHappened();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(switchId, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
    }

    private final int BOTTOM_SCROLL_MARGIN = 24;
    private final int TOP_SCROLL_MARGIN = 252;

    public void scrollToStatusFragment(View v) {
        statusScrollView.post(() -> {
            int height = v.getMeasuredHeight();
            int[] currentLocation = new int[2];
            v.getLocationOnScreen(currentLocation);
            int yMax = findViewById(R.id.drawer_layout).getMeasuredHeight() - BOTTOM_SCROLL_MARGIN;

            int yDiff = currentLocation[1] + height - yMax;

            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                setBottomSheetBehaviorState(BottomSheetBehavior.STATE_EXPANDED);
                return;
            }

            if (yDiff > 0) {
                /*
                 * scroll DOWN because bottom of current fragment is BELOW screen bottom
                 */
                statusScrollView.scrollBy(0, 1);
                statusScrollView.smoothScrollBy(0, yDiff - 1);
            } else if (currentLocation[1] < TOP_SCROLL_MARGIN) {
                /*
                 * scroll UP because top of current fragment is ABOVE screen top
                 */
                statusScrollView.scrollBy(0, -1);
                statusScrollView.smoothScrollBy(0, currentLocation[1] - TOP_SCROLL_MARGIN + 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == APPLICATION_SETTINGS)
            getPermissions();
    }

    @Override
    public void onRequestPermissionsResult(
            final int requestCode,
            final @NonNull String[] permissions,
            final @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermissionUtils.KEYS.SMS.ordinal()) {
            if (PermissionUtils.checkResult(grantResults)) {
                permissionDeniedHandler.continueWithoutPermission(false);
                permissionGrantedHandler.continueWithPermission();
            } else
                permissionDeniedHandler.continueWithoutPermission(true);
        }
    }

    private void setupNavViewAndActionBar() {
        /*
        Setup some UI stuff for the action bar at the top
         */
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_map_current, R.id.navigation_map_history,
                //R.id.navigation_wifi,
                R.id.navigation_sms_history,
                R.id.navigation_position_history, R.id.navigation_battery_history,
                R.id.navigation_preferences, R.id.navigation_log
        )
                .setOpenableLayout(drawer)
                .build();

        final @Nullable NavHostFragment navHostFragment = getNavHostFragment();
        final @NonNull NavController navController;

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
        }
    }

    private void handleNewIncomingMessages(final @NonNull List<Sms> newSmsList) {
        if (!smsViewModel.isNewMessagesObserving()) {
            smsViewModel.setNewMessagesObserving(true);
            return;
        }

        for (@NonNull Sms newSms : newSmsList)
            new SmsParser(newSms, stateViewModel, smsViewModel, logViewModel).execute();
    }

    public void setBottomSheetBehaviorState(final int state) {
        if (!bottomSheetBehavior.isHideable() && state == BottomSheetBehavior.STATE_HIDDEN)
            return;

        bottomSheetBehavior.setState(state);
    }

    public void setBottomSheetHideable(final boolean hideable) {
        bottomSheetBehavior.setHideable(hideable);
    }

    public void setToolbarScrollEnabled(final boolean scrollEnabled) {
        if (toolbar == null)
            return;

        final @NonNull AppBarLayout.LayoutParams appLayoutParams =
                (AppBarLayout.LayoutParams) toolbar.getLayoutParams();

        if (scrollEnabled) {
            appLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                    | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
                    | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
            );
        } else {
            appLayoutParams.setScrollFlags(0);
        }

        toolbar.setLayoutParams(appLayoutParams);
    }

    public void resumeToolbarAndBottomSheet() {
        setToolbarVisible(true);
        setBottomSheetBehaviorState(BottomSheetBehavior.STATE_HIDDEN);

        final @Nullable NavHostFragment n = getNavHostFragment();
        if (n != null) {
            final @Nullable Fragment f = n.getChildFragmentManager().getFragments().get(0);
            if (f != null && f.getClass() == MapFragment.class && f.isVisible() )
                ((MapFragment) f).setShareButtonVisible(false);
        }
    }

    private void setToolbarVisible(boolean visible) {
        final @Nullable AppBarLayout appBarLayout = findViewById(R.id.appBar);
        if (appBarLayout != null)
            appBarLayout.setExpanded(visible, true);

        if (toolbar != null)
            toolbar.setVisibility(getVisibilityForBoolean(visible));
    }

    private int getVisibilityForBoolean(boolean visibility) {
        if (visibility)
            return View.VISIBLE;
        else return View.GONE;
    }

    @Override
    public boolean onSupportNavigateUp() {
        final @Nullable NavHostFragment navHostFragment = getNavHostFragment();

        if (navHostFragment != null)
            return NavigationUI.navigateUp(navHostFragment.getNavController(), mAppBarConfiguration)
                    || super.onSupportNavigateUp();

        return false;
    }

    private @Nullable NavHostFragment getNavHostFragment() {
        return (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
    }

    public void navigateTo(final @IdRes int dest, final @Nullable Bundle args) {
        final @Nullable NavHostFragment navHostFragment = getNavHostFragment();

        if (navHostFragment != null)
            navHostFragment.getNavController().navigate(dest, args);
    }

    private void getPermissions() {
        if (PermissionUtils.hasSmsPermissions(this)) {
            permissionGrantedHandler.continueWithPermission();
            permissionDeniedHandler.continueWithoutPermission(false);
        }
    }

    private static final int APPLICATION_SETTINGS = 10;

    private void openSettings(final @NonNull View v) {
        final @NonNull Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        final @NonNull Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, APPLICATION_SETTINGS);
    }

    private void showErrorView(final boolean show) {
        if (show) {
            errorView.setVisibility(View.VISIBLE);
            Snackbar.make(findViewById(android.R.id.content),
                    getString(R.string.warning_sms_permission),
                    Snackbar.LENGTH_LONG
            ).show();
        } else
            errorView.setVisibility(View.GONE);
    }

    private void fetchSms() {
        final @Nullable String number = PreferencesUtils.getBikeBeanNumber(
                preferences, logViewModel
        );

        if (number != null && PreferencesUtils.isInitDone(this))
            smsViewModel.fetchSms(this, stateViewModel, logViewModel, number);
    }

    public boolean isLightTheme() {
        final @NonNull TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(
                R.attr.isLightTheme, typedValue, true
        );
        return typedValue.data != 0;
    }

    public interface PermissionGrantedHandler {
        void continueWithPermission();
    }

    public interface PermissionDeniedHandler {
        void continueWithoutPermission(final boolean show);
    }
}
