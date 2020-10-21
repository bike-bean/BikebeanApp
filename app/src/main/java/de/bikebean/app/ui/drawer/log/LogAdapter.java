package de.bikebean.app.ui.drawer.log;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.bikebean.app.R;
import de.bikebean.app.db.log.Log;
import de.bikebean.app.ui.utils.date.DateUtils;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

    private final @NonNull Context mContext;

    static class LogViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateTimeText, messageText;

        LogViewHolder(View itemView) {
            super(itemView);
            dateTimeText = itemView.findViewById(R.id.dateTimeText5);
            messageText = itemView.findViewById(R.id.messageText);
        }
    }

    private final @NonNull LayoutInflater mInflater;
    private @Nullable List<Log> mLog;  /* cached copy of sms */

    LogAdapter(final @NonNull Context context, final @Nullable List<Log> log) {
        mInflater = LayoutInflater.from(context);

        mContext = context;
        mLog = log;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(final @NonNull ViewGroup parent, int viewType) {
        final @NonNull View itemView = mInflater.inflate(R.layout.recyclerview_item_log, parent, false);
        return new LogViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final @NonNull LogViewHolder holder, int position) {
        if (mLog != null) {
            final @NonNull Log current = mLog.get(position);

            holder.messageText.setText(current.getMessage());
            holder.dateTimeText.setText(DateUtils.convertPeriodToHuman(current.getTimestamp()));

            switch (current.getLevel()) {
                case DEBUG:
                    setColor(holder, R.color.grey);
                case INFO:
                    setColor(holder, R.color.blue);
                case WARNING:
                    setColor(holder, R.color.brandColorGreen);
                case ERROR:
                    setColor(holder, R.color.design_default_color_error);
            }
        } else {
            holder.messageText.setVisibility(View.INVISIBLE);
            holder.dateTimeText.setVisibility(View.INVISIBLE);
        }
    }

    private void setColor(final @NonNull LogViewHolder holder, int color) {
        holder.messageText.setTextColor(mContext.getResources().getColor(color));
    }

    public void setLog(final @Nullable List<Log> log) {
        mLog = log;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mLog != null)
            return mLog.size();
        else return 0;
    }
}
