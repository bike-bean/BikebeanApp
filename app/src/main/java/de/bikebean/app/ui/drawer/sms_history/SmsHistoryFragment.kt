package de.bikebean.app.ui.drawer.sms_history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.bikebean.app.MainActivity
import de.bikebean.app.MainActivity.LimitedBackScope
import de.bikebean.app.R
import de.bikebean.app.db.sms.Sms

class SmsHistoryFragment : Fragment(), LimitedBackScope {

    private lateinit var smsViewModel: SmsViewModel
    private var chatAdapter: ChatAdapter? = null
    private var noDataText: TextView? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater
            .inflate(R.layout.fragment_history_sms, container, false)
            .apply {
                noDataText = findViewById(R.id.noDataText2)
                smsViewModel = ViewModelProvider(this@SmsHistoryFragment).get(
                        SmsViewModel::class.java
                )
                initRecyclerView(this)
            }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        smsViewModel.chat.observe(viewLifecycleOwner, ::updateAdapterSms)
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).apply {
            setToolbarScrollEnabled(false)
            resumeToolbarAndBottomSheet()
        }
    }

    override fun onBackPressed(): Boolean {
        (requireActivity() as MainActivity).navigateTo(R.id.map_back_action, null)
        return true
    }

    private fun initRecyclerView(v: View) {
        v.findViewById<RecyclerView>(R.id.recyclerView)?.apply {
            chatAdapter = ChatAdapter(requireContext(), smsViewModel.chat.value)
            adapter = chatAdapter

            LinearLayoutManager(requireContext()).apply {
                reverseLayout = true
                layoutManager = this
            }
        }
    }

    private fun updateAdapterSms(sms: List<Sms>) {
        if (sms.isNotEmpty()) {
            chatAdapter!!.setSms(sms)
            noDataText!!.visibility = View.GONE
        } else noDataText!!.visibility = View.VISIBLE
    }
}