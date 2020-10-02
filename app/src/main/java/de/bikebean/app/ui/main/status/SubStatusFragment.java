package de.bikebean.app.ui.main.status;

import android.os.Bundle;
import android.provider.Telephony;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import de.bikebean.app.R;
import de.bikebean.app.ui.main.status.menu.log.LogViewModel;
import de.bikebean.app.ui.utils.Utils;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.main.status.menu.sms_history.SmsViewModel;
import de.bikebean.app.ui.utils.sms.send.SmsSender;
import de.bikebean.app.ui.main.status.settings.LiveDataTimerViewModel;

public abstract class SubStatusFragment extends Fragment {

    private StateViewModel st;
    protected LogViewModel lv;
    protected LiveDataTimerViewModel tv;
    protected SmsViewModel sm;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        st = new ViewModelProvider(this).get(StateViewModel.class);
        lv = new ViewModelProvider(this).get(LogViewModel.class);
        tv = new ViewModelProvider(this).get(LiveDataTimerViewModel.class);
        sm = new ViewModelProvider(this).get(SmsViewModel.class);

        setupListeners(getViewLifecycleOwner());
        initUserInteractionElements();
    }

    private void onPostSend(boolean sent, final @NonNull SmsSender smsSender) {
        if (sent) {
            Snackbar.make(requireView(),
                    String.format("SMS an %s gesendet", smsSender.getAddress()),
                    Snackbar.LENGTH_LONG
            ).show();

            sm.insert(new Sms(sm.getLatestId(lv, Telephony.Sms.MESSAGE_TYPE_SENT), smsSender));
            st.insert(smsSender);
        } else {
            Snackbar.make(requireView(), "Vorgang abgebrochen.", Snackbar.LENGTH_LONG).show();
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

    protected void setElements(@NonNull List<State> states) {
        if (states.size() == 0)
            return;

        @NonNull State state = states.get(0);

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
                    case ACC: // And
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
                    case _STATUS:
                    case WAPP:
                    case NO_WIFI_ACCESS_POINTS:
                    case NO_CELL_TOWERS:
                        break;
                }
                break;
        }
    }

    // confirmed
    protected abstract void setBatteryElementsConfirmed(@NonNull State state);
    protected abstract void setIntervalElementsConfirmed(@NonNull State state);
    protected abstract void setWifiElementsConfirmed(@NonNull State state);
    protected abstract void setWarningNumberElementsConfirmed(@NonNull State state);
    protected abstract void setStatusElementsConfirmed(@NonNull State state);
    protected abstract void setLocationElementsConfirmed(@NonNull State state);
    protected abstract void setLatConfirmed(@NonNull State state);
    protected abstract void setLngConfirmed(@NonNull State state);
    protected abstract void setAccConfirmed(@NonNull State state);

    // pending
    protected abstract void setBatteryElementsPending(@NonNull State state);
    protected abstract void setIntervalElementsPending(@NonNull State state);
    protected abstract void setWifiElementsPending(@NonNull State state);
    protected abstract void setWarningNumberElementsPending(@NonNull State state);
    protected abstract void setLocationElementsPending(@NonNull State state);
    protected abstract void setLocationElementsTempPending(@NonNull State state);

    // unset
    protected abstract void setBatteryElementsUnset(@NonNull State state);
    protected abstract void setWarningNumberElementsUnset(@NonNull State state);
    protected abstract void setStatusElementsUnset(@NonNull State state);
    protected abstract void setLocationElementsUnset();
    protected abstract void setLocationElementsTempUnset();

    protected void updatePendingText(TextView textView, long stopTime, long residualSeconds) {
        if (residualSeconds < 0) {
            textView.setText(getString(R.string.overdue,
                    Utils.ConvertPeriodToHuman(stopTime))
            );
            return;
        }

        int hours = (int) residualSeconds / 60 / 60;
        int minutes = (int) (residualSeconds / 60 ) - (hours * 60);

        String hoursPadded = (hours < 10) ? "0" + hours : String.valueOf(hours);
        String minutesPadded = (minutes < 10) ? "0" + minutes : String.valueOf(minutes);

        textView.setText(getString(R.string.pending_text,
                hoursPadded + ":" + minutesPadded,
                Utils.convertToTime(stopTime))
        );
    }

    protected void sendSms(@NonNull Sms.MESSAGE message, @NonNull State[] updates) {
        new SmsSender(requireActivity(), lv, this::onPostSend, message, updates).send();
    }
}
