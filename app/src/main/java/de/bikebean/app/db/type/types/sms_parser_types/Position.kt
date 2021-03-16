package de.bikebean.app.db.type.types.sms_parser_types

import de.bikebean.app.db.settings.Setting
import de.bikebean.app.db.sms.Sms
import de.bikebean.app.db.type.types.ParserPatterns.positionPattern
import de.bikebean.app.db.type.types.ParserPatterns.statusBatteryStatusPattern
import de.bikebean.app.db.type.types.ParserType
import de.bikebean.app.ui.drawer.log.LogViewModel
import java.lang.ref.WeakReference

class Position(sms: Sms, lv: WeakReference<LogViewModel>) : ParserType(TYPE.POSITION, sms, lv) {

    init {
        positionMatcher = positionPattern.matcher(sms.body)
        statusBatteryStatusMatcher = statusBatteryStatusPattern.matcher(sms.body)
    }

    override val matchers = listOf(
            positionMatcher,
            statusBatteryStatusMatcher
    )

    override val settings: List<Setting> = emptyList()

}