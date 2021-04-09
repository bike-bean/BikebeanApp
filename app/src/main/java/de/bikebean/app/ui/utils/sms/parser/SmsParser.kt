package de.bikebean.app.ui.utils.sms.parser

import de.bikebean.app.db.sms.Sms
import de.bikebean.app.db.type.types.ParserType
import de.bikebean.app.db.type.types.ParserTypeFactory
import de.bikebean.app.ui.drawer.log.LogViewModel
import de.bikebean.app.ui.drawer.sms_history.SmsViewModel
import de.bikebean.app.ui.drawer.status.StateViewModel
import de.bikebean.app.ui.drawer.status.insert
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class SmsParser(
        private val sms: Sms,
        st: StateViewModel?,
        sm: SmsViewModel?,
        lv: LogViewModel) {

    private val statusViewModelReference: WeakReference<StateViewModel> = WeakReference(st)
    private val smsViewModelReference: WeakReference<SmsViewModel> = WeakReference(sm)
    private val lv: WeakReference<LogViewModel> = WeakReference(lv)

    fun execute() {
        CoroutineScope(Dispatchers.Main).launch {
            when {
                doInBackground() -> smsViewModelReference.get()?.markParsed(sms)
            }
        }
    }

    private suspend fun doInBackground(): Boolean =
            withContext(Dispatchers.Default) {
                types.forEach {
                    lv.get()?.w("Detected Type ${it.type.ordinal} (${it.type.name})")
                    statusViewModelReference.get()?.let{ st ->
                        insert(st, it)
                    } ?: return@withContext false
                }

                true
            }

    val types: List<ParserType>
        get() = ParserTypeFactory.createList(sms, lv).apply {
            when {
                isEmpty() -> lv.get()?.w("Could not Parse SMS: ${sms.body}")
            }
        }

}


