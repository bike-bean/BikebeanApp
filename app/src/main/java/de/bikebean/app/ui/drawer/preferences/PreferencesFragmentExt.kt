package de.bikebean.app.ui.drawer.preferences

import android.content.Intent
import android.net.Uri
import android.view.View
import de.bikebean.app.ui.drawer.preferences.VersionJsonParser.AppRelease

fun PreferencesFragment.startObservingNewVersion() {
    preferencesViewModel.getNewVersion().observe(viewLifecycleOwner, ::setVersionNameNew)
}

fun PreferencesFragment.startVersionChecker() {
    VersionChecker(
            requireContext(),
            logViewModel, preferencesViewModel,
            ::newerVersionHandler
    ).execute()
}

private fun PreferencesFragment.newerVersionHandler(release: AppRelease) {
    NewVersionDialog(
            requireActivity(), release,
            ::downloadNewVersion
    ).show(childFragmentManager, "newVersionDialog")
}

private fun PreferencesFragment.setVersionNameNew(release: AppRelease) {
    if (release.url.isEmpty()) return

    val newVersionString = "Neueste Version: ${release.name}"

    listOf(
        versionNameNew,
        downloadNewVersionButton
    ).forEach(::setVisible)

    versionNameNew.text = newVersionString
    downloadNewVersionButton.setOnClickListener { downloadNewVersion(release.url) }
}

private fun PreferencesFragment.downloadNewVersion(url: String) {
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