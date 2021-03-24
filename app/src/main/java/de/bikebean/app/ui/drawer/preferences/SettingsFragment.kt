package de.bikebean.app.ui.drawer.preferences

import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.lifecycle.ViewModelProvider
import androidx.preference.*
import androidx.preference.EditTextPreference.OnBindEditTextListener
import com.google.android.material.snackbar.Snackbar
import de.bikebean.app.R
import de.bikebean.app.db.type.types.Initial
import de.bikebean.app.ui.drawer.log.LogViewModel
import de.bikebean.app.ui.drawer.sms_history.SmsViewModel
import de.bikebean.app.ui.drawer.status.StateViewModel
import de.bikebean.app.ui.drawer.status.insert
import de.bikebean.app.ui.utils.Utils.eliminateSpaces
import de.bikebean.app.ui.utils.Utils.getErrorString
import de.bikebean.app.ui.utils.preferences.PreferencesUtils.getBikeBeanNumber

class SettingsFragment : PreferenceFragmentCompat() {

    private var stateViewModel: StateViewModel? = null
    private var smsViewModel: SmsViewModel? = null
    var logViewModel: LogViewModel? = null

    companion object {
        const val RESET_PREFERENCE = "reset"
        const val NAME_PREFERENCE = "name"
        const val NUMBER_PREFERENCE = "number"
        const val MAP_TYPE_PREFERENCE = "mapType"
        const val THEME_PREFERENCE = "theme"
        const val INIT_STATE_PREFERENCE = "initState"
        const val PREF_UNIQUE_ID = "DEVICE_UUID"
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        stateViewModel = ViewModelProvider(this).get(StateViewModel::class.java)
        smsViewModel = ViewModelProvider(this).get(SmsViewModel::class.java)
        logViewModel = ViewModelProvider(this).get(LogViewModel::class.java)

        /*
         Preferences
         */
        findPreference<EditTextPreference>(NUMBER_PREFERENCE)?.apply {
            setOnBindEditTextListener(numberEditTextListener)
            setDialogMessage(R.string.message_pref_number)
            onPreferenceChangeListener = numberChangeListener
        }

        findPreference<Preference>(RESET_PREFERENCE)?.apply {
            createResetDialog(getBikeBeanNumber(requireContext(), logViewModel))?.let { resetDialog ->
                onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    resetDialog.show(requireActivity().supportFragmentManager, "resetDialog")
                    true
                }
            } ?: run { isEnabled = false }
        }

        findPreference<ListPreference>(THEME_PREFERENCE)?.apply {
            onPreferenceChangeListener = themeChangeListener
        }
    }

    private val numberEditTextListener = OnBindEditTextListener {
        text: EditText -> text.inputType = InputType.TYPE_CLASS_PHONE
    }

    private val numberChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
        getErrorString(newValue.toString())?.let {
            Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).show()

            when (it) {
                R.string.message_pref_number_no_blanks -> {
                    findPreference<EditTextPreference>(NUMBER_PREFERENCE)?.run {
                        text = eliminateSpaces(newValue.toString())
                    } ?: logViewModel!!.e("Failed to load BB-number! Maybe it's not set?")
                }
            }

            false
        } ?: run {
            resetAll(newValue.toString())

            true
        }
    }

    private val themeChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
        when ((newValue as String).toInt()) {
            1 -> setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
            2 -> setDefaultNightMode(MODE_NIGHT_NO)
            3 -> setDefaultNightMode(MODE_NIGHT_YES)
        }
        true
    }

    private fun createResetDialog(address: String?): ResetDialog? = address?.let {
        ResetDialog(requireActivity(), address, ::resetAll)
    }

    private fun resetAll(address: String) {
        // reset DB and repopulate it
        de.bikebean.app.db.resetAll()
        insert(stateViewModel, Initial())
        smsViewModel?.fetchSmsSync(requireContext(), stateViewModel, logViewModel, address)
        Snackbar.make(
                requireView(),
                R.string.toast_db_reset,
                Snackbar.LENGTH_LONG
        ).show()
    }

    enum class InitState {
        NEW, DONE
    }

}

