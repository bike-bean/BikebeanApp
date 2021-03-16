package de.bikebean.app.ui.drawer.history.position;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import java.util.List;
import java.util.Locale;

import de.bikebean.app.MainActivity;
import de.bikebean.app.R;
import de.bikebean.app.db.DatabaseEntity;
import de.bikebean.app.db.state.LocationState;
import de.bikebean.app.ui.drawer.history.HistoryAdapter;
import de.bikebean.app.ui.utils.date.DateUtils;

class PositionHistoryAdapter extends HistoryAdapter {

    static class PositionHistoryViewHolder extends HistoryViewHolder {
        private final TableLayout table;
        private final CardView buttonOpenMap;
        private final TextView lat, lng, acc, dateTimeText, smsIdText;

        PositionHistoryViewHolder(final @NonNull View v) {
            super(v);

            table = v.findViewById(R.id.tableLayout2);
            buttonOpenMap = v.findViewById(R.id.buttonOpenMap);
            lat = v.findViewById(R.id.lat2);
            lng = v.findViewById(R.id.lng2);
            acc = v.findViewById(R.id.acc2);
            dateTimeText = v.findViewById(R.id.dateTimeText6);
            smsIdText = v.findViewById(R.id.smsIdText);
        }
    }

    PositionHistoryAdapter(final @NonNull Context context,
                           final @Nullable List<? extends DatabaseEntity> states) {
        super(context, states);
    }

    @NonNull
    @Override
    public PositionHistoryViewHolder onCreateViewHolder(final @NonNull ViewGroup parent, int viewType) {
        final @NonNull View itemView = mInflater.inflate(
                R.layout.recyclerview_item_position_history,
                parent, false
        );
        return new PositionHistoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final @NonNull HistoryViewHolder holder, int position) {
        final @NonNull PositionHistoryViewHolder pHolder = (PositionHistoryViewHolder) holder;

        if (mStates != null) {
            final @NonNull LocationState current = (LocationState) mStates.get(position);

            pHolder.table.setVisibility(View.VISIBLE);
            pHolder.lat.setText(String.format(Locale.GERMANY, "%.7f", current.getLat()));
            pHolder.lng.setText(String.format(Locale.GERMANY, "%.7f", current.getLng()));
            pHolder.acc.setText(String.format(Locale.GERMANY, "%.1f", current.getAcc()));
            pHolder.dateTimeText.setText(DateUtils.convertPeriodToHuman(current.getTimestamp()));
            pHolder.smsIdText.setText(
                    String.format(Locale.GERMANY, "SmsId: %d", current.getSmsId())
            );

            pHolder.buttonOpenMap.setOnClickListener(v ->
                ((MainActivity) ctx).navigateTo(R.id.map_action, current.getArgs())
            );
        } else {
            pHolder.table.setVisibility(View.INVISIBLE);
            pHolder.buttonOpenMap.setVisibility(View.INVISIBLE);
            pHolder.dateTimeText.setVisibility(View.GONE);
            pHolder.smsIdText.setVisibility(View.GONE);
        }
    }
}
