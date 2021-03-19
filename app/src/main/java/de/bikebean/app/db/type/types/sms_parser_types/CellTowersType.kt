package de.bikebean.app.db.type.types.sms_parser_types

import de.bikebean.app.db.settings.settings.add_to_list_settings.Wapp
import de.bikebean.app.db.settings.settings.add_to_list_settings.number_settings.CellTowers
import de.bikebean.app.db.sms.Sms
import de.bikebean.app.db.state.State
import de.bikebean.app.db.type.types.ParserPatterns.positionPattern
import de.bikebean.app.db.type.types.ParserType
import de.bikebean.app.ui.drawer.log.LogViewModel
import java.lang.ref.WeakReference

class CellTowersType(
        sms: Sms, lv: WeakReference<LogViewModel>
) : ParserType(TYPE.CELL_TOWERS, sms, lv) {

    override val matchers = listOf(
            positionPattern.matcher(sms.body)
    )

    override val settings get() = listOf(
            /* no battery entry in this special case */
            CellTowers(sms, matchers[0].groupStrings()),
            Wapp(sms, State.WAPP_CELL_TOWERS)
    )
}