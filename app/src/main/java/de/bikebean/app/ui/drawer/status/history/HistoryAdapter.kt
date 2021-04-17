package de.bikebean.app.ui.drawer.status.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import de.bikebean.app.db.DatabaseEntity
import de.bikebean.app.ui.drawer.status.history.HistoryAdapter.HistoryViewHolder

abstract class HistoryAdapter(
        protected val ctx: Context,
        states: List<DatabaseEntity?>?
) : RecyclerView.Adapter<HistoryViewHolder?>() {

    abstract class HistoryViewHolder(v: View) : RecyclerView.ViewHolder(v)

    protected val mInflater: LayoutInflater = LayoutInflater.from(ctx)
    protected var mStates: List<DatabaseEntity?>? = states

    fun setStates(states: List<DatabaseEntity?>?) {
        mStates = states
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = mStates?.size ?: 0

}