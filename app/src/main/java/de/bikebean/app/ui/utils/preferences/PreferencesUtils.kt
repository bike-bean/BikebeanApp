package de.bikebean.app.ui.utils.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import de.bikebean.app.ui.drawer.log.LogViewModel
import de.bikebean.app.ui.drawer.preferences.SettingsFragment.InitState.*
import de.bikebean.app.ui.drawer.preferences.SettingsFragment as SF

object PreferencesUtils {

    @JvmStatic
    fun getBikeBeanNumber(sharedPreferences: SharedPreferences?,
                          logViewModel: LogViewModel?): String? =
            when (sharedPreferences) {
                null -> null
                else -> {
                    sharedPreferences.getString(SF.NUMBER_PREFERENCE, null).also {
                        it ?: run {
                            resetInitState(sharedPreferences)
                            logViewModel?.e("Failed to load BB-number! Maybe it's not set?")
                        }
                    }
                }
            }

    @JvmStatic
    fun getBikeBeanNumber(context: Context, logViewModel: LogViewModel?): String? =
            getBikeBeanNumber(
                    PreferenceManager.getDefaultSharedPreferences(context),
                    logViewModel
            )

    fun getBikeBeanNumber(context: Context): String? = getBikeBeanNumber(context, null)

    @JvmStatic
    @SuppressLint("ApplySharedPref")
    fun setInitStateAddress(sharedPreferences: SharedPreferences?, number: String) : Boolean? =
            sharedPreferences?.edit()
                    ?.putString(SF.NUMBER_PREFERENCE, number)
                    ?.commit()

    private fun resetInitState(sharedPreferences: SharedPreferences) = sharedPreferences
            .edit()
            .putInt(SF.INIT_STATE_PREFERENCE, NEW.ordinal)
            .apply()

    @SuppressLint("ApplySharedPref")
    fun setInitStateDone(sharedPreferences: SharedPreferences?) = sharedPreferences
            ?.edit()
            ?.putInt(SF.INIT_STATE_PREFERENCE, DONE.ordinal)
            ?.commit()

    @JvmStatic
    fun isInitDone(context: Context): Boolean = PreferenceManager
            .getDefaultSharedPreferences(context)
            .getInt(SF.INIT_STATE_PREFERENCE, Int.MAX_VALUE) == DONE.ordinal
}