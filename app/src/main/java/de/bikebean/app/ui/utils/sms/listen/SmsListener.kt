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

        // Exit if the intent is something else
        isIncomingSms(intent.action) ?: return

        (PreferencesUtils.getBikeBeanNumber(ctx) ?: return).let { number ->
            // Exit if the message is NOT from our bikeBean
            isFromOurBikeBean(intent, number) ?: return
        }

        // Trigger the MainActivity for a new message
        Intent(ctx, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(newSmsString, 1)
                .let(ctx::startActivity)
    }

    private fun isIncomingSms(action: String?): Unit? = when {
        action != null && action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION -> Unit
        else -> null
    }

    private fun isFromOurBikeBean(intent: Intent, number: String): Unit? = when {
        Telephony.Sms.Intents.getMessagesFromIntent(intent).any {
            it.originatingAddress != null && it.originatingAddress.equals(number)
        } -> Unit
        else -> null
    }
}