package de.bikebean.app.db.settings.settings

import de.bikebean.app.db.settings.Setting
import de.bikebean.app.db.state.State

abstract class ReplaceIfNewerSetting(state: State) : Setting(state, Setting::replaceIfNewer)