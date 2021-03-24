package de.bikebean.app.ui.drawer.status.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import de.bikebean.app.R;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.drawer.status.LastChangedView;
import de.bikebean.app.ui.drawer.status.ProgressView;
import de.bikebean.app.ui.drawer.status.SubStatusFragment;

public class SettingsStatusFragment extends SubStatusFragment implements SettingsElementsSetter {

    SettingsStateViewModel st;

    // UI Elements
    private TextView titleText;
    private ImageView backButton, helpButton;

    private SettingsStatusWlanView settingsStatusWlanView;
    private SettingsStatusIntervalView settingsStatusIntervalView;
    private SettingsStatusWarningNumberView settingsStatusWarningNumberView;

    private List<? extends SettingsStatusSubView> settingsStatusSubViews;

    @NonNull
    @Override
    public View onCreateView(final @NonNull LayoutInflater inflater,
                             final @Nullable ViewGroup container,
                             final @Nullable Bundle savedInstanceState) {
        final @NonNull View v = inflater.inflate(
                R.layout.fragment_status_settings, container, false
        );

        lastChangedView = new LastChangedView(
                v.findViewById(R.id.lastChangedText),
                v.findViewById(R.id.lastChangedIndicator)
        );

        titleText = v.findViewById(R.id.titleText);
        backButton = v.findViewById(R.id.moreInfoButton);
        helpButton = v.findViewById(R.id.helpButton);

        final @NonNull ProgressBar progressBar = v.findViewById(R.id.progressBar);

        final @NonNull ProgressView wlanProgressView = new ProgressView(
                v.findViewById(R.id.wlanPendingStatus), progressBar
        );
        final @NonNull ProgressView intervalProgressView = new ProgressView(
                v.findViewById(R.id.intervalPendingStatus), progressBar
        );
        final @NonNull ProgressView warningNumberProgressView = new ProgressView(
                v.findViewById(R.id.warningNumberPendingStatus), progressBar
        );

        settingsStatusWlanView = new SettingsStatusWlanView(
                v.findViewById(R.id.wlanCardView),
                v.findViewById(R.id.wlanImage),
                v.findViewById(R.id.wlanSwitch),
                v.findViewById(R.id.wlanSummary),
                wlanProgressView
        );
        settingsStatusIntervalView = new SettingsStatusIntervalView(
                v.findViewById(R.id.intervalDropdown),
                v.findViewById(R.id.intervalSummary),
                v.findViewById(R.id.nextUpdateEstimation),
                intervalProgressView,
                v.findViewById(R.id.intervalImage),
                v.findViewById(R.id.intervalCardView)
        );
        settingsStatusWarningNumberView = new SettingsStatusWarningNumberView(
                v.findViewById(R.id.sendButton),
                v.findViewById(R.id.warningNumberSummary),
                warningNumberProgressView,
                v.findViewById(R.id.warningNumberImage),
                v.findViewById(R.id.warningNumberCardView)
        );

        settingsStatusSubViews = new ArrayList<SettingsStatusSubView>() {{
            add(settingsStatusWlanView);
            add(settingsStatusIntervalView);
            add(settingsStatusWarningNumberView);
        }};

        return v;
    }

    @Override
    public void onActivityCreated(final @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        settingsStatusIntervalView.initIntervalDropdown(this);
    }

    @Override
    protected void setupListeners(final @NonNull LifecycleOwner l) {
        st = new ViewModelProvider(this).get(SettingsStateViewModel.class);
        st.getStatus().observe(l, this::setElements);

        for (SettingsStatusSubView s : settingsStatusSubViews)
            s.setupListeners(l, this);
    }

    @Override
    protected void initUserInteractionElements() {
        // React to user interactions
        helpButton.setOnClickListener(this::onHelpClick);

        initTransitionButton(backButton, helpButton, this, false);
        titleText.setText(R.string.heading_settings);

        for (SettingsStatusSubView s : settingsStatusSubViews)
            s.initUserInteractionElements(this);
    }

    @Override
    protected void resetElements() {
        for (SettingsStatusSubView s : settingsStatusSubViews)
            s.resetElements(this);
    }

    // unset
    @Override
    public void setWarningNumberElementsUnset() {
        settingsStatusWarningNumberView.setWarningNumberElementsUnset(this);
    }

    @Override
    public void setIntervalElementsUnset(final @NonNull State state) {
        settingsStatusIntervalView.setIntervalElementsUnset(state, this);
    }

    @Override
    public void setStatusElementsUnset() {
        lastChangedView.set(null, this);
    }

    // confirmed
    @Override
    public void setIntervalElementsConfirmed(final @NonNull State state) {
        settingsStatusIntervalView.setIntervalElementsConfirmed(state, this);
    }

    @Override
    public void setWifiElementsConfirmed(final @NonNull State state) {
        settingsStatusWlanView.setWifiElementsConfirmed(state, this);
    }

    @Override
    public void setWarningNumberElementsConfirmed(final @NonNull State state) {
        settingsStatusWarningNumberView.setWarningNumberElementsConfirmed(state, this);
    }

    @Override
    public void setStatusElementsConfirmed(final @NonNull State state) {
        lastChangedView.set(state, this);
    }

    // pending
    @Override
    public void setIntervalElementsPending(final @NonNull State state) {
        settingsStatusIntervalView.setIntervalElementsPending(state, this);
    }

    @Override
    public void setWifiElementsPending(final @NonNull State state) {
        settingsStatusWlanView.setWifiElementsPending(state, this);
    }

    @Override
    public void setWarningNumberElementsPending(final @NonNull State state) {
        settingsStatusWarningNumberView.setWarningNumberElementsPending(state, this);
    }

    private void onHelpClick(final @NonNull View v) {
        Snackbar.make(
                v,
                R.string.text_help_settings,
                Snackbar.LENGTH_LONG
        ).show();
    }
}

