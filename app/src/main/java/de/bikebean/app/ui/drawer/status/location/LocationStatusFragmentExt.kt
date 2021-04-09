package de.bikebean.app.ui.drawer.status.location

import de.bikebean.app.db.settings.settings.add_to_list_settings.WappState

// cached copy of parsed SMS
private val parsedSms: MutableList<Int> = mutableListOf()

fun isAlreadyParsed(wappState: WappState): Boolean =
    with(listOf(
            wappState.cellTowers.id,
            wappState.wifiAccessPoints.id
    )) {
        when {
            any(parsedSms::contains) -> true
            else -> false.also { forEach{ parsedSms.add(it) } }
        }
    }

