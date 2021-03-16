package de.bikebean.app.ui.initialization

import de.bikebean.app.db.settings.Setting
import de.bikebean.app.db.sms.Sms
import de.bikebean.app.db.type.types.InitialConversation
import de.bikebean.app.db.type.types.ParserType
import de.bikebean.app.ui.drawer.log.LogViewModel
import de.bikebean.app.ui.drawer.sms_history.SmsViewModel
import de.bikebean.app.ui.drawer.status.StateViewModel
import de.bikebean.app.ui.utils.sms.parser.SmsParser

class Conversation(
        private val stateViewModel: StateViewModel,
        private val smsViewModel: SmsViewModel,
        private val logViewModel: LogViewModel) {

    private val settings: MutableList<Setting> = InitialConversation().settings.toMutableList()

    fun add(sms: Sms) {
        SmsParser(sms, null, null, logViewModel).types.forEach { type: ParserType ->
            type.addToConversationList(settings)
        }
        smsViewModel.insert(sms)
    }

    fun updatePreferences() {
        logViewModel.d("Inserting ${settings.size} Settings")
        stateViewModel.insert(settings)
        logViewModel.d("Done inserting Settings!")
    }

}