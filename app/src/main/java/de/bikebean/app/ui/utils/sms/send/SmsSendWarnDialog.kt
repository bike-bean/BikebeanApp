package de.bikebean.app.ui.utils.sms.send

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import de.bikebean.app.R

class SmsSendWarnDialog(
        private val act: Activity,
        private val smsSender: SmsSender
        ) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        @SuppressLint("InflateParams")
        val v = act.layoutInflater.inflate(R.layout.dialog_warn_sms, null)

        setBubbleText(v)

        return AlertDialog.Builder(act).apply {
            setView(v)
            setMessage(R.string.message_sms_send)
            setPositiveButton(R.string.button_send_sms) { _, _ -> smsSender.permissions }
            setNegativeButton(R.string.button_cancel) { _, _ -> smsSender.cancelSend() }
        }.create()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        smsSender.cancelSend()
    }

    private fun setBubbleText(v: View) {
        val smsMessage = v.findViewById<TextView>(R.id.txtMsgYou2) ?: return
        val smsMessageDots = v.findViewById<TextView>(R.id.txtMsgFrom2) ?: return

        smsMessage.text = smsSender.message.msg
        smsMessageDots.text = "..."
    }
}