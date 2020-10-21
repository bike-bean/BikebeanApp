package de.bikebean.app.ui.utils.sms.listen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import de.bikebean.app.MainActivity
import de.bikebean.app.ui.utils.preferences.PreferencesUtils

class SmsListener : BroadcastReceiver() {

    /*
     SMS Listener

     This class listens for new SMS and calls
     back to MainActivity to handle them
     */
    companion object {
        @JvmStatic
        val newSmsString = "NEW_SMS"
    }

    override fun onReceive(ctx: Context, intent: Intent) {
        /*
         When a new message arrives, just trigger the MainActivity
         */

        val number = PreferencesUtils.getBikeBeanNumber(ctx) ?: return
        isIncomingSms(intent.action) ?: return
        isFromOurBikeBean(intent, number) ?: return

        val backIntent = Intent(ctx, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(newSmsString, 1)

        ctx.startActivity(backIntent)
    }

    private fun isIncomingSms(action: String?): Unit? {
        if (action != null && action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
            return Unit

        return null
    }

    private fun isFromOurBikeBean(intent: Intent, number: String): Unit? {
        if (Telephony.Sms.Intents.getMessagesFromIntent(intent).any {
            it.originatingAddress != null && it.originatingAddress.equals(number)
        })
            return Unit

        return null
    }
}