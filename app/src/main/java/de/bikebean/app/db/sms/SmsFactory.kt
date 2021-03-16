package de.bikebean.app.db.sms

import android.database.Cursor
import android.provider.Telephony
import de.bikebean.app.db.state.State
import de.bikebean.app.ui.initialization.Conversation
import de.bikebean.app.ui.utils.date.DateUtils.convertToTime
import de.bikebean.app.ui.utils.sms.send.SmsSender

object SmsFactory {

    @JvmStatic
    fun createNewSentSms(
            smsId: Int,
            smsSender: SmsSender
    ): Sms = System.currentTimeMillis().let {
        Sms(
                smsId - 1,
                smsSender.address,
                smsSender.message.msg,
                Telephony.Sms.MESSAGE_TYPE_SENT,
                Sms.STATUS.NEW.ordinal,
                convertToTime(it),
                it
        )
    }

    fun createSmsFromState(
            state: State
    ): Sms = Sms(
            state.smsId,
            "",
            "",
            0,
            0,
            convertToTime(state.timestamp),
            state.timestamp
    )

    fun createSmsFromCursor(
            inbox: Cursor,
            conversation: Conversation?
    ): Sms = inbox.getString(inbox.getColumnIndexOrThrow("date")).toLong().let {
        Sms(
                inbox.getString(inbox.getColumnIndexOrThrow("_id")).toInt(),
                inbox.getString(inbox.getColumnIndexOrThrow("address")),
                inbox.getString(inbox.getColumnIndexOrThrow("body")),
                inbox.getString(inbox.getColumnIndexOrThrow("type")).toInt(),
                (conversation?.let { Sms.STATUS.PARSED } ?: Sms.STATUS.NEW).ordinal,
                convertToTime(it),
                it
        )
    }

    @JvmStatic
    fun createNullSms(): Sms = Sms(
            0,
            "",
            "",
            0,
            0,
            "",
            0
    )
}