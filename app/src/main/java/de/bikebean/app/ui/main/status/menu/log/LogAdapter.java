package de.bikebean.app.ui.main.status.menu.log;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.bikebean.app.R;
import de.bikebean.app.ui.utils.Utils;
import de.bikebean.app.db.log.Log;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

    private final Context mContext;

    static class LogViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateTimeText, messageText;

        LogViewHolder(View itemView) {
            super(itemView);
            dateTimeText = itemView.findViewById(R.id.dateTimeText5);
            messageText = itemView.findViewById(R.id.messageText);
        }
    }

    private final LayoutInflater mInflater;
    private List<Log> mLog;  // cached copy of sms

    LogAdapter(Context context, List<Log> log) {
        mInflater = LayoutInflater.from(context);

        mContext = context;
        mLog = log;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item_log, parent, false);
        return new LogViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        if (mLog != null) {
            Log current = mLog.get(position);

            holder.messageText.setText(current.getMessage());
            holder.dateTimeText.setText(Utils.convertToDateHuman(current.getTimestamp()));

            switch (current.getLevel()) {
                case DEBUG:
                    setColor(holder, R.color.blue);
                case INFO:
                    setColor(holder, R.color.grey);
                case WARNING:
                    setColor(holder, R.color.primaryColor);
                case ERROR:
                    setColor(holder, R.color.secondaryVariant);
            }
        } else {
            holder.messageText.setVisibility(View.INVISIBLE);
            holder.dateTimeText.setVisibility(View.INVISIBLE);
        }
    }

    private void setColor(@NonNull LogViewHolder holder, int color) {
        holder.messageText.setTextColor(mContext.getResources().getColor(color));
    }

    public void setLog(List<Log> log) {
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
