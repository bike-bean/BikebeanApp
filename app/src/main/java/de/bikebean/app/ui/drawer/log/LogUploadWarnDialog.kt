package de.bikebean.app.ui.drawer.log

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import de.bikebean.app.R

class LogUploadWarnDialog(
        private val act: Activity,
        private val generateReport: (String) -> Unit
        ) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        @SuppressLint("InflateParams")
        val v = act.layoutInflater.inflate(R.layout.dialog_upload_log, null)
        val editText : EditText = v.findViewById(R.id.errorDescription)

        return AlertDialog.Builder(act).apply {
            setView(v)
            setPositiveButton(R.string.button_send_sms) { _, _ -> generateReport(editText.text.toString()) }
            setNegativeButton(R.string.button_cancel) { _, _ -> }
        }.create()
    }

}