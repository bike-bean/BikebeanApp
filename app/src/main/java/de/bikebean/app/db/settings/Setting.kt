package de.bikebean.app.db.settings

import de.bikebean.app.db.sms.Sms
import de.bikebean.app.db.sms.SmsFactory
import de.bikebean.app.db.state.State
import de.bikebean.app.db.state.State.KEY

abstract class Setting(
        val state: State,
        val conversationListAdder: (MutableList<Setting>, Setting) -> Unit) {

    val sms: Sms = SmsFactory.createSmsFromState(state)

    companion object {

        fun addToList(
                conversationList: MutableList<Setting>,
                setting: Setting) {
            conversationList.add(setting)
        }

        fun replaceIfNewer(
                conversationList: MutableList<Setting>,
                settingToAdd: Setting) {
            conversationList
                    .firstOrNull(settingToAdd::equalsKeyAndIsNewer)
                    ?.let(conversationList::remove)
                    ?.also { conversationList.add(settingToAdd) }
        }
    }

    /* utils */
    private fun equalsKeyAndIsNewer(other: Setting): Boolean =
            equalsKey(other) && isNewerThan(other)

    private fun equalsKey(other: Setting): Boolean = key == other.key

    private fun isNewerThan(other: Setting): Boolean = sms.timestamp > other.sms.timestamp

    private val key: KEY = KEY.getValue(state)
}