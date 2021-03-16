package de.bikebean.app.ui.drawer.status

import androidx.appcompat.app.AppCompatActivity
import de.bikebean.app.db.sms.Sms.MESSAGE
import de.bikebean.app.db.state.State
import de.bikebean.app.ui.utils.sms.send.SmsSender

fun SubStatusFragmentSmall.sendSms(message: MESSAGE, updates: Array<State>) {
    SmsSender(
            message, updates.toList(),
            (requireActivity() as AppCompatActivity), ::onPostSend, lv
    ).showDialogBeforeSend()
}
