package de.bikebean.app.db.type.types

import de.bikebean.app.db.settings.settings.replace_if_newer_settings.Interval
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.Status
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.WarningNumber
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.Wifi
import de.bikebean.app.db.sms.SmsFactory
import de.bikebean.app.db.type.Type

class InitialConversation : Type() {

    override val settings = listOf(
            Interval(),
            Wifi(),
            Status(),
            WarningNumber(SmsFactory.createNullSms(), "")
    )
}