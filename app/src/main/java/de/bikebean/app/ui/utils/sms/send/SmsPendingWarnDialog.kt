package de.bikebean.app.ui.utils.sms.send

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import de.bikebean.app.R

class SmsPendingWarnDialog(
        private val act: Activity,
        private val smsSender: SmsSender,
        ) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            AlertDialog.Builder(act).apply {
                setMessage(R.string.message_sms_pending)
                setPositiveButton(R.string.button_continue) { _, _ -> smsSender.showWarnDialog() }
                setNegativeButton(R.string.button_cancel) { _, _ -> smsSender.cancelSend() }
            }.create()

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        smsSender.cancelSend()
    }

}