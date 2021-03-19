package de.bikebean.app.ui.drawer.history.battery;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Locale;

import de.bikebean.app.R;
import de.bikebean.app.db.DatabaseEntity;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.drawer.history.HistoryAdapter;
import de.bikebean.app.ui.utils.resource.ResourceUtils;
import de.bikebean.app.ui.utils.date.DateUtils;

class BatteryHistoryAdapter extends HistoryAdapter {

    static class BatteryHistoryViewHolder extends HistoryViewHolder {
        private final TextView batteryValue, dateTimeText, smsIdText;

        BatteryHistoryViewHolder(final @NonNull View v) {
            super(v);

            batteryValue = v.findViewById(R.id.batteryValue);
            dateTimeText = v.findViewById(R.id.dateTimeText);
            smsIdText = v.findViewById(R.id.smsIdText);
        }
    }

    BatteryHistoryAdapter(final @NonNull Context context,
                          final @Nullable List<? extends DatabaseEntity> states) {
        super(context, states);
    }

    @NonNull
    @Override
    public BatteryHistoryViewHolder onCreateViewHolder(final @NonNull ViewGroup parent, int viewType) {
        final @NonNull View itemView = mInflater.inflate(
                R.layout.recyclerview_item_battery_history,
                parent, false
        );
        return new BatteryHistoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final @NonNull HistoryViewHolder holder, int position) {
        final @NonNull BatteryHistoryViewHolder bHolder = (BatteryHistoryViewHolder) holder;

        if (mStates != null) {
            final @NonNull State current = (State) mStates.get(position);

            final @NonNull String batteryStatus = current.getValue().intValue() + " %";
            bHolder.batteryValue.setText(batteryStatus);
            bHolder.batteryValue.setCompoundDrawablesWithIntrinsicBounds(
                    ResourceUtils.getBatteryDrawable(ctx, current.getValue()),
                    null, null, null
            );
            bHolder.dateTimeText.setText(DateUtils.convertPeriodToHuman(current.getTimestamp()));
            bHolder.smsIdText.setText(
                    String.format(Locale.GERMANY, "SmsId: %d", current.getSmsId()));
        } else {
            bHolder.batteryValue.setVisibility(View.INVISIBLE);
            bHolder.dateTimeText.setVisibility(View.GONE);
            bHolder.smsIdText.setVisibility(View.GONE);
        }
    }
}
