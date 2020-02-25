package de.bikebean.app.ui.status;

import android.content.Context;
import android.os.Bundle;
import android.provider.Telephony;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.bikebean.app.R;
import de.bikebean.app.Utils;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.status.menu.sms_history.SmsViewModel;
import de.bikebean.app.ui.status.sms_utils.send.SmsSendIdGetter;
import de.bikebean.app.ui.status.sms_utils.send.SmsSender;
import de.bikebean.app.ui.status.status.LiveDataTimerViewModel;

public abstract class SubStatusFragment extends Fragment {

    private StateViewModel st;
    protected LiveDataTimerViewModel tv;
    protected SmsViewModel sm;

    protected FragmentActivity act;
    protected Context ctx;

    private SmsSender smsSender;
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        st = new ViewModelProvider(this).get(StateViewModel.class);
        tv = new ViewModelProvider(this).get(LiveDataTimerViewModel.class);
        sm = new ViewModelProvider(this).get(SmsViewModel.class);

        act = Objects.requireNonNull(getActivity());
        ctx = Objects.requireNonNull(act).getApplicationContext();

        LifecycleOwner l = getViewLifecycleOwner();

        smsSender = new SmsSender(act, this::onPostSend);

        setupListeners(l);
        initUserInteractionElements();
    }

    private void onPostSend(boolean sent, String address, String message, State[] updates) {
        if (sent) {
            Toast.makeText(ctx,
                    String.format("SMS an %s gesendet", address),
                    Toast.LENGTH_LONG
            ).show();

            long timestamp = System.currentTimeMillis();

            synchronized (this) {
                new SmsSendIdGetter(sm, smsId ->
                        sm.insert(new Sms(smsId - 1, address, message,
                                Telephony.Sms.MESSAGE_TYPE_SENT, Sms.STATUS.NEW,
                                Utils.convertToTime(timestamp), timestamp))
                ).execute();
            }
            st.insert(updates);
        } else {
            Toast.makeText(ctx, "Vorgang abgebrochen.", Toast.LENGTH_LONG).show();
            resetElements();
        }
    }

    protected abstract void setupListeners(LifecycleOwner l);

    protected abstract void initUserInteractionElements();

    protected abstract void resetElements();

    /*
    * Change the Text Views, Switches etc. (UI elements)
    * according to the states from the viewModel.
    * */
    // Cached copy of parsed sms
    private final List<Integer> parsedSms = new ArrayList<>();

    protected void setElements(List<State> states) {
        if (states.size() == 0)
            return;

        State state = states.get(0);

        int id = state.id;

        if (parsedSms.contains(id))
            return;

        parsedSms.add(id);

        State.KEY key = State.KEY.getValue(state.getKey());
        switch (State.STATUS.values()[state.getState()]) {
            case UNSET:
                switch (key) {
                    case BATTERY:
                        setBatteryElementsUnset(state);
                        break;
                    case INTERVAL:
                        setIntervalElementsConfirmed(state);
                        break;
                    case WIFI:
                        setWifiElementsConfirmed(state);
                        break;
                    case WARNING_NUMBER:
                        setWarningNumberElementsUnset(state);
                        break;
                    case _STATUS:
                        setStatusElementsUnset(state);
                        break;
                    case LAT: // And
                    case LNG: // And
                    case ACC:
                        break;
                    case LOCATION:
                        setLocationElementsUnset();
                        break;
                    case CELL_TOWERS: // And
                    case WIFI_ACCESS_POINTS:
                        setLocationElementsTempUnset();
                        break;
                    case WAPP:
                    case NO_WIFI_ACCESS_POINTS:
                    case NO_CELL_TOWERS:
                        break;
                }
                break;
            case CONFIRMED:
                switch (key) {
                    case BATTERY:
                        setBatteryElementsConfirmed(state);
                        break;
                    case INTERVAL:
                        setIntervalElementsConfirmed(state);
                        break;
                    case WIFI:
                        setWifiElementsConfirmed(state);
                        break;
                    case WARNING_NUMBER:
                        setWarningNumberElementsConfirmed(state);
                        break;
                    case _STATUS:
                        setStatusElementsConfirmed(state);
                        break;
                    case LAT:
                        setLatConfirmed(state);
                        break;
                    case LNG:
                        setLngConfirmed(state);
                        break;
                    case ACC:
                        setAccConfirmed(state);
                        break;
                    case LOCATION:
                        setLocationElementsConfirmed(state);
                    case CELL_TOWERS: // And
                    case WIFI_ACCESS_POINTS: // And
                    case WAPP: // And
                    case NO_WIFI_ACCESS_POINTS: // And
                    case NO_CELL_TOWERS:
                        break;
                }
                break;
            case PENDING:
                switch (key) {
                    case BATTERY:
                        setBatteryElementsPending(state);
                        break;
                    case INTERVAL:
                        setIntervalElementsPending(state);
                        break;
                    case WIFI:
                        setWifiElementsPending(state);
                        break;
                    case WARNING_NUMBER:
                        setWarningNumberElementsPending(state);
                        break;
                    case _STATUS:
                        break;
                    case LAT: // And
                    case LNG: // And
                    case ACC: // And
                        break;
                    case LOCATION:
                        setLocationElementsPending(state);
                        break;
                    case CELL_TOWERS: // And
                    case WIFI_ACCESS_POINTS:
                        setLocationElementsTempPending(state);
                        break;
                }
                break;
        }
    }

    // confirmed
    protected abstract void setBatteryElementsConfirmed(State state);
    protected abstract void setIntervalElementsConfirmed(State state);
    protected abstract void setWifiElementsConfirmed(State state);
    protected abstract void setWarningNumberElementsConfirmed(State state);
    protected abstract void setStatusElementsConfirmed(State state);
    protected abstract void setLocationElementsConfirmed(State state);
    protected abstract void setLatConfirmed(State state);
    protected abstract void setLngConfirmed(State state);
    protected abstract void setAccConfirmed(State state);

    // pending
    protected abstract void setBatteryElementsPending(State state);
    protected abstract void setIntervalElementsPending(State state);
    protected abstract void setWifiElementsPending(State state);
    protected abstract void setWarningNumberElementsPending(State state);
    protected abstract void setLocationElementsPending(State state);
    protected abstract void setLocationElementsTempPending(State state);

    // unset
    protected abstract void setBatteryElementsUnset(State state);
    protected abstract void setWarningNumberElementsUnset(State state);
    protected abstract void setStatusElementsUnset(State state);
    protected abstract void setLocationElementsUnset();
    protected abstract void setLocationElementsTempUnset();

    protected void updatePendingText(TextView textView, long stopTime, long residualSeconds) {
        if (residualSeconds < 0) {
            textView.setText(getResources().getString(R.string.overdue,
                    Utils.convertToDateHuman(stopTime))
            );
            return;
        }

        int hours = (int) residualSeconds / 60 / 60;
        int minutes = (int) (residualSeconds / 60 ) - (hours * 60);

        String hoursPadded = (hours < 10) ? "0" + hours : String.valueOf(hours);
        String minutesPadded = (minutes < 10) ? "0" + minutes : String.valueOf(minutes);

        textView.setText(getResources().getString(R.string.pending_text,
                hoursPadded + ":" + minutesPadded,
                Utils.convertToTime(stopTime))
        );
    }

    protected void sendSms(Sms.MESSAGE message, State[] updates) {
        smsSender.send(message, updates);
    }
}