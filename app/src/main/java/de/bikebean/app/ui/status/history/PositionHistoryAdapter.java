package de.bikebean.app.ui.status.history;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import de.bikebean.app.R;
import de.bikebean.app.Utils;
import de.bikebean.app.db.state.LocationState;

public class PositionHistoryAdapter
        extends RecyclerView.Adapter<PositionHistoryAdapter.HistoryViewHolder> {

    class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final TableLayout table;
        private final CardView buttonOpenMap;
        private final TextView lat, lng, acc, dateTimeText, smsIdText;

        HistoryViewHolder(View v) {
            super(v);

            table = v.findViewById(R.id.tableLayout2);
            buttonOpenMap = v.findViewById(R.id.buttonOpenMap);
            lat = v.findViewById(R.id.lat2);
            lng = v.findViewById(R.id.lng2);
            acc = v.findViewById(R.id.acc2);
            dateTimeText = v.findViewById(R.id.dateTimeText);
            smsIdText = v.findViewById(R.id.smsIdText);
        }
    }

    private final LayoutInflater mInflater;
    private List<LocationState> mStates;  // cached copy of sms

    PositionHistoryAdapter(Context context, List<LocationState> states) {
        mInflater = LayoutInflater.from(context);
        mStates = states;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(
                R.layout.recyclerview_item_position_history,
                parent, false
        );
        return new HistoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        if (mStates != null) {
            LocationState current = mStates.get(position);

            Bundle bundle = new Bundle();
            bundle.putDouble("lat", current.getLat());
            bundle.putDouble("lng", current.getLng());
            bundle.putDouble("acc", current.getAcc());
            bundle.putInt("noCellTowers", current.getNoCellTowers());
            bundle.putInt("noWifiAccessPoints", current.getNoWifiAccessPoints());

            holder.table.setVisibility(View.VISIBLE);
            holder.lat.setText(String.format(Locale.GERMANY, "%.7f", current.getLat()));
            holder.lng.setText(String.format(Locale.GERMANY, "%.7f", current.getLng()));
            holder.acc.setText(String.format(Locale.GERMANY, "%.1f", current.getAcc()));
            holder.dateTimeText.setText(Utils.convertToDateHuman(current.getTimestamp()));
            holder.smsIdText.setText(
                    String.format(Locale.GERMANY, "SmsId: %d", current.getSmsId())
            );

            holder.buttonOpenMap.setOnClickListener(v ->
                    Navigation.findNavController(v).navigate(R.id.map_action, bundle));
        } else {
            holder.table.setVisibility(View.INVISIBLE);
            holder.buttonOpenMap.setVisibility(View.INVISIBLE);
            holder.dateTimeText.setVisibility(View.GONE);
            holder.smsIdText.setVisibility(View.GONE);
        }
    }

    void setStates(List<LocationState> states) {
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
