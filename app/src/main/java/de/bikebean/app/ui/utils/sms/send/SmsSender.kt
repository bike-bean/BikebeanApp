package de.bikebean.app.ui.utils.sms.send

import android.telephony.SmsManager
import androidx.appcompat.app.AppCompatActivity
import de.bikebean.app.MainActivity
import de.bikebean.app.MainActivity.PermissionGrantedHandler
import de.bikebean.app.db.sms.Sms.MESSAGE
import de.bikebean.app.db.state.State
import de.bikebean.app.ui.drawer.log.LogViewModel
import de.bikebean.app.ui.utils.permissions.PermissionUtils.hasSmsPermissions
import de.bikebean.app.ui.utils.preferences.PreferencesUtils.getBikeBeanNumber

class SmsSender(
        val message: MESSAGE,
        val updates: List<State>,
        private val act: AppCompatActivity,
        private val postSmsSendHandler: (Boolean, SmsSender) -> Unit,
        lv: LogViewModel?,
        private val isLocationPending: Boolean = false,
        private val isBatteryPending: Boolean = false) {

    val address: String = getBikeBeanNumber(act, lv) ?: ""

    fun showDialogBeforeSend() = when {
        address.isEmpty() -> cancelSend()
        isLocationPending or isBatteryPending -> showPendingDialog()
        else -> showWarnDialog()
    }

    private fun showPendingDialog() = with(SmsPendingWarnDialog(act, this)) {
        if ((dialog ?: onCreateDialog(null)).isShowing) {
            cancelSend()
            return
        }

        show(act.supportFragmentManager, "smsWarning")
    }

    fun showWarnDialog() = with(SmsSendWarnDialog(act, this)) {
        if ((dialog ?: onCreateDialog(null)).isShowing) {
            cancelSend()
            return
        }

        show(act.supportFragmentManager, "smsWarning")
    }

    val permissions: Unit
        get() {
            MainActivity.permissionGrantedHandler = PermissionGrantedHandler { send() }
            if (hasSmsPermissions(act)) {
                MainActivity.permissionDeniedHandler.continueWithoutPermission(false)
                MainActivity.permissionGrantedHandler.continueWithPermission()
            } else cancelSend()
        }

    /*
    The user decided to send the SMS, so actually send it!
    */
    private fun send() = SmsManager.getDefault()?.sendTextMessage(
            address, null, message.msg,
            null, null
    ).also {
        postSmsSendHandler(true, this)
    }

    /*
     * The user decided to cancel, don't send an SMS and signal it to the user.
     */
    fun cancelSend() = postSmsSendHandler(false, this)

}