package de.bikebean.app.ui.drawer.status.history

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.bikebean.app.MainActivity
import de.bikebean.app.R
import de.bikebean.app.db.DatabaseEntity

abstract class HistoryFragment : Fragment(), MainActivity.LimitedBackScope {

    protected var st: HistoryViewModel? = null
    private var historyAdapter: HistoryAdapter? = null

    // UI Elements
    protected var recyclerView: RecyclerView? = null
    protected var noDataText: TextView? = null

    protected abstract val newStateViewModel: HistoryViewModel

    protected abstract fun setupListeners()

    protected abstract fun getNewAdapter(ctx: Context): HistoryAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        st = newStateViewModel
        setupListeners()
        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).apply {
            setToolbarScrollEnabled(true)
            resumeToolbarAndBottomSheet()
        }
    }

    override fun onBackPressed(): Boolean {
        (requireActivity() as MainActivity).navigateTo(R.id.map_back_action, null)
        return true
    }

    private fun initRecyclerView() {
        requireContext().let { ctx ->
            historyAdapter = getNewAdapter(ctx)
            recyclerView!!.apply {
                adapter = historyAdapter
                layoutManager = LinearLayoutManager(ctx)
            }
        }
    }

    protected fun setStatesToAdapter(ls: List<DatabaseEntity?>) {
        if (ls.isNotEmpty()) {
            historyAdapter!!.setStates(ls)
            noDataText!!.visibility = View.GONE
        } else noDataText!!.visibility = View.VISIBLE
    }
}