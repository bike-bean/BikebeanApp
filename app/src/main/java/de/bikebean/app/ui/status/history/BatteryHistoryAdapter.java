package de.bikebean.app.ui.status.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import de.bikebean.app.R;
import de.bikebean.app.Utils;
import de.bikebean.app.db.state.State;

public class BatteryHistoryAdapter
        extends RecyclerView.Adapter<BatteryHistoryAdapter.HistoryViewHolder> {

    class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView batteryValue, dateTimeText, smsIdText;

        HistoryViewHolder(View v) {
            super(v);

            batteryValue = v.findViewById(R.id.batteryValue);
            dateTimeText = v.findViewById(R.id.dateTimeText);
            smsIdText = v.findViewById(R.id.smsIdText);
        }
    }

    private Context ctx;
    private final LayoutInflater mInflater;
    private List<State> mStates;  // cached copy of sms

    BatteryHistoryAdapter(Context context, List<State> states) {
        ctx = context;

        mInflater = LayoutInflater.from(context);
        mStates = states;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(
                R.layout.recyclerview_item_battery_history,
                parent, false
        );
        return new HistoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        if (mStates != null) {
            State current = mStates.get(position);

            String batteryStatus = current.getValue() + " %";
            holder.batteryValue.setText(batteryStatus);
            holder.batteryValue.setCompoundDrawablesWithIntrinsicBounds(
                    Utils.getBatteryDrawable(ctx, current.getValue()),
                    null, null, null
            );
            holder.dateTimeText.setText(Utils.convertToDateHuman(current.getTimestamp()));
            holder.smsIdText.setText(
                    String.format(Locale.GERMANY, "SmsId: %d", current.getSmsId()));
        } else {
            holder.batteryValue.setVisibility(View.INVISIBLE);
            holder.dateTimeText.setVisibility(View.GONE);
            holder.smsIdText.setVisibility(View.GONE);
        }
    }

    void setStates(List<State> states) {
        mStates = states;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mStates != null)
            return mStates.size();
        else return 0;
    }
}
