package de.bikebean.app.ui.drawer.preferences;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import de.bikebean.app.MainActivity;
import de.bikebean.app.R;
import de.bikebean.app.ui.drawer.log.LogViewModel;
import de.bikebean.app.ui.utils.device.DeviceUtils;

import static android.content.Intent.*;

public class PreferencesFragment extends Fragment {

    private TextView versionNameNew;
    private Button downloadNewVersionButton;

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

        final @NonNull LogViewModel logViewModel =
                new ViewModelProvider(this).get(LogViewModel.class);
        final @NonNull PreferencesViewModel preferencesViewModel =
                new ViewModelProvider(this).get(PreferencesViewModel.class);

        final @Nullable TextView versionName = v.findViewById(R.id.versionName);
        final @NonNull String versionNameString =
                "Aktuelle Version: " + DeviceUtils.getVersionName();
        if (versionName != null)
            versionName.setText(versionNameString);
        else
            logViewModel.e("Failed to load TextView versionName!");

        versionNameNew = v.findViewById(R.id.versionName2);
        downloadNewVersionButton = v.findViewById(R.id.downloadNewVersion);
        preferencesViewModel.getNewVersion().observe(getViewLifecycleOwner(), setVersionNameNew);

        new VersionChecker(
                requireContext(),
                logViewModel, preferencesViewModel,
                this::newerVersionHandler
        ).execute();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        ((MainActivity) requireActivity()).setToolbarScrollEnabled(false);
    }

    private final @NonNull Observer<Release> setVersionNameNew = s -> {
        if (s.getUrl().equals(""))
            return;

        final @NonNull String versionNameString = "Neueste Version: " + s.getName();

        versionNameNew.setVisibility(View.VISIBLE);
        versionNameNew.setText(versionNameString);
        downloadNewVersionButton.setVisibility(View.VISIBLE);
        downloadNewVersionButton.setOnClickListener(v -> downloadNewVersion(s.getUrl()));
    };

    void newerVersionHandler(final @NonNull Release release) {
        new NewVersionDialog(
                requireActivity(), release,
                this::downloadNewVersion, this::cancelNewVersionDownload
        ).show(getChildFragmentManager(),"newVersionDialog");
    }

    void downloadNewVersion(final @NonNull String url) {
        final @NonNull Intent intent = new Intent(ACTION_VIEW, Uri.parse(url));
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null)
            startActivity(intent);
    }

    void cancelNewVersionDownload() {
        assert true;
    }
}
