package de.bikebean.app.ui.drawer.status;

import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.util.TypedValue;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import de.bikebean.app.MainActivity;
import de.bikebean.app.R;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.drawer.log.LogViewModel;
import de.bikebean.app.ui.drawer.map.MapFragmentViewModel;
import de.bikebean.app.ui.drawer.sms_history.SmsViewModel;
import de.bikebean.app.ui.drawer.status.battery.BatteryElementsSetter;
import de.bikebean.app.ui.drawer.status.location.LocationElementsSetter;
import de.bikebean.app.ui.drawer.status.settings.SettingsElementsSetter;
import de.bikebean.app.ui.utils.sms.send.SmsSender;

public abstract class SubStatusFragmentSmall extends Fragment {

    private StateViewModel st;

    protected LogViewModel lv;
    protected SmsViewModel sm;
    protected MapFragmentViewModel mf;

    @Override
    public void onActivityCreated(final @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        st = new ViewModelProvider(this).get(StateViewModel.class);
        lv = new ViewModelProvider(this).get(LogViewModel.class);
        sm = new ViewModelProvider(this).get(SmsViewModel.class);
        mf = new ViewModelProvider(this).get(MapFragmentViewModel.class);

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

    private boolean isAlreadyParsed(final @NonNull State state) {
        if (parsedSms.contains(state.id))
            return true;

        parsedSms.add(state.id);
        return false;
    }

    protected void setElements(final @NonNull List<State> states) {
        if (states.size() == 0 || isAlreadyParsed(states.get(0)))
            return;

        setElements(states.get(0));
    }

    private void setElements(final @NonNull State state) {
        final @NonNull State.KEY key = State.KEY.getValue(state);
        final @NonNull State.STATUS status = State.STATUS.getValue(state);
        switch (status) {
            case UNSET:
                switch (key) {
                    case BATTERY:
                        getBSetter().setButtonEnabled();
                        getBSetter().setBatteryElementsUnset(state);
                        break;
                    case INTERVAL:
                        try {
                            getSSetter().setIntervalElementsConfirmed(state);
                        } catch (ClassCastException e) {
                            getBSetter().setIntervalElementsConfirmed();
                        }
                        break;
                    case WIFI:
                        try {
                            getSSetter().setWifiElementsConfirmed(state);
                        } catch (ClassCastException e) {
                            getBSetter().setWifiElementsConfirmed(state);
                        }
                        break;
                    case WARNING_NUMBER:
                        getSSetter().setWarningNumberElementsUnset(state);
                        break;
                    case _STATUS:
                        getSSetter().setStatusElementsUnset();
                        break;
                    case LOCATION:
                        getLSetter().setLocationElementsUnset();
                        break;
                    case CELL_TOWERS: // And
                    case WIFI_ACCESS_POINTS:
                        try {
                            getLSetter().setButtonEnabled();
                            getLSetter().setLocationElementsProgressTimeUnset();
                        } catch (ClassCastException e) {
                            getBSetter().setButtonForceEnabled();
                        }
                        break;
                    case LAT: // And
                    case LNG: // And
                    case ACC: // And
                    case WAPP: // And
                    case NO_WIFI_ACCESS_POINTS: // And
                    case NO_CELL_TOWERS:
                        break;
                }
                break;
            case CONFIRMED:
                switch (key) {
                    case BATTERY:
                        getBSetter().setButtonEnabled();
                        getBSetter().setBatteryElementsConfirmed(state);
                        break;
                    case INTERVAL:
                        try {
                            getSSetter().setIntervalElementsConfirmed(state);
                        } catch (ClassCastException e) {
                            getBSetter().setIntervalElementsConfirmed();
                        }
                        break;
                    case WIFI:
                        try {
                            getSSetter().setWifiElementsConfirmed(state);
                        } catch (ClassCastException e) {
                            getBSetter().setWifiElementsConfirmed(state);
                        }
                        break;
                    case WARNING_NUMBER:
                        getSSetter().setWarningNumberElementsConfirmed(state);
                        break;
                    case _STATUS:
                        getSSetter().setStatusElementsConfirmed(state);
                        break;
                    case LAT:
                        getLSetter().setLatConfirmed(state);
                        break;
                    case LNG:
                        getLSetter().setLngConfirmed(state);
                        break;
                    case ACC:
                        getLSetter().setAccConfirmed(state);
                        break;
                    case LOCATION:
                        getLSetter().setLocationElementsConfirmed(state);
                        break;
                    case CELL_TOWERS: // And
                    case WIFI_ACCESS_POINTS:
                        try {
                            getLSetter().setButtonEnabled();
                            getLSetter().setLocationElementsProgressTimeConfirmed();
                        } catch (ClassCastException e) {
                            getBSetter().setButtonForceEnabled();
                        }
                        break;
                    case NO_WIFI_ACCESS_POINTS: // And
                    case NO_CELL_TOWERS:
                        getLSetter().setLocationElementsNumbersConfirmed(state);
                        break;
                    case WAPP:
                        break;
                }
                break;
            case PENDING:
                switch (key) {
                    case BATTERY:
                        getBSetter().setButtonDisabled();
                        getBSetter().setBatteryElementsPending(state);
                        break;
                    case INTERVAL:
                        try {
                            getSSetter().setIntervalElementsPending(state);
                        } catch (ClassCastException e) {
                            assert true;
                        }
                        break;
                    case WIFI:
                        try {
                            getSSetter().setWifiElementsPending(state);
                        } catch (ClassCastException e) {
                            assert true;
                        }
                        break;
                    case WARNING_NUMBER:
                        getSSetter().setWarningNumberElementsPending(state);
                        break;
                    case LOCATION:
                        getLSetter().setLocationElementsPending(state);
                        break;
                    case CELL_TOWERS: // And
                    case WIFI_ACCESS_POINTS:
                        try {
                            getLSetter().setButtonDisabled();
                            getLSetter().setLocationElementsProgressTimePending(state);
                        } catch (ClassCastException e) {
                            getBSetter().setButtonForceDisabled();
                        }
                        break;
                    case WAPP:
                        getLSetter().setLocationElementsProgressTextPending();
                        break;
                    case _STATUS: // And
                    case LAT: // And
                    case LNG: // And
                    case ACC: // And
                    case NO_WIFI_ACCESS_POINTS: // And
                    case NO_CELL_TOWERS: // And
                        break;
                }
                break;
        }
    }

    private BatteryElementsSetter getBSetter() {
        return (BatteryElementsSetter) this;
    }

    private LocationElementsSetter getLSetter() {
        return (LocationElementsSetter) this;
    }

    private SettingsElementsSetter getSSetter() {
        return (SettingsElementsSetter) this;
    }

    protected ColorFilter getCurrentIconColorFilter() {
        final @NonNull TypedValue typedValue = new TypedValue();
        final @ColorInt int color;

        requireContext().getTheme().resolveAttribute(
                R.attr.colorPrimary, typedValue, true
        );

        if (((MainActivity) requireActivity()).isLightTheme())
            color = ContextCompat.getColor(requireContext(), typedValue.resourceId);
        else color = ContextCompat.getColor(requireContext(), R.color.white);

        return new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    protected void sendSms(final @NonNull Sms.MESSAGE message, final @NonNull State[] updates) {
        new SmsSender(
                (AppCompatActivity) requireActivity(), lv,
                this::onPostSend, message, updates
        ).send();
    }

    public MapFragmentViewModel getMf() { return mf; }
}
