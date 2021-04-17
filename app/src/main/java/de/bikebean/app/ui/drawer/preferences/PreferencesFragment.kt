package de.bikebean.app.ui.drawer.preferences

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import de.bikebean.app.MainActivity
import de.bikebean.app.MainActivity.LimitedBackScope
import de.bikebean.app.R
import de.bikebean.app.ui.drawer.log.LogViewModel
import de.bikebean.app.ui.utils.device.DeviceUtils

class PreferencesFragment : Fragment(), LimitedBackScope {

    private var versionNameNew: TextView? = null
    private var downloadNewVersionButton: Button? = null

    private lateinit var logViewModel: LogViewModel
    private lateinit var preferencesViewModel: PreferencesViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_preferences, container, false).apply {
            findViewById<TextView>(R.id.versionName)?.apply {
                text = String.format("Aktuelle Version: ${DeviceUtils.versionName}")
            } ?: logViewModel.e("Failed to load TextView versionName!")

            versionNameNew = findViewById(R.id.versionName2)
            downloadNewVersionButton = findViewById(R.id.downloadNewVersion)
        }.also {
            childFragmentManager
                    .beginTransaction()
                    .replace(R.id.settings, SettingsFragment())
                    .commit()
            logViewModel = ViewModelProvider(this).get(LogViewModel::class.java)
            preferencesViewModel = ViewModelProvider(this).get(PreferencesViewModel::class.java)
            startObservingNewVersion()
            startVersionChecker()
        }
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

    private fun startObservingNewVersion() {
        preferencesViewModel.getNewVersion().observe(viewLifecycleOwner, ::setVersionNameNew)
    }

    private fun startVersionChecker() {
        VersionChecker(
                requireContext(),
                logViewModel, preferencesViewModel,
                ::newerVersionHandler
        ).execute()
    }

    private fun newerVersionHandler(release: VersionJsonParser.AppRelease) {
        NewVersionDialog(
                requireActivity(), release,
                ::downloadNewVersion
        ).show(childFragmentManager, "newVersionDialog")
    }

    private fun setVersionNameNew(release: VersionJsonParser.AppRelease) {
        if (release.url.isEmpty()) return

        val newVersionString = "Neueste Version: ${release.name}"

        listOfNotNull(
                versionNameNew,
                downloadNewVersionButton
        ).forEach(::setVisible)

        versionNameNew?.text = newVersionString
        downloadNewVersionButton?.setOnClickListener { downloadNewVersion(release.url) }
    }

    private fun downloadNewVersion(url: String) {
        Intent(Intent.ACTION_VIEW, Uri.parse(url)).run {
            when {
                resolveActivity(requireActivity().packageManager) != null -> this
                else -> null
            }
        }?.let(::startActivity)
    }

    private fun setVisible(v: View) {
        v.visibility = View.VISIBLE
    }
}