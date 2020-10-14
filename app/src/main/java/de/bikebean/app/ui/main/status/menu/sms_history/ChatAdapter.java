package de.bikebean.app.ui.main.status.menu.sms_history;

import android.content.Context;
import android.provider.Telephony;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.bikebean.app.R;
import de.bikebean.app.db.sms.Sms;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout msgFrom, msgYou;
        private final TextView txtMsgYou, timeMsgYou, lblMsgFrom, txtMsgFrom, timeMsgFrom;

        ChatViewHolder(final @NonNull View itemView) {
            super(itemView);
            msgFrom = itemView.findViewById(R.id.msgFrom);
            msgYou = itemView.findViewById(R.id.msgYou);
            txtMsgYou = itemView.findViewById(R.id.txtMsgYou);
            timeMsgYou = itemView.findViewById(R.id.timeMsgYou);
            lblMsgFrom = itemView.findViewById(R.id.lblMsgFrom);
            txtMsgFrom = itemView.findViewById(R.id.txtMsgFrom);
            timeMsgFrom = itemView.findViewById(R.id.timeMsgFrom);
        }
    }

    private final @NonNull LayoutInflater mInflater;
    private @Nullable List<Sms> mSms;  /* cached copy of sms */

    ChatAdapter(final @NonNull Context context, final @Nullable List<Sms> sms) {
        mInflater = LayoutInflater.from(context);
        mSms = sms;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(final @NonNull ViewGroup parent, int viewType) {
        final @NonNull View itemView = mInflater.inflate(R.layout.recyclerview_item_chat, parent, false);
        return new ChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final @NonNull ChatViewHolder holder, int position) {
        if (mSms != null) {
            final @NonNull Sms current = mSms.get(position);

            if (current.getType() == Telephony.Sms.MESSAGE_TYPE_INBOX) {
                /* Make FROM elements visible (message from other part) */
                holder.msgFrom.setVisibility(View.VISIBLE);
                holder.msgYou.setVisibility(View.GONE);

                holder.txtMsgFrom.setText(current.getBody());
                holder.lblMsgFrom.setText(String.format("Bike Bean (%s)", current.getAddress()));
                holder.timeMsgFrom.setText(current.getDate());
            } else {
                /* Make OUR elements visible (message from us) */
                holder.msgYou.setVisibility(View.VISIBLE);
                holder.msgFrom.setVisibility(View.GONE);

                holder.txtMsgYou.setText(current.getBody());
                holder.timeMsgYou.setText(current.getDate());
            }
        } else {
            holder.msgFrom.setVisibility(View.INVISIBLE);
            holder.msgYou.setVisibility(View.INVISIBLE);
        }
    }

    public void setSms(final @Nullable List<Sms> sms) {
        mSms = sms;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mSms != null)
            return mSms.size();
        else return 0;
    }
}
