package de.bikebean.app.ui.main.status;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    public void onActivityCreated(final @Nullable Bundle savedInstanceState) {
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

            sm.insert(smsSender, lv);
            st.insert(smsSender);
        } else {
            Snackbar.make(requireView(), "Vorgang abgebrochen.", Snackbar.LENGTH_LONG).show();
            resetElements();
        }
    }

    protected abstract void setupListeners(final @NonNull LifecycleOwner l);

    protected abstract void initUserInteractionElements();

    protected abstract void resetElements();

    /*
    * Change the Text Views, Switches etc. (UI elements)
    * according to the states from the viewModel.
    * */
    // Cached copy of parsed sms
    private final @NonNull List<Integer> parsedSms = new ArrayList<>();

    protected void setElements(final @NonNull List<State> states) {
        if (states.size() == 0)
            return;

        final @NonNull State state = states.get(0);

        int id = state.id;

        if (parsedSms.contains(id))
            return;

        parsedSms.add(id);

        final @NonNull State.KEY key = State.KEY.getValue(state);
        final @NonNull State.STATUS status = State.STATUS.getValue(state);
        switch (status) {
            case UNSET:
                switch (key) {
                    case BATTERY:
                        setBatteryElementsUnset(state);
                        break;
                    case INTERVAL:
                        setIntervalElementsConfirmed();
                        break;
                    case WIFI:
                        setWifiElementsConfirmed(state);
                        break;
                    case WARNING_NUMBER:
                        setWarningNumberElementsUnset(state);
                        break;
                    case _STATUS:
                        setStatusElementsUnset();
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
                        setIntervalElementsConfirmed();
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
    protected abstract void setBatteryElementsConfirmed(final @NonNull State state);
    protected abstract void setIntervalElementsConfirmed();
    protected abstract void setWifiElementsConfirmed(final @NonNull State state);
    protected abstract void setWarningNumberElementsConfirmed(final @NonNull State state);
    protected abstract void setStatusElementsConfirmed(final @NonNull State state);
    protected abstract void setLocationElementsConfirmed(final @NonNull State state);
    protected abstract void setLatConfirmed(final @NonNull State state);
    protected abstract void setLngConfirmed(final @NonNull State state);
    protected abstract void setAccConfirmed(final @NonNull State state);

    // pending
    protected abstract void setBatteryElementsPending(final @NonNull State state);
    protected abstract void setIntervalElementsPending(final @NonNull State state);
    protected abstract void setWifiElementsPending(final @NonNull State state);
    protected abstract void setWarningNumberElementsPending(final @NonNull State state);
    protected abstract void setLocationElementsPending(final @NonNull State state);
    protected abstract void setLocationElementsTempPending(final @NonNull State state);

    // unset
    protected abstract void setBatteryElementsUnset(final @NonNull State state);
    protected abstract void setWarningNumberElementsUnset(final @NonNull State state);
    protected abstract void setStatusElementsUnset();
    protected abstract void setLocationElementsUnset();
    protected abstract void setLocationElementsTempUnset();

    protected void updatePendingText(final @NonNull TextView textView, long stopTime, long residualSeconds) {
        if (residualSeconds < 0) {
            textView.setText(getString(R.string.overdue,
                    Utils.ConvertPeriodToHuman(stopTime))
            );
            return;
        }

        int hours = (int) residualSeconds / 60 / 60;
        int minutes = (int) (residualSeconds / 60 ) - (hours * 60);

        final @NonNull String hoursPadded = (hours < 10) ? "0" + hours : String.valueOf(hours);
        final @NonNull String minutesPadded = (minutes < 10) ? "0" + minutes : String.valueOf(minutes);

        textView.setText(getString(R.string.pending_text,
                hoursPadded + ":" + minutesPadded,
                Utils.convertToTime(stopTime))
        );
    }

    protected void sendSms(final @NonNull Sms.MESSAGE message, final @NonNull State[] updates) {
        new SmsSender(requireActivity(), lv, this::onPostSend, message, updates).send();
    }
}
