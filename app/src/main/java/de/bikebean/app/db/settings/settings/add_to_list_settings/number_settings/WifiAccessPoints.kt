package de.bikebean.app.db.settings.settings.add_to_list_settings.number_settings

import de.bikebean.app.db.settings.settings.add_to_list_settings.NumberSetting
import de.bikebean.app.db.settings.settings.add_to_list_settings.WappState
import de.bikebean.app.db.sms.Sms
import de.bikebean.app.db.state.State
import de.bikebean.app.db.state.State.KEY
import de.bikebean.app.db.state.StateFactory
import kotlinx.serialization.Serializable

class WifiAccessPoints : NumberSetting {

    override val list: List<WifiAccessPoint>

    constructor(sms: Sms, wifiAccessPoints: String) :
            super(wifiAccessPoints, sms, key,
                    StateFactory.createNumberState(
                            sms, numberKey,
                            getNumberFromString(wifiAccessPoints).toDouble(), State.STATUS.CONFIRMED
                    )
            ) {
        list = WifiAccessPointListBuilder(wifiAccessPoints)
            }

    constructor(wappState: WappState) : this(wappState.sms, wappState.wifiAccessPoints.longValue)

    constructor() : super("", key, StateFactory.createNullState()) {
        list = WifiAccessPointListBuilder("")
    }

    class WifiAccessPointListBuilder(stringArrayWapp: String) : ArrayList<WifiAccessPoint>() {

        init {
            if (stringArrayWapp.isNotEmpty())
                stringArrayWapp.split("\n").forEach { s ->
                    add(WifiAccessPointBuilder(s).get())
                }
        }

        fun add(element: WifiAccessPoint?): Boolean {
            return when (element) {
                null -> false
                else -> super.add(element)
            }
        }

    }

    @Serializable
    data class WifiAccessPoint(
            val macAddress: String,
            val signalStrength: Int)
        : RawNumberSettings()

    class WifiAccessPointBuilder(s: String) {

        private val wifiAccessPoint: WifiAccessPoint? = when {
            s != "    " && s.isNotEmpty() ->
                WifiAccessPoint(
                        createMacAddress(s.substring(2)),
                        ("-" + s.substring(0, 2)).toInt())
            else -> null
        }

        fun get(): WifiAccessPoint? = wifiAccessPoint


        private fun createMacAddress(rawMacAddress: String): String = StringBuilder().let {
            splitIntoChunksRecursive(rawMacAddress, it)
            it.toString()
        }

        private fun splitIntoChunksRecursive(remainder: String, final: StringBuilder) : Unit =
                final.let {
                    it.append(remainder.substring(0 until 2))

                    if (remainder.length <= 2) return

                    it.append(":")
                    splitIntoChunksRecursive(remainder.substring(2), it)
                }
    }

    companion object {
        private val key = KEY.WIFI_ACCESS_POINTS
        private val numberKey = KEY.NO_WIFI_ACCESS_POINTS
    }
}