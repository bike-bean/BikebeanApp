package de.bikebean.app.ui.drawer.preferences;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import de.bikebean.app.MainActivity;
import de.bikebean.app.R;
import de.bikebean.app.ui.drawer.log.LogViewModel;
import de.bikebean.app.ui.utils.device.DeviceUtils;

import static de.bikebean.app.ui.drawer.preferences.PreferencesFragmentExtKt.startObservingNewVersion;
import static de.bikebean.app.ui.drawer.preferences.PreferencesFragmentExtKt.startVersionChecker;

public class PreferencesFragment extends Fragment {

    TextView versionNameNew;
    Button downloadNewVersionButton;

    LogViewModel logViewModel;
    PreferencesViewModel preferencesViewModel;

    @Nullable
    @Override
    public View onCreateView(final @NonNull LayoutInflater inflater,
                             final @Nullable ViewGroup container,
                             final @Nullable Bundle savedInstanceState) {
        final @NonNull View v = inflater.inflate(R.layout.fragment_preferences, container, false);

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        logViewModel = new ViewModelProvider(this).get(LogViewModel.class);
        preferencesViewModel = new ViewModelProvider(this).get(PreferencesViewModel.class);

        final @Nullable TextView versionName = v.findViewById(R.id.versionName);
        final @NonNull String versionNameString =
                "Aktuelle Version: " + DeviceUtils.getVersionName();
        if (versionName != null)
            versionName.setText(versionNameString);
        else
            logViewModel.e("Failed to load TextView versionName!");

        versionNameNew = v.findViewById(R.id.versionName2);
        downloadNewVersionButton = v.findViewById(R.id.downloadNewVersion);

        startObservingNewVersion(this);
        startVersionChecker(this);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        final @NonNull MainActivity activity = (MainActivity) requireActivity();
        activity.setToolbarScrollEnabled(false);
        activity.resumeToolbarAndBottomSheet();
    }

}
