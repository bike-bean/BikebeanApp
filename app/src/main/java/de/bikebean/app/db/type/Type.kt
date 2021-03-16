package de.bikebean.app.db.type

import de.bikebean.app.db.settings.Setting

abstract class Type {

    abstract val settings: List<Setting>

}