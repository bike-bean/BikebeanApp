package de.bikebean.app.ui.initialization

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import de.bikebean.app.MainActivity
import de.bikebean.app.R
import de.bikebean.app.db.sms.Sms.MESSAGE
import de.bikebean.app.db.state.State
import de.bikebean.app.db.state.StateFactory
import de.bikebean.app.db.type.types.Initial
import de.bikebean.app.ui.drawer.log.LogViewModel
import de.bikebean.app.ui.drawer.sms_history.SmsViewModel
import de.bikebean.app.ui.drawer.status.StateViewModel
import de.bikebean.app.ui.drawer.status.insert
import de.bikebean.app.ui.utils.permissions.PermissionUtils
import de.bikebean.app.ui.utils.permissions.PermissionUtils.checkResult
import de.bikebean.app.ui.utils.permissions.PermissionUtils.hasSmsPermissions
import de.bikebean.app.ui.utils.sms.listen.SmsListener
import de.bikebean.app.ui.utils.sms.send.SmsSender
import de.bikebean.app.ui.initialization.views.InitialItemView
import de.bikebean.app.ui.initialization.views.InitialItemViewList
import de.bikebean.app.ui.utils.resource.ResourceUtils.getIntervalString
import de.bikebean.app.ui.utils.preferences.PreferencesUtils as PrefUtils

class InitialConfigurationFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_initial_configuration, container, false)
    }

    private var preferences: SharedPreferences? = null

    private var initialItemList: InitialItemViewList? = null
    private var progressBar: LinearLayout? = null

    private var permissionGrantedHandler: (() -> Unit)? = null
    private var permissionDeniedHandler: ((Boolean) -> Unit)? = null

    private var sm: SmsViewModel? = null
    private var st: StateViewModel? = null
    private var lv: LogViewModel? = null

    override fun onViewCreated(view: View,
                               savedInstanceState: Bundle?) {
        val subTitle = view.findViewById<TextView>(R.id.subTitle)
        subTitle.setText(R.string.initial_configuration_subtitle)

        preferences = getDefaultSharedPreferences(context)

        progressBar = view.findViewById(R.id.progressBar2)
        initialItemList = view.findViewById(R.id.initialItemList)

        initialItemList?.setOnClickListenerCallback(0, ::finalizePhoneNumber)
        initialItemList?.setOnClickListenerCallback(1, ::getPermissions)
        initialItemList?.setOnClickListenerCallback(2, ::sendWarningNumber)
        initialItemList?.setOnClickListenerSkipCallback(2, ::skip)
        initialItemList?.setOnClickListenerCallback(3, ::setInterval)
        initialItemList?.setOnClickListenerSkipCallback(3, ::skip)
        initialItemList?.setOnClickListenerCallback(4, ::getPosition)
        initialItemList?.setOnClickListenerSkipCallback(4, ::skip)
        initialItemList?.setOnClickListenerCallback(5, ::finish)

        permissionGrantedHandler = ::fetchSms
        permissionDeniedHandler = null

        View.GONE.let {
            progressBar?.visibility = it
        }

        sm = ViewModelProvider(this).get(SmsViewModel::class.java)
        st = ViewModelProvider(this).get(StateViewModel::class.java)
        lv = ViewModelProvider(this).get(LogViewModel::class.java)

        updateChecked()
    }

    private fun skip(initialItemView: InitialItemView) =
            initialItemList?.skip(initialItemView).also {
                updateChecked()
            } ?: Unit

    private fun skip(position: Int) =
            initialItemList?.skip(position)

    private fun updateChecked() = st?.let {
        initialItemList?.updateChecked(it)
    }

    private fun fetchSms() {
        val number: String = PrefUtils.getBikeBeanNumber(preferences, lv) ?: run {
            lv?.w("Could not perform initial loading because phone number is not set!")
            return
        }

        progressBar?.visibility = View.VISIBLE

        sm?.fetchSmsSync(context, st, lv, number)
        updateChecked()

        progressBar?.visibility = View.GONE
    }

    private fun sendWarningNumber() = sendSms(MESSAGE.WARNING_NUMBER, listOf(
            StateFactory.createPendingState(State.KEY.WARNING_NUMBER, 0.0)),
            ::onPostWarningNumber
    )

    private fun setInterval() =
            (initialItemList?.getAdditionalElements(3) as Spinner).let { spinner: Spinner ->
                getIntervalString(spinner.selectedItemPosition, requireContext()).toInt().let { interval ->
                    with(MESSAGE.INT) {
                        setValue("Int $interval")

                        sendSms(this, listOf(StateFactory.createPendingState(
                                State.KEY.INTERVAL, interval.toDouble())),
                                ::onPostInterval
                        )
                    }
                }
            }

    private fun finalizePhoneNumber() = insert(st, Initial()).also {
        updateChecked()
    }

    private fun getPosition() = sendSms(MESSAGE.WAPP, listOf(
            StateFactory.createPendingState(State.KEY.LOCATION, 0.0),
            StateFactory.createPendingState(State.KEY.CELL_TOWERS, 0.0),
            StateFactory.createPendingState(State.KEY.WIFI_ACCESS_POINTS, 0.0)),
            ::onPostPosition
    )

    private fun finish() {
        PrefUtils.setInitStateDone(preferences)

        Intent(context, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(SmsListener.newSmsString, 1)
                .let { context?.startActivity(it) }
    }

    private fun getPermissions() = when {
        hasSmsPermissions(this) -> {
            permissionGrantedHandler?.invoke()
            permissionDeniedHandler?.invoke(false)
        }
        else -> null
    } ?: Unit

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String?>,
            grantResults: IntArray) {
        if (requestCode == PermissionUtils.KEYS.SMS.ordinal) {
            when {
                checkResult(grantResults) -> {
                    permissionDeniedHandler?.invoke(false)
                    permissionGrantedHandler?.invoke()
                }
                else -> permissionDeniedHandler?.invoke(true)
            }
        }
    }

    private fun sendSms(
            message: MESSAGE,
            updates: List<State>,
            onPostSend: (Boolean, SmsSender) -> Unit) =
            SmsSender(
                    message, updates, (requireActivity() as AppCompatActivity), onPostSend, lv
            ).showDialogBeforeSend()

    private fun onPostWarningNumber(sent: Boolean, smsSender: SmsSender) {
        onPostSend(sent, smsSender, 2)
    }

    private fun onPostInterval(sent: Boolean, smsSender: SmsSender) {
        onPostSend(sent, smsSender, 3)
    }

    private fun onPostPosition(sent: Boolean, smsSender: SmsSender) {
        onPostSend(sent, smsSender, 4)
    }

    private fun onPostSend(sent: Boolean, smsSender: SmsSender, position: Int) = when {
        sent -> {
            sm?.insert(smsSender, lv)
            st?.insert(smsSender)
            skip(position)
        }
        else -> null
    }.also { updateChecked() }

    /*
     * TODO:
     *   - Map: Adapt route and share intents to non-googly maps
     *   - Map: Fix "Jumping" behaviour when clicking the marker
     *   - Map: Show Fab only when location is not visible on the map
     *   - Map: Provide Zoom Controls
     *   - Map: Provide Device's Location
     *   - ...
     *   - UI: Do make buttons available if another query is running, but maybe indicate that in
     *         an extra dialog prior to sending the sms (or even in the same...)
     *   - UI: Provide a shortcut to the position history (from location status view) and
     *         to battery history (from battery status view)
     *   - UI: Make WLAN switched On more annoying ("warning" icons, orange background etc.)
     *   - UI: Help Snackbar Texts are rather short-lived and, more importantly, only 2 lines
     *         long! Make better!
     *   - UI: WifiLoc-Fragment should inform about the Wifi Status and react accordingly
     *   - ...
     *   - Code: More Kotlin (Next: MainActivity split)
     *   - Code: API 19: WHAT is with the Fabs??
     *   - Code: Review/Rewrite spacing/dimens into more meaningful names/values
     *   - Code: Get Rid of MapFragmentHelper and MapFragmentOld
     *   - ...
     *   - Postponed: Design: Make BottomSheet have rounded corners and an image indicating
     *                        draw-ability instead of main app bar.)
     *   - Postponed: Design: Set peek height of bottomSheet to just the height of the title
     *   - Postponed: Init: Provide some meaningful images or stuff like that (one for each item))
     *   - Postponed: UI: Progressbar is constantly switching between two states when two commands
     *                    are issued (e.g. warning number AND interval)
     *
     */
}