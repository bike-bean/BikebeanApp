package de.bikebean.app.ui.wifi_localization;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import de.bikebean.app.R;

public class Wifi_localizationFragment extends Fragment {

    private Wifi_localizationViewModel wifi_localizationViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        wifi_localizationViewModel =
                ViewModelProviders.of(this).get(Wifi_localizationViewModel.class);
        View root = inflater.inflate(R.layout.fragment_wifi_localization, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);
        wifi_localizationViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}