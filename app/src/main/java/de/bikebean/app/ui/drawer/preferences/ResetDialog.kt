package de.bikebean.app.ui.drawer.preferences

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import de.bikebean.app.R

class ResetDialog (
        private val act: Activity,
        private val address: String,
        private val resetHandler: (String) -> Unit,
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            AlertDialog.Builder(act).apply {
                setTitle(R.string.pref_db_reset)
                setMessage(R.string.message_db_reset)
                setPositiveButton(R.string.button_reset) { _, _ -> resetHandler(address) }
                setNegativeButton(R.string.button_cancel) { _, _ -> }
            }.create()
}