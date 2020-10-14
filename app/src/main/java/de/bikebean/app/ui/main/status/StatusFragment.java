package de.bikebean.app.ui.main.status;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;

import de.bikebean.app.R;
import de.bikebean.app.ui.utils.PermissionsRationaleDialog;
import de.bikebean.app.ui.utils.Utils;
import de.bikebean.app.ui.main.status.battery.BatteryStatusFragment;
import de.bikebean.app.ui.main.status.location.LocationStatusFragment;
import de.bikebean.app.ui.main.status.menu.log.LogViewModel;
import de.bikebean.app.ui.main.status.menu.sms_history.SmsViewModel;
import de.bikebean.app.ui.main.status.settings.SettingsStatusFragment;

import static de.bikebean.app.ui.main.status.menu.preferences.PreferencesActivity.NUMBER_PREFERENCE;

public class StatusFragment extends Fragment {

    public static @NonNull String[] getSmsPermissions() {
        if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
            return smsPermissionsAndroid8_0;
        } else {
            return smsPermissionsAndroidX_X;
        }
    }

    private static final @NonNull String[] smsPermissionsAndroid8_0 = {
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.RECEIVE_SMS,
            android.Manifest.permission.READ_PHONE_STATE
    };

    private static final @NonNull String[] smsPermissionsAndroidX_X = {
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.RECEIVE_SMS
    };

    // These are ViewModels
    private SmsViewModel smsViewModel;
    private StateViewModel stateViewModel;
    private LogViewModel logViewModel;

    public interface PermissionGrantedHandler {
        void continueWithPermission();
    }

    public interface PermissionDeniedHandler {
        void continueWithoutPermission(boolean show);
    }

    public static PermissionGrantedHandler permissionGrantedHandler;
    public static PermissionDeniedHandler permissionDeniedHandler;

    // UI Elements
    private CardView errorView;

    @Override
    public View onCreateView(final @NonNull LayoutInflater inflater,
                             final @Nullable ViewGroup container,
                             final @Nullable Bundle savedInstanceState) {
        final @NonNull View root = inflater.inflate(R.layout.fragment_status, container, false);

        errorView = root.findViewById(R.id.errorView);

        final @Nullable Button openSettingsButton = root.findViewById(R.id.errorViewButton);
        if (openSettingsButton != null)
            openSettingsButton.setOnClickListener(this::openSettings);

        // init sub-fragments
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.include0, new LocationStatusFragment())
                .replace(R.id.include1, new BatteryStatusFragment())
                .replace(R.id.include2, new SettingsStatusFragment())
                .disallowAddToBackStack()
                .commit();

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onActivityCreated(final @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // init the ViewModels
        smsViewModel = new ViewModelProvider(this).get(SmsViewModel.class);
        stateViewModel = new ViewModelProvider(this).get(StateViewModel.class);
        logViewModel = new ViewModelProvider(this).get(LogViewModel.class);

        // Get Activity
        final @NonNull FragmentActivity act = requireActivity();

        // Show the Action Bar
        final @Nullable ActionBar actionbar = ((AppCompatActivity) act).getSupportActionBar();
        if (actionbar != null) {
            actionbar.show();
            actionbar.setTitle(R.string.status_title);
        }

        permissionDeniedHandler = this::showErrorView;

        act.getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                act.finish();
            }
        });

        final @Nullable String address = PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getString(NUMBER_PREFERENCE, null);

        if (address == null)
            // Navigate to the initial configuration screen
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.initial_configuration_action);
        else
            getPermissions();
    }

    private void getPermissions() {
        permissionGrantedHandler = this::fetchSms;

        final @NonNull FragmentActivity act = requireActivity();

        if (Utils.getPermissions(act, Utils.PERMISSION_KEY.SMS, () ->
                new PermissionsRationaleDialog(act, Utils.PERMISSION_KEY.SMS).show(
                        act.getSupportFragmentManager(),
                        "smsRationaleDialog"
                )
        )) {
            permissionDeniedHandler.continueWithoutPermission(false);
            permissionGrantedHandler.continueWithPermission();
        }
    }

    @Override
    public void onCreateOptionsMenu(final @NonNull Menu menu, final @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.status_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(final @NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_sms_history:
                Navigation.findNavController(requireView())
                        .navigate(R.id.sms_history_action);
                return true;
            case R.id.menu_item_settings:
                Navigation.findNavController(requireView())
                        .navigate(R.id.settings_action);
                return true;
            case R.id.menu_item_history:
                Navigation.findNavController(requireView())
                        .navigate(R.id.history_action);
                return true;
            case R.id.menu_item_info:
                Navigation.findNavController(requireView())
                        .navigate(R.id.log_action);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showErrorView(boolean show) {
        if (show) {
            errorView.setVisibility(View.VISIBLE);
            Snackbar.make(requireView(),
                    getString(R.string.warning_sms_permission),
                    Snackbar.LENGTH_LONG
            ).show();
        } else
            errorView.setVisibility(View.GONE);
    }

    private void openSettings(final @NonNull View v) {
        if (v.getId() == 0)
            return;

        final @NonNull Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        final @NonNull Uri uri =
                Uri.fromParts("package", requireActivity().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void fetchSms() {
        final @NonNull Context ctx = requireContext();
        final @Nullable String number = PreferenceManager.getDefaultSharedPreferences(ctx)
                .getString(NUMBER_PREFERENCE, null);

        if (number != null)
            smsViewModel.fetchSms(ctx, stateViewModel, logViewModel, number);
        else
            logViewModel.e("Failed to load BB-number! Maybe it's not set?");
    }
}
