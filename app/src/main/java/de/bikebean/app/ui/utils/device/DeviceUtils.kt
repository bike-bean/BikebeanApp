package de.bikebean.app.ui.utils.device

import android.content.Context
import androidx.preference.PreferenceManager
import de.bikebean.app.BuildConfig
import de.bikebean.app.ui.drawer.preferences.SettingsFragment
import java.util.*

object DeviceUtils {

    @JvmStatic
    val versionName: String
        get() = BuildConfig.VERSION_NAME

    @JvmStatic
    @Synchronized
    fun getUUID(context: Context): String {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPrefs ?: return UUID.randomUUID().toString()

        val uniqueID = sharedPrefs.getString(SettingsFragment.PREF_UNIQUE_ID, null)

        uniqueID ?: run{
            sharedPrefs.edit()
                    .putString(SettingsFragment.PREF_UNIQUE_ID, uniqueID)
                    .apply()

            return UUID.randomUUID().toString()
        }

        return uniqueID
    }
}