package de.bikebean.app.ui.status;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import java.util.Objects;

import de.bikebean.app.R;
import de.bikebean.app.Utils;
import de.bikebean.app.ui.status.battery.BatteryStatusFragment;
import de.bikebean.app.ui.status.location.LocationStatusFragment;
import de.bikebean.app.ui.status.menu.log.LogViewModel;
import de.bikebean.app.ui.status.menu.sms_history.SmsViewModel;
import de.bikebean.app.ui.status.status.StatusStatusFragment;

public class StatusFragment extends Fragment {

    public static final String[] smsPermissions = {
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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_status, container, false);

        errorView = root.findViewById(R.id.errorView);

        Button openSettingsButton = root.findViewById(R.id.errorViewButton);
        openSettingsButton.setOnClickListener(this::openSettings);

        // init sub-fragments
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.include0, new LocationStatusFragment())
                .replace(R.id.include1, new BatteryStatusFragment())
                .replace(R.id.include2, new StatusStatusFragment())
                .disallowAddToBackStack()
                .commit();

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // init the ViewModels
        smsViewModel = new ViewModelProvider(this).get(SmsViewModel.class);
        stateViewModel = new ViewModelProvider(this).get(StateViewModel.class);
        logViewModel = new ViewModelProvider(this).get(LogViewModel.class);

        // Get Activity
        FragmentActivity act = requireActivity();

        // Show the Action Bar
        ActionBar actionbar = ((AppCompatActivity) act).getSupportActionBar();
        Objects.requireNonNull(actionbar).show();
        actionbar.setTitle(R.string.status_text);

        permissionDeniedHandler = this::showErrorView;

        act.getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                act.finish();
            }
        });

        String address = PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getString("number", "");

        if (address.equals(""))
            // Navigate to the initial configuration screen
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.initial_configuration_action);
        else
            getPermissions();
    }

    private void getPermissions() {
        permissionGrantedHandler = this::fetchSms;

        FragmentActivity act = requireActivity();

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
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.status_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_sms_history:
                Navigation.findNavController(Objects.requireNonNull(getView()))
                        .navigate(R.id.sms_history_action);
                return true;
            case R.id.menu_item_settings:
                Navigation.findNavController(Objects.requireNonNull(getView()))
                        .navigate(R.id.settings_action);
                return true;
            case R.id.menu_item_history:
                Navigation.findNavController(Objects.requireNonNull(getView()))
                        .navigate(R.id.history_action);
                return true;
            case R.id.menu_item_log:
                Navigation.findNavController(Objects.requireNonNull(getView()))
                        .navigate(R.id.log_action);
                return true;
            // case R.id.menu_item_licenses:
                // startActivity(new Intent(this, OssLicensesMenuActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showErrorView(boolean show) {
        if (show) {
            errorView.setVisibility(View.VISIBLE);
            Toast.makeText(requireContext(),
                    getString(R.string.warning_sms_permission),
                    Toast.LENGTH_LONG
            ).show();
        } else
            errorView.setVisibility(View.GONE);
    }

    private void openSettings(View v) {
        if (v.getId() == 0)
            return;

        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void fetchSms() {
        Context ctx = requireContext();

        smsViewModel.fetchSms(ctx, stateViewModel, logViewModel,
                PreferenceManager.getDefaultSharedPreferences(ctx)
                        .getString("number", ""), ""
        );
    }
}
