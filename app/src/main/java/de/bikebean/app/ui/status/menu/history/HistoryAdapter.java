package de.bikebean.app.ui.status.menu.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.bikebean.app.db.DatabaseEntity;

public abstract class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    public abstract class HistoryViewHolder extends RecyclerView.ViewHolder {
        public HistoryViewHolder(View v) {
            super(v);
        }
    }

    protected Context ctx;
    protected final LayoutInflater mInflater;
    protected List<? extends DatabaseEntity> mStates;

    public HistoryAdapter(Context context, List<? extends DatabaseEntity> states) {
        ctx = context;

        mInflater = LayoutInflater.from(context);
        mStates = states;
    }

    void setStates(List<? extends DatabaseEntity> states) {
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
