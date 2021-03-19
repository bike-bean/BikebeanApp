package de.bikebean.app.ui.drawer.preferences

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import de.bikebean.app.R
import de.bikebean.app.ui.drawer.preferences.VersionJsonParser.AppRelease

class NewVersionDialog (
        private val act: Activity,
        private val release: AppRelease,
        private val newVersionDownloadHandler: (String) -> Unit,
        private val newVersionDownloadCanceller: () -> Unit
) : DialogFragment() {

    private val message by lazy {
        act.getString(R.string.update_text_1) +
                release.name + act.getString(R.string.update_text_2)
    }

    private val positiveButtonListener: (DialogInterface, Int) -> Unit =
            { _, _ -> newVersionDownloadHandler(release.url) }
    private val negativeButtonListener: (DialogInterface, Int) -> Unit =
            { _, _ -> newVersionDownloadCanceller() }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(act).apply {
            setTitle(R.string.update_title)
            setMessage(message)
            setPositiveButton(R.string.continue_update, positiveButtonListener)
            setNegativeButton(R.string.cancel_update, negativeButtonListener)
        }.create()
}