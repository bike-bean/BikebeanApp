package de.bikebean.app.ui.drawer.preferences

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import de.bikebean.app.R
import de.bikebean.app.ui.drawer.preferences.VersionJsonParser.AppRelease

class NewVersionDialog (
        private val act: Activity,
        private val release: AppRelease,
        private val newVersionDownloadHandler: (String) -> Unit
) : DialogFragment() {

    private val message by lazy {
        act.getString(R.string.message_update_1) +
                release.name + act.getString(R.string.message_update_2)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(act).apply {
            setTitle(R.string.title_update)
            setMessage(message)
            setPositiveButton(R.string.button_download) { _, _ -> newVersionDownloadHandler(release.url) }
            setNegativeButton(R.string.button_update_cancel) { _, _ -> }
        }.create()
}