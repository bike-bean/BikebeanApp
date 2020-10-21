package de.bikebean.app.ui.utils.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import de.bikebean.app.ui.drawer.log.LogViewModel
import de.bikebean.app.ui.drawer.preferences.SettingsFragment

object PreferencesUtils {

    @JvmStatic
    fun getBikeBeanNumber(
            sharedPreferences: SharedPreferences,
            logViewModel: LogViewModel): String? {
        val number = sharedPreferences.getString(SettingsFragment.NUMBER_PREFERENCE, null)

        number ?: run {
            sharedPreferences.edit()
                    .putInt(SettingsFragment.INIT_STATE_PREFERENCE, SettingsFragment.INIT_STATE.NEW.ordinal)
                    .apply()
            logViewModel.e("Failed to load BB-number! Maybe it's not set?")
        }

        return number
    }

    @JvmStatic
    fun getBikeBeanNumber(context: Context, logViewModel: LogViewModel): String? {
        return getBikeBeanNumber(
                PreferenceManager.getDefaultSharedPreferences(context),
                logViewModel
        )
    }

    fun getBikeBeanNumber(context: Context): String? {
        val number = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(SettingsFragment.NUMBER_PREFERENCE, null)

        number ?: PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(SettingsFragment.INIT_STATE_PREFERENCE, SettingsFragment.INIT_STATE.NEW.ordinal)
                .apply()

        return number
    }

    @JvmStatic
    fun setInitStateDone(sharedPreferences: SharedPreferences) {
        sharedPreferences.edit()
                .putInt(SettingsFragment.INIT_STATE_PREFERENCE, SettingsFragment.INIT_STATE.DONE.ordinal)
                .apply()
    }

    @JvmStatic
    @SuppressLint("ApplySharedPref")
    fun setInitStateAddress(sharedPreferences: SharedPreferences, number: String) {
        sharedPreferences.edit()
                .putString(SettingsFragment.NUMBER_PREFERENCE, number)
                .putInt(SettingsFragment.INIT_STATE_PREFERENCE, SettingsFragment.INIT_STATE.ADDRESS.ordinal)
                .commit()
    }

    @JvmStatic
    fun getInitState(sharedPreferences: SharedPreferences): Int {
        val initState = sharedPreferences.getInt(SettingsFragment.INIT_STATE_PREFERENCE, Int.MAX_VALUE)

        if (initState == Int.MAX_VALUE) {
            sharedPreferences.edit()
                    .putInt(SettingsFragment.INIT_STATE_PREFERENCE, SettingsFragment.INIT_STATE.NEW.ordinal)
                    .apply()

            return SettingsFragment.INIT_STATE.NEW.ordinal
        }

        return initState
    }

    @JvmStatic
    fun isInitDone(context: Context): Boolean {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getInt(SettingsFragment.INIT_STATE_PREFERENCE, Int.MAX_VALUE) ==
                SettingsFragment.INIT_STATE.DONE.ordinal
    }
}