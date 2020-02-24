package de.bikebean.app.ui.status.menu.history.battery;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Locale;

import de.bikebean.app.R;
import de.bikebean.app.Utils;
import de.bikebean.app.db.DatabaseEntity;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.status.menu.history.HistoryAdapter;

class BatteryHistoryAdapter extends HistoryAdapter {

    class BatteryHistoryViewHolder extends HistoryViewHolder {
        private final TextView batteryValue, dateTimeText, smsIdText;

        BatteryHistoryViewHolder(View v) {
            super(v);

            batteryValue = v.findViewById(R.id.batteryValue);
            dateTimeText = v.findViewById(R.id.dateTimeText);
            smsIdText = v.findViewById(R.id.smsIdText);
        }
    }

    BatteryHistoryAdapter(Context context, List<? extends DatabaseEntity> states) {
        super(context, states);
    }

    @NonNull
    @Override
    public BatteryHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(
                R.layout.recyclerview_item_battery_history,
                parent, false
        );
        return new BatteryHistoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        BatteryHistoryViewHolder bHolder = (BatteryHistoryViewHolder) holder;

        if (mStates != null) {
            State current = (State) mStates.get(position);

            String batteryStatus = current.getValue() + " %";
            bHolder.batteryValue.setText(batteryStatus);
            bHolder.batteryValue.setCompoundDrawablesWithIntrinsicBounds(
                    Utils.getBatteryDrawable(ctx, current.getValue()),
                    null, null, null
            );
            bHolder.dateTimeText.setText(Utils.convertToDateHuman(current.getTimestamp()));
            bHolder.smsIdText.setText(
                    String.format(Locale.GERMANY, "SmsId: %d", current.getSmsId()));
        } else {
            bHolder.batteryValue.setVisibility(View.INVISIBLE);
            bHolder.dateTimeText.setVisibility(View.GONE);
            bHolder.smsIdText.setVisibility(View.GONE);
        }
    }
}
