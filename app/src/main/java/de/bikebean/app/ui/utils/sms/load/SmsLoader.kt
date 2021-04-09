package de.bikebean.app.ui.utils.sms.load

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.Telephony
import de.bikebean.app.db.sms.Sms
import de.bikebean.app.db.sms.SmsFactory
import de.bikebean.app.ui.drawer.log.LogViewModel
import de.bikebean.app.ui.drawer.sms_history.SmsViewModel
import de.bikebean.app.ui.drawer.status.StateViewModel
import de.bikebean.app.ui.initialization.Conversation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class SmsLoader(
        private val contextWeakReference: WeakReference<Context?>,
        private val smsViewModel: SmsViewModel?,
        private val stateViewModel: StateViewModel?,
        private val logViewModel: LogViewModel?) {

    constructor(context: Context?,
                smsViewModel: SmsViewModel?,
                stateViewModel: StateViewModel?,
                logViewModel: LogViewModel?) :
            this(WeakReference(context), smsViewModel, stateViewModel, logViewModel)

    constructor(context: Context) :
            this(WeakReference(context),null, null, null)

    fun execute(address: String) {
        CoroutineScope(Dispatchers.Main).launch {
            doInBackground(address)
        }
    }

    private suspend fun doInBackground(address: String) =
            withContext(Dispatchers.IO) {
                getCursor(address)?.let {
                    traverseInboxAndClose(it, ::insertSmsIfNotPresent, null)
                }
            }

    fun loadInitial(phoneNumber: String) {
        val conversation = Conversation(stateViewModel!!, smsViewModel!!, logViewModel!!)

        logViewModel.i("Initial Loading")
        getCursor(phoneNumber)?.let { traverseInboxAndClose(it, conversation::add, conversation) }
        logViewModel.i("Initial Loading completed")
    }

    fun getMessageNumber(phoneNumber: String): Int = getCursor(phoneNumber)?.count ?: 0

    private fun getCursor(phoneNumber: String): Cursor? = contextWeakReference.get()?.let { context ->
        context.contentResolver?.let { contentResolver ->
            getInbox(contentResolver, arrayOf(phoneNumber)) ?: null.also {
                logViewModel?.w("Failed to load SMS, could not get cursor")
            }
        } ?: null.also {
            logViewModel?.w("Failed to load SMS, could not get contentResolver!")
        }
    } ?: null.also {
        logViewModel?.w("Failed to load SMS, context was removed!")
    }

    private fun getInbox(contentResolver: ContentResolver, argList: Array<String>): Cursor? =
            contentResolver.query(
                    Telephony.Sms.Inbox.CONTENT_URI, null,
                    "address=?", argList,
                    null, null
            )

    private fun traverseInboxAndClose(
            inbox: Cursor,
            insertHandler: (Sms) -> Unit,
            conversation: Conversation?) {

        if (inbox.moveToFirst()) {
            conversation?.let { logViewModel?.d("Loading ${inbox.count} SMS") }

            repeat(inbox.count) {
                SmsFactory.createSmsFromCursor(inbox, conversation).let(insertHandler)
                inbox.moveToNext()
            }

            conversation?.let { logViewModel?.d("Done Loading SMS!") }
            inbox.close()
        }

        conversation?.updatePreferences()
    }

    private fun insertSmsIfNotPresent(sms: Sms) {
        smsViewModel ?: return
        when {
            smsViewModel.getSmsById(sms.id).isEmpty() -> sms
            else -> null
        }?.let(smsViewModel::insert)
    }
}