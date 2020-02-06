package de.bikebean.app.ui.status.location;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.bikebean.app.R;
import de.bikebean.app.Utils;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.status.StateViewModel;
import de.bikebean.app.ui.status.sms.SmsViewModel;
import de.bikebean.app.ui.status.sms.send.SmsSender;

public class LocationStatusFragment extends Fragment {

    private static final int LOCATION_DEFAULT = 0;
    private static final int LOCATION_PENDING = 1;

    private Button buttonGetLocation, buttonOpenMap;
    private TextView latText, lngText, accText, locationLastChangedText;

    private ProgressBar progressBar;

    private SmsSender smsSender;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_status_location, container, false);

        latText = v.findViewById(R.id.lat);
        lngText = v.findViewById(R.id.lng);
        accText = v.findViewById(R.id.acc);
        locationLastChangedText = v.findViewById(R.id.datetimeText1);

        progressBar = v.findViewById(R.id.progressBar);

        buttonGetLocation = v.findViewById(R.id.button_get_location);
        buttonOpenMap = v.findViewById(R.id.button_open_map);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        StateViewModel st = new ViewModelProvider(this).get(StateViewModel.class);
        SmsViewModel sm = new ViewModelProvider(this).get(SmsViewModel.class);
        LifecycleOwner l = getViewLifecycleOwner();
        FragmentActivity act = getActivity();
        Context ctx = Objects.requireNonNull(act).getApplicationContext();

        smsSender = new SmsSender(ctx, act, sm, st);

        // Observe any changes to
        // lat, lng or acc in the database
        // (i.e. after the location updater has written
        // its stuff in there)
        st.getStatusLocationLat().observe(l,
                s -> updateLocTextViewsFromStatuses(s, latText, 7)
        );
        st.getStatusLocationLng().observe(l,
                s -> updateLocTextViewsFromStatuses(s, lngText, 7)
        );
        st.getStatusLocationAcc().observe(l,
                s -> updateLocTextViewsFromStatuses(s, accText, 1)
        );

        // Pending
        st.getPendingCellTowers().observe(l,
                s -> updateLocation(st, sm, ctx, s, "cellTowers")
        );
        st.getPendingWifiAccessPoints().observe(l,
                s -> updateLocation(st, sm, ctx, s, "wifiAccessPoints")
        );

        // Buttons
        buttonGetLocation.setOnClickListener(v -> smsSender.send("Wapp", null));
        buttonOpenMap.setOnClickListener(this::navigateToNext);
    }

    private void setLocationArea(int state) {
        if (state == LOCATION_PENDING) {
            latText.setText("");
            lngText.setText("");
            accText.setText("");
            progressBar.setVisibility(ProgressBar.VISIBLE);
        } else if (state == LOCATION_DEFAULT)
            progressBar.setVisibility(ProgressBar.GONE);
    }

    private void updateLocTextViewsFromStatuses(List<State> states, TextView tv, int precision) {
        if (states.size() == 0)
            return;

        updateTextViewValueFromStatus(tv, states.get(0), precision);
        updateTextViewTimeFromStatus(locationLastChangedText, states.get(0));
    }

    private void updateTextViewTimeFromStatus(TextView tv, State s) {
        tv.setText(Utils.convertToDateHuman(s.getTimestamp()));
    }

    private void updateTextViewValueFromStatus(TextView tv, State s, int precision) {
        final String formatStr = "%." + precision + "f";
        tv.setText(String.format(Locale.GERMANY, formatStr, s.getValue()));
    }

    // Cached copy of parsed sms
    private final List<Integer> parsedSms = new ArrayList<>();

    private void updateLocation(StateViewModel st, SmsViewModel smsViewModel,
                                Context ctx, List<State> states, String key) {
        if (states.size() == 0) return;

        int smsId = states.get(0).getSmsId();

        if (parsedSms.contains(smsId)) return;

        String longValue = states.get(0).getLongValue();
        parsedSms.add(smsId);

        setLocationArea(LOCATION_PENDING);

        new LocationUpdater(ctx, st, smsViewModel, smsId, isLocationUpdated -> {
            if (isLocationUpdated) {
                smsViewModel.markParsed(smsId);
                setLocationArea(LOCATION_DEFAULT);
            }
        }).execute(key, longValue);
    }

    private void navigateToNext(View v) {
        Navigation.findNavController(v).navigate(R.id.map_action);
    }
}
